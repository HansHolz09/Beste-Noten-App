import UIKit

final class NavigationTitleView: UIView {
    private let font: UIFont
    private let preferredSize: CGSize
    private var titleLabel: UILabel?

    init(font: UIFont, titles: [String]) {
        self.font = font
        let width = titles.map { ($0 as NSString).size(withAttributes: [.font: font]).width }.max() ?? 0
        preferredSize = CGSize(width: ceil(width) + 40, height: ceil(font.lineHeight))
        super.init(frame: CGRect(origin: .zero, size: preferredSize))
        clipsToBounds = true
        isUserInteractionEnabled = false
        isAccessibilityElement = true
        accessibilityTraits = .header
        setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var intrinsicContentSize: CGSize {
        preferredSize
    }

    func update(title: String, backwards: Bool, animated: Bool) {
        guard titleLabel?.text != title else { return }
        let outgoingLabel = titleLabel
        let incomingLabel = UILabel()
        incomingLabel.font = font
        incomingLabel.textColor = .label
        incomingLabel.textAlignment = .center
        incomingLabel.lineBreakMode = .byTruncatingTail
        incomingLabel.text = title
        incomingLabel.isAccessibilityElement = false
        incomingLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(incomingLabel)
        NSLayoutConstraint.activate([
            incomingLabel.leadingAnchor.constraint(equalTo: leadingAnchor),
            incomingLabel.trailingAnchor.constraint(equalTo: trailingAnchor),
            incomingLabel.topAnchor.constraint(equalTo: topAnchor),
            incomingLabel.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
        layoutIfNeeded()
        titleLabel = incomingLabel
        accessibilityLabel = title

        guard animated, outgoingLabel != nil, !UIAccessibility.isReduceMotionEnabled else {
            for subview in subviews where subview !== incomingLabel {
                subview.layer.removeAllAnimations()
                subview.removeFromSuperview()
            }
            return
        }

        let direction: CGFloat = backwards ? -1 : 1
        incomingLabel.alpha = 0
        incomingLabel.transform = CGAffineTransform(translationX: direction * 20, y: 0)
        UIView.animate(
            withDuration: 0.3,
            delay: 0,
            options: [.curveEaseInOut, .beginFromCurrentState, .allowUserInteraction]
        ) {
            incomingLabel.alpha = 1
            incomingLabel.transform = .identity
            outgoingLabel?.alpha = 0
            outgoingLabel?.transform = CGAffineTransform(translationX: -direction * 20, y: 0)
        } completion: { _ in
            outgoingLabel?.removeFromSuperview()
        }
    }
}
