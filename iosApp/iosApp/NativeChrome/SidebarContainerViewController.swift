import UIKit

final class SidebarContainerViewController: UIViewController {
    private let contentController: UIViewController
    private let effectView: UIVisualEffectView
    private let topInsetChanged: (CGFloat) -> Void
    private var lastReportedTopInset: CGFloat?

    init(contentController: UIViewController, topInsetChanged: @escaping (CGFloat) -> Void) {
        self.contentController = contentController
        self.topInsetChanged = topInsetChanged
        if #available(iOS 26.0, *) {
            effectView = UIVisualEffectView(effect: UIGlassEffect())
        } else {
            effectView = UIVisualEffectView(effect: UIBlurEffect(style: .systemMaterial))
        }
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear
        effectView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(effectView)
        NSLayoutConstraint.activate([
            effectView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            effectView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            effectView.topAnchor.constraint(equalTo: view.topAnchor),
            effectView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])

        addChild(contentController)
        view.addSubview(contentController.view)
        contentController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            contentController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            contentController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            contentController.view.topAnchor.constraint(equalTo: view.topAnchor),
            contentController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        contentController.didMove(toParent: self)
    }

    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        reportTopInset()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        reportTopInset()
    }

    private func reportTopInset() {
        guard let window = view.window else { return }
        let windowSize = window.bounds.size
        let screenSize = window.screen.bounds.size
        let isWindowed = abs(windowSize.width - screenSize.width) > 1 || abs(windowSize.height - screenSize.height) > 1
        let topInset = isWindowed ? view.safeAreaInsets.top : window.safeAreaInsets.top
        guard topInset != lastReportedTopInset else { return }
        lastReportedTopInset = topInset
        topInsetChanged(topInset)
    }
}
