import ComposeApp
import CoreText
import UIKit

final class NativeChromeViewController: UIViewController, UISplitViewControllerDelegate, UITabBarDelegate, UIGestureRecognizerDelegate {
    private let splitController = UISplitViewController(style: .doubleColumn)
    private let tabBar = UITabBar()
    private lazy var offlineItem: UIBarButtonItem = {
        let item = UIBarButtonItem(
            image: UIImage(named: "material-cloud-off")?.withRenderingMode(.alwaysTemplate),
            style: .plain,
            target: nil,
            action: nil
        )
        item.isEnabled = false
        item.accessibilityLabel = "Offline"
        return item
    }()
    private lazy var composeBackItem = UIBarButtonItem(
        image: UIImage(systemName: "chevron.backward"),
        style: .plain,
        target: self,
        action: #selector(navigateComposeBack)
    )
    private lazy var composeBackGesture = UIScreenEdgePanGestureRecognizer(
        target: self,
        action: #selector(handleComposeBackGesture(_:))
    )
    private lazy var sidebarRevealGesture = UIScreenEdgePanGestureRecognizer(
        target: self,
        action: #selector(handleSidebarRevealGesture(_:))
    )
    private lazy var sidebarToggleItem = UIBarButtonItem(
        image: UIImage(systemName: "sidebar.left"),
        style: .plain,
        target: self,
        action: #selector(toggleAppSidebar)
    )
    private var composeController: UIViewController!
    private var contentHostController: ComposeScrollHostViewController!
    private var contentNavigationController: UINavigationController!
    private var sidebarController: UIViewController!
    private var easterEggOverlayController: UIViewController!
    private var bridge: NativeComponentBridge!
    private var selectedSection: AppSection = .home
    private var displaysMainContent = false
    private var usesSidebar = false
    private var hasRefreshedSidebarPresentation = false
    private var isSidebarCollapsed = false
    private var canNavigateBack = false
    private var isDarkTheme = false
    private var usesSystemAppearance = true
    private var bottomTabsVisible = false
    private lazy var primaryTabsContent = PrimaryTabsPaletteContentView(
        font: nativeBodyFont(ofSize: 17),
        horizontalCenter: { [weak self] contentView in
            self?.primaryTabsHorizontalCenter(in: contentView)
        },
        selectionChanged: { [weak self] index in
            self?.bridge.selectPrimaryTab(index: Int32(index))
        }
    )
    private var primaryTabsVisible = false
    private var primaryTabsAnimator: UIViewPropertyAnimator?
    private var primaryTabsInsetLink: CADisplayLink?

    init() {
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear
        registerBundledFont(named: "Sniglet-Regular", resource: "Sniglet-Regular")
        view.tintColor = .systemYellow
        configureBridge()
        configureSplitView()
        configureTabBar()
        configureEasterEggOverlay()
        updateLayout(for: traitCollection, animated: false)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        layoutPrimaryTabs()
        primaryTabsContent.updateHorizontalCenter()
        primaryTabsContent.layoutIfNeeded()
        updateComposeInsets()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        view.window?.tintColor = .systemYellow
        bridge.systemAppearanceChanged(isDark: currentSystemIsDark)
        applyTheme(isDark: isDarkTheme, usesSystemAppearance: usesSystemAppearance)
        refreshSidebarPresentationIfNeeded()
    }

    private func refreshSidebarPresentationIfNeeded() {
        guard view.window != nil, !hasRefreshedSidebarPresentation else { return }
        DispatchQueue.main.async { [weak self] in
            guard let self, !self.hasRefreshedSidebarPresentation, self.usesSidebar,
                  self.splitController.displayMode == .oneBesideSecondary else { return }
            self.hasRefreshedSidebarPresentation = true
            UIView.performWithoutAnimation {
                self.splitController.hide(.primary)
                self.splitController.view.layoutIfNeeded()
                self.splitController.show(.primary)
                self.splitController.view.layoutIfNeeded()
            }
        }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        bridge?.systemAppearanceChanged(isDark: currentSystemIsDark)
        updateLayout(for: traitCollection, animated: true)
    }

    private var currentSystemIsDark: Bool {
        UIScreen.main.traitCollection.userInterfaceStyle == .dark
    }

