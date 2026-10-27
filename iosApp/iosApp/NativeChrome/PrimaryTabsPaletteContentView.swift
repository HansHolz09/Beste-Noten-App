import UIKit

final class PrimaryTabsPaletteContentView: UIView {
    var selectionChanged: (Int) -> Void

    private let segmentedControl = UISegmentedControl()
    private let tabContainer = UIView()
    private var titles: [String] = []
    private let updateLayout: () -> Void
    private var layoutDisplayLink: CADisplayLink?

    init(
        font: UIFont,
        updateLayout: @escaping () -> Void,
        selectionChanged: @escaping (Int) -> Void
    ) {
        self.updateLayout = updateLayout
        self.selectionChanged = selectionChanged
        super.init(frame: .zero)

        tabContainer.translatesAutoresizingMaskIntoConstraints = false
        addSubview(tabContainer)
        NSLayoutConstraint.activate([
            tabContainer.leadingAnchor.constraint(equalTo: leadingAnchor),
            tabContainer.trailingAnchor.constraint(equalTo: trailingAnchor),
            tabContainer.topAnchor.constraint(equalTo: topAnchor),
            tabContainer.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])

        segmentedControl.setTitleTextAttributes([.font: font], for: .normal)
        segmentedControl.setTitleTextAttributes([.font: font], for: .selected)
        segmentedControl.addTarget(self, action: #selector(selectTab), for: .valueChanged)
        segmentedControl.translatesAutoresizingMaskIntoConstraints = false
        setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        setContentHuggingPriority(.defaultLow, for: .horizontal)
        if #available(iOS 26.0, *) {
            let effect = UIGlassEffect(style: .clear)
            let ciColor = CIColor(color: UIColor.systemYellow)
            let darkYellow = UIColor(red: ciColor.red * 0.15, green: ciColor.green * 0.15, blue: ciColor.blue * 0.15, alpha: 0.75)
            let lightYellow = UIColor(red: 1 - (1 - ciColor.red) * 0.175, green: 1 - (1 - ciColor.green) * 0.175, blue: 1 - (1 - ciColor.blue) * 0.175, alpha: 0.75)
            effect.tintColor = UIColor { $0.userInterfaceStyle == .dark ? darkYellow : lightYellow }
            let glassView = UIVisualEffectView(effect: effect)
            glassView.translatesAutoresizingMaskIntoConstraints = false
            glassView.layer.cornerRadius = 20
            glassView.clipsToBounds = true
            tabContainer.addSubview(glassView)
            glassView.contentView.addSubview(segmentedControl)
            NSLayoutConstraint.activate([
                glassView.leadingAnchor.constraint(equalTo: segmentedControl.leadingAnchor),
                glassView.trailingAnchor.constraint(equalTo: segmentedControl.trailingAnchor),
                glassView.topAnchor.constraint(equalTo: segmentedControl.topAnchor),
                glassView.bottomAnchor.constraint(equalTo: segmentedControl.bottomAnchor),
            ])
            segmentedControl.backgroundColor = .clear
            segmentedControl.selectedSegmentTintColor = UIColor { traits in
                UIColor.black.withAlphaComponent(traits.userInterfaceStyle == .dark ? 0.35 : 0.08)
            }
        } else {
            tabContainer.addSubview(segmentedControl)
        }
        let preferredWidth = segmentedControl.widthAnchor.constraint(equalToConstant: 370)
        preferredWidth.priority = .defaultHigh
        NSLayoutConstraint.activate([
            segmentedControl.centerXAnchor.constraint(equalTo: tabContainer.centerXAnchor),
            segmentedControl.leadingAnchor.constraint(greaterThanOrEqualTo: tabContainer.leadingAnchor, constant: 16),
            segmentedControl.trailingAnchor.constraint(lessThanOrEqualTo: tabContainer.trailingAnchor, constant: -16),
            segmentedControl.centerYAnchor.constraint(equalTo: tabContainer.centerYAnchor),
            segmentedControl.heightAnchor.constraint(equalToConstant: 40),
            preferredWidth,
        ])
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func didMoveToWindow() {
        super.didMoveToWindow()
        layoutDisplayLink?.invalidate()
        layoutDisplayLink = nil
        if window != nil {
            if traitCollection.userInterfaceIdiom == .pad {
                let link = CADisplayLink(target: self, selector: #selector(updateTabLayout))
                link.add(to: .main, forMode: .common)
                layoutDisplayLink = link
            }
            updateLayout()
        }
    }

    @objc private func updateTabLayout() {
        updateLayout()
    }

    override var intrinsicContentSize: CGSize {
        CGSize(width: UIView.noIntrinsicMetric, height: 56)
    }

    func setTabsVisible(_ visible: Bool) {
        setTabsVisibilityProgress(visible ? 1 : 0)
    }

    func setTabsVisibilityProgress(_ progress: CGFloat) {
        tabContainer.transform = UIAccessibility.isReduceMotionEnabled
            ? .identity
            : CGAffineTransform(translationX: 0, y: -16 * (1 - progress))
    }

    func update(titles: [String], selectedIndex: Int) {
        if self.titles != titles {
            segmentedControl.removeAllSegments()
            for (index, title) in titles.enumerated() {
                segmentedControl.insertSegment(withTitle: title, at: index, animated: false)
            }
            self.titles = titles
        }

        if titles.indices.contains(selectedIndex) {
            segmentedControl.selectedSegmentIndex = selectedIndex
        }
    }

    @objc private func selectTab() {
        selectionChanged(segmentedControl.selectedSegmentIndex)
    }

    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        segmentedControl.point(inside: segmentedControl.convert(point, from: self), with: event)
    }
}
