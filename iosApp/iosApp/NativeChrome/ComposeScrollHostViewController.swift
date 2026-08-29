import UIKit

final class ComposeScrollHostViewController: UIViewController {
    let contentController: UIViewController
    private let scrollView = UIScrollView()
    private let scrollExtentView = UIView()

    init(contentController: UIViewController) {
        self.contentController = contentController
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        view = scrollView
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear
        scrollView.backgroundColor = .clear
        scrollView.contentInsetAdjustmentBehavior = .never
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.panGestureRecognizer.isEnabled = false
        scrollView.scrollsToTop = false
        if #available(iOS 26.0, *) {
            scrollView.topEdgeEffect.style = .automatic
        }

        scrollExtentView.isUserInteractionEnabled = false
        scrollExtentView.backgroundColor = .clear
        scrollExtentView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(scrollExtentView)
        addChild(contentController)
        scrollView.addSubview(contentController.view)
        contentController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            contentController.view.leadingAnchor.constraint(equalTo: scrollView.frameLayoutGuide.leadingAnchor),
            contentController.view.trailingAnchor.constraint(equalTo: scrollView.frameLayoutGuide.trailingAnchor),
            contentController.view.topAnchor.constraint(equalTo: scrollView.frameLayoutGuide.topAnchor),
            contentController.view.bottomAnchor.constraint(equalTo: scrollView.frameLayoutGuide.bottomAnchor),
            scrollExtentView.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            scrollExtentView.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            scrollExtentView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            scrollExtentView.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            scrollExtentView.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor),
            scrollExtentView.heightAnchor.constraint(equalTo: scrollView.frameLayoutGuide.heightAnchor, constant: 1),
        ])
        contentController.didMove(toParent: self)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        if scrollView.contentOffset.y != 1 {
            scrollView.setContentOffset(CGPoint(x: 0, y: 1), animated: false)
        }
    }
}