    private func configureBridge() {
        bridge = NativeComponentBridge()
        bridge.systemAppearanceChanged(isDark: currentSystemIsDark)
        bridge.setCallbacks(
            onRootDestinationChanged: { [weak self] route in
                DispatchQueue.main.async { self?.setRootDestination(route) }
            },
            onFragmentDestinationChanged: { [weak self] route in
                DispatchQueue.main.async { self?.setFragmentDestination(route) }
            },
            onCanNavigateBackChanged: { [weak self] canNavigateBack in
                DispatchQueue.main.async { self?.setCanNavigateBack(canNavigateBack.boolValue) }
            },
            onThemeChanged: { [weak self] isDark, usesSystemAppearance in
                DispatchQueue.main.async {
                    self?.applyTheme(
                        isDark: isDark.boolValue,
                        usesSystemAppearance: usesSystemAppearance.boolValue
                    )
                }
            },
            onPrimaryTabsChanged: { [weak self] labels, selectedIndex, visible in
                DispatchQueue.main.async {
                    self?.updatePrimaryTabs(
                        labels: labels,
                        selectedIndex: selectedIndex.intValue,
                        visible: visible.boolValue
                    )
                }
            },
            onOfflineStatusChanged: { [weak self] offline in
                DispatchQueue.main.async {
                    guard let self else { return }
                    self.contentHostController.navigationItem.setRightBarButton(
                        offline.boolValue ? self.offlineItem : nil,
                        animated: true
                    )
                }
            }
        )
    }

    private func configureSplitView() {
        composeController = MainViewControllerKt.mainViewController(nativeBridge: bridge)
        contentHostController = ComposeScrollHostViewController(contentController: composeController)
        composeController.view.tintColor = .systemYellow
        contentHostController.view.tintColor = .systemYellow
        contentHostController.extendedLayoutIncludesOpaqueBars = true
        contentHostController.edgesForExtendedLayout = .all

        contentNavigationController = UINavigationController(rootViewController: contentHostController)
        contentNavigationController.view.backgroundColor = .clear
        contentNavigationController.navigationBar.isTranslucent = true
        contentNavigationController.navigationBar.prefersLargeTitles = false
        contentNavigationController.navigationBar.titleTextAttributes = [.font: registerComposeTitleFont()]
        composeBackGesture.edges = .left
        composeBackGesture.cancelsTouchesInView = false
        composeController.view.addGestureRecognizer(composeBackGesture)
        sidebarRevealGesture.edges = .left
        sidebarRevealGesture.cancelsTouchesInView = false
        sidebarRevealGesture.delegate = self

        sidebarController = SidebarContainerViewController(
            contentController: SidebarViewControllerKt.sidebarViewController(nativeBridge: bridge),
            topInsetChanged: { [weak bridge] inset in
                bridge?.sidebarTopInsetChanged(points: inset)
            }
        )

        splitController.setViewController(contentNavigationController, for: .secondary)
        splitController.preferredSplitBehavior = .tile
        splitController.delegate = self
        splitController.preferredPrimaryColumnWidthFraction = 0.28
        splitController.minimumPrimaryColumnWidth = 260
        splitController.maximumPrimaryColumnWidth = 360
        splitController.presentsWithGesture = false
        splitController.displayModeButtonVisibility = .never
        splitController.view.backgroundColor = .clear
        contentHostController.view.addGestureRecognizer(sidebarRevealGesture)

        addChild(splitController)
        view.addSubview(splitController.view)
        splitController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            splitController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            splitController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            splitController.view.topAnchor.constraint(equalTo: view.topAnchor),
            splitController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        splitController.didMove(toParent: self)
        primaryTabsContent.isHidden = true
        primaryTabsContent.alpha = 0
    }

    private func configureTabBar() {
        tabBar.delegate = self
        tabBar.tintColor = .systemYellow
        tabBar.isHidden = true
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        let tabSections: [AppSection] = [.home, .settings]
        let tabTitleAttributes: [NSAttributedString.Key: Any] = [
            .font: nativeBodyFont(ofSize: 12),
        ]
        tabBar.items = tabSections.map { section in
            let image = UIImage(named: section.imageName)?.withRenderingMode(.alwaysTemplate)
            let tag = AppSection.allCases.firstIndex(of: section) ?? 0
            let item = UITabBarItem(title: section.title, image: image, tag: tag)
            item.setTitleTextAttributes(tabTitleAttributes, for: .normal)
            item.setTitleTextAttributes(tabTitleAttributes, for: .selected)
            return item
        }
        tabBar.selectedItem = tabBar.items?.first
        view.addSubview(tabBar)
        NSLayoutConstraint.activate([
            tabBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tabBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tabBar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }

    private func configureEasterEggOverlay() {
        easterEggOverlayController = EasterEggOverlayViewControllerKt.easterEggOverlayViewController(nativeBridge: bridge)
        addChild(easterEggOverlayController)
        view.addSubview(easterEggOverlayController.view)
        easterEggOverlayController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            easterEggOverlayController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            easterEggOverlayController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            easterEggOverlayController.view.topAnchor.constraint(equalTo: view.topAnchor),
            easterEggOverlayController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        easterEggOverlayController.didMove(toParent: self)
    }

    private func updateLayout(for traits: UITraitCollection, animated: Bool) {
        usesSidebar = UIDevice.current.userInterfaceIdiom == .pad && traits.horizontalSizeClass == .regular
        updateChrome(animated: animated)
    }

    private func setRootDestination(_ route: String) {
        displaysMainContent = route == "main"
        updateChrome(animated: true)
    }

    private func setFragmentDestination(_ route: String) {
        guard let section = AppSection(rawValue: route) else { return }
        selectedSection = section
        contentHostController.navigationItem.title = section.title
        if let index = AppSection.allCases.firstIndex(of: section) {
            tabBar.selectedItem = tabBar.items?.first(where: { $0.tag == index })
        }
        updateChrome(animated: true)
    }

    private func setCanNavigateBack(_ canNavigateBack: Bool) {
        self.canNavigateBack = canNavigateBack
        updateChrome(animated: true)
    }

    private func applyTheme(isDark: Bool, usesSystemAppearance: Bool) {
        isDarkTheme = isDark
        self.usesSystemAppearance = usesSystemAppearance
        let style: UIUserInterfaceStyle = usesSystemAppearance ? .unspecified : (isDark ? .dark : .light)
        overrideUserInterfaceStyle = style
        view.window?.overrideUserInterfaceStyle = style
        splitController.overrideUserInterfaceStyle = style
        contentNavigationController?.overrideUserInterfaceStyle = style
        contentHostController?.overrideUserInterfaceStyle = style
        composeController?.overrideUserInterfaceStyle = style
        sidebarController?.overrideUserInterfaceStyle = style
        easterEggOverlayController?.overrideUserInterfaceStyle = style
    }

    private func updatePrimaryTabs(labels: String, selectedIndex: Int, visible: Bool) {
        guard #available(iOS 26.0, *), contentHostController != nil else { return }
        if visible {
            let titles = labels.split(separator: "\u{001F}", omittingEmptySubsequences: false).map(String.init)
            primaryTabsContent.update(titles: titles, selectedIndex: selectedIndex)
        }
        guard visible != primaryTabsVisible else { return }
        primaryTabsVisible = visible
        if let animator = primaryTabsAnimator {
            animator.stopAnimation(false)
            animator.finishAnimation(at: .current)
        }
        primaryTabsContent.isHidden = false
        primaryTabsContent.isUserInteractionEnabled = visible
        if primaryTabsContent.superview == nil {
            view.addSubview(primaryTabsContent)
        }
        if visible && primaryTabsContent.interactions.isEmpty {
            let interaction = UIScrollEdgeElementContainerInteraction()
            interaction.scrollView = contentHostController.scrollView
            interaction.edge = .top
            primaryTabsContent.addInteraction(interaction)
        }
        layoutPrimaryTabs()
        let animator = UIViewPropertyAnimator(
            duration: UIAccessibility.isReduceMotionEnabled ? 0 : 0.3,
            curve: .easeInOut
        ) {
            self.primaryTabsContent.alpha = visible ? 1 : 0
        }
        animator.addCompletion { [weak self] _ in
            guard let self, self.primaryTabsVisible == visible else { return }
            self.primaryTabsContent.isHidden = !visible
            if !visible {
                for interaction in self.primaryTabsContent.interactions {
                    (interaction as? UIScrollEdgeElementContainerInteraction)?.scrollView = nil
                    self.primaryTabsContent.removeInteraction(interaction)
                }
                self.primaryTabsContent.removeFromSuperview()
            }
            self.primaryTabsInsetLink?.invalidate()
            self.primaryTabsInsetLink = nil
            self.primaryTabsAnimator = nil
            self.updateComposeInsets()
        }
        primaryTabsAnimator = animator
        primaryTabsInsetLink?.invalidate()
        let link = CADisplayLink(target: self, selector: #selector(primaryTabsAnimationTick))
        link.add(to: .main, forMode: .common)
        primaryTabsInsetLink = link
        animator.startAnimation()
    }

    private func layoutPrimaryTabs() {
        guard contentHostController != nil else { return }
        let hostView = view!
        let navigationBottom = contentNavigationController.navigationBar.convert(
            contentNavigationController.navigationBar.bounds, to: hostView
        ).maxY
        primaryTabsContent.frame = CGRect(x: 0, y: navigationBottom, width: hostView.bounds.width, height: 56)
        if primaryTabsContent.superview === hostView {
            hostView.bringSubviewToFront(primaryTabsContent)
        }
    }

    @objc private func primaryTabsAnimationTick() {
        updateComposeInsets()
    }

    private func registerComposeTitleFont() -> UIFont {
        registerBundledFont(named: "KeaniaOne-Regular", resource: "KeaniaOne-Regular")
        return UIFont(name: "KeaniaOne-Regular", size: 24) ?? UIFont.systemFont(ofSize: 24)
    }

    private func nativeBodyFont(ofSize size: CGFloat) -> UIFont {
        UIFont(name: "Sniglet-Regular", size: size) ?? UIFont.systemFont(ofSize: size)
    }

    private func primaryTabsHorizontalCenter(in contentView: UIView) -> CGFloat? {
        guard let window = contentView.window ?? viewIfLoaded?.window else { return nil }
        let splitFrame = splitController.view.convert(splitController.view.bounds, to: window)
        var visibleContentMinX = splitFrame.minX

        let sidebarIsVisible = usesSidebar && splitController.displayMode != .secondaryOnly
        if sidebarIsVisible,
           let primaryController = splitController.viewController(for: .primary),
           primaryController.viewIfLoaded?.window === window {
            let primaryFrame: CGRect
            if let presentationFrame = primaryController.view.layer.presentation()?.frame,
               let primarySuperview = primaryController.view.superview {
                primaryFrame = primarySuperview.convert(presentationFrame, to: window)
            } else {
                primaryFrame = primaryController.view.convert(primaryController.view.bounds, to: window)
            }

            let visiblePrimaryFrame = primaryFrame.intersection(splitFrame)
            if !visiblePrimaryFrame.isNull, visiblePrimaryFrame.width > 1 {
                visibleContentMinX = visiblePrimaryFrame.maxX
            }
        }

        let visibleContentCenter = CGPoint(
            x: (visibleContentMinX + splitFrame.maxX) / 2,
            y: splitFrame.midY
        )
        return contentView.convert(visibleContentCenter, from: window).x
    }

    private func registerBundledFont(named fontName: String, resource: String) {
        guard UIFont(name: fontName, size: 12) == nil,
              let fontURL = Bundle.main.url(
                  forResource: resource,
                  withExtension: "ttf",
                  subdirectory: "compose-resources/composeResources/bestenotenapp.composeapp.generated.resources/font"
              ) else { return }
        CTFontManagerRegisterFontsForURL(fontURL as CFURL, .process, nil)
    }

    private func updateChrome(animated: Bool) {
        guard isViewLoaded else { return }
        let visible = displaysMainContent
        let sidebarVisible = visible && usesSidebar
        let compactChromeVisible = visible && !usesSidebar
        let shouldShowBottomTabs =
            compactChromeVisible && (selectedSection == .home || selectedSection == .settings)

        contentNavigationController.setNavigationBarHidden(!visible, animated: animated)

        splitController.preferredDisplayMode = sidebarVisible && !isSidebarCollapsed
            ? .oneBesideSecondary
            : .secondaryOnly
        let primaryController = sidebarVisible ? sidebarController : nil
        if splitController.viewController(for: .primary) !== primaryController {
            splitController.setViewController(primaryController, for: .primary)
        }
        contentHostController.navigationItem.leftBarButtonItem = sidebarVisible
            ? sidebarToggleItem
            : (compactChromeVisible && canNavigateBack ? composeBackItem : nil)
        composeBackGesture.isEnabled = compactChromeVisible && canNavigateBack
        splitController.presentsWithGesture = false
        sidebarRevealGesture.isEnabled = sidebarVisible && !canNavigateBack
        splitController.displayModeButtonVisibility = .never
        splitController.view.setNeedsLayout()
        updateBottomTabs(visible: shouldShowBottomTabs, animated: animated)
        refreshSidebarPresentationIfNeeded()
    }

    private func updateComposeInsets() {
        guard composeController != nil else { return }
        let baseTop = contentHostController.view.safeAreaInsets.top
        let chromeBottom: CGFloat
        if displaysMainContent && !contentNavigationController.isNavigationBarHidden {
            let navigationBarBottom = contentNavigationController.navigationBar.convert(
                contentNavigationController.navigationBar.bounds,
                to: contentHostController.view
            ).maxY
            let tabsProgress = primaryTabsContent.isHidden
                ? 0 : CGFloat(primaryTabsContent.layer.presentation()?.opacity ?? Float(primaryTabsContent.alpha))
            chromeBottom = navigationBarBottom + 56 * tabsProgress
        } else {
            chromeBottom = baseTop
        }
        contentHostController.additionalSafeAreaInsets.top = 0
        bridge.contentTopInsetChanged(points: Double(max(baseTop, chromeBottom)))
        contentHostController.additionalSafeAreaInsets.bottom = bottomTabsVisible ? tabBar.bounds.height : 0
    }

    private func updateBottomTabs(visible: Bool, animated: Bool) {
        let visibilityChanged = visible != bottomTabsVisible
        bottomTabsVisible = visible

        guard visibilityChanged else {
            tabBar.isHidden = !visible
            if visible {
                tabBar.transform = .identity
            }
            updateComposeInsets()
            return
        }

        view.layoutIfNeeded()
        let hiddenTransform = CGAffineTransform(
            translationX: 0,
            y: tabBar.bounds.height + view.safeAreaInsets.bottom + 12
        )

        guard animated else {
            tabBar.isHidden = !visible
            tabBar.transform = visible ? .identity : hiddenTransform
            updateComposeInsets()
            return
        }

        if visible {
            tabBar.isHidden = false
            tabBar.transform = hiddenTransform
            contentHostController.additionalSafeAreaInsets.bottom = 0
            UIView.animate(
                withDuration: 0.42,
                delay: 0,
                usingSpringWithDamping: 0.9,
                initialSpringVelocity: 0.1,
                options: [.beginFromCurrentState, .allowUserInteraction]
            ) {
                self.tabBar.transform = .identity
                self.contentHostController.additionalSafeAreaInsets.bottom = self.tabBar.bounds.height
                self.view.layoutIfNeeded()
            }
        } else {
            UIView.animate(
                withDuration: 0.3,
                delay: 0,
                options: [.curveEaseIn, .beginFromCurrentState, .allowUserInteraction]
            ) {
                self.tabBar.transform = hiddenTransform
                self.contentHostController.additionalSafeAreaInsets.bottom = 0
                self.view.layoutIfNeeded()
            } completion: { [weak self] _ in
                guard let self, !self.bottomTabsVisible else { return }
                self.tabBar.isHidden = true
            }
        }
    }

    private func select(_ section: AppSection) {
        selectedSection = section
        bridge.selectFragment(route: section.rawValue)
        setFragmentDestination(section.rawValue)
    }

    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        guard AppSection.allCases.indices.contains(item.tag) else { return }
        select(AppSection.allCases[item.tag])
    }

    @objc private func navigateComposeBack() {
        _ = bridge.navigateBack()
    }

    @objc private func toggleAppSidebar() {
        if splitController.displayMode == .secondaryOnly {
            isSidebarCollapsed = false
            splitController.preferredDisplayMode = .oneBesideSecondary
            splitController.show(.primary)
        } else {
            isSidebarCollapsed = true
            splitController.preferredDisplayMode = .secondaryOnly
            splitController.hide(.primary)
        }
    }

    @objc private func handleComposeBackGesture(_ gesture: UIScreenEdgePanGestureRecognizer) {
        guard gesture.state == .ended, gesture.translation(in: gesture.view).x > 44 else { return }
        navigateComposeBack()
    }

    @objc private func handleSidebarRevealGesture(_ gesture: UIScreenEdgePanGestureRecognizer) {
        guard gesture.state == .ended, gesture.translation(in: gesture.view).x > 44 else { return }
        isSidebarCollapsed = false
        splitController.preferredDisplayMode = .oneBesideSecondary
        splitController.show(.primary)
    }

    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        guard gestureRecognizer === sidebarRevealGesture else { return true }
        return displaysMainContent && usesSidebar && !canNavigateBack &&
            splitController.displayMode == .secondaryOnly
    }

    func splitViewController(
        _ splitViewController: UISplitViewController,
        topColumnForCollapsingToProposedTopColumn proposedTopColumn: UISplitViewController.Column
    ) -> UISplitViewController.Column {
        .secondary
    }

    func splitViewController(
        _ splitViewController: UISplitViewController,
        displayModeForExpandingToProposedDisplayMode proposedDisplayMode: UISplitViewController.DisplayMode
    ) -> UISplitViewController.DisplayMode {
        usesSidebar && displaysMainContent ? .oneBesideSecondary : .secondaryOnly
    }
}
