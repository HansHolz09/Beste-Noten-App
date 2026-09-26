import UIKit

final class PrimaryTabsPaletteContentView: UIView {
    var selectionChanged: (Int) -> Void

    private let segmentedControl = UISegmentedControl()
    private let horizontalCenter: (UIView) -> CGFloat?
    private var horizontalCenterConstraint: NSLayoutConstraint!
    private var titles: [String] = []
    private var lastHostWidth: CGFloat = 0

    init(
        font: UIFont,
        horizontalCenter: @escaping (UIView) -> CGFloat?,
        selectionChanged: @escaping (Int) -> Void
    ) {
        self.horizontalCenter = horizontalCenter
        self.selectionChanged = selectionChanged
        super.init(frame: .zero)

        segmentedControl.setTitleTextAttributes([.font: font], for: .normal)
        segmentedControl.setTitleTextAttributes([.font: font], for: .selected)
        segmentedControl.addTarget(self, action: #selector(selectTab), for: .valueChanged)
        segmentedControl.translatesAutoresizingMaskIntoConstraints = false
        setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        setContentHuggingPriority(.defaultLow, for: .horizontal)
        if #available(iOS 26.0, *) {
            let glassView = UIVisualEffectView(effect: UIGlassEffect(style: .regular))
            glassView.translatesAutoresizingMaskIntoConstraints = false
            glassView.layer.cornerRadius = 20
            glassView.clipsToBounds = true
            addSubview(glassView)
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
            addSubview(segmentedControl)
        }
        let preferredWidth = segmentedControl.widthAnchor.constraint(equalToConstant: 370)
        preferredWidth.priority = .defaultHigh
        horizontalCenterConstraint = segmentedControl.centerXAnchor.constraint(equalTo: centerXAnchor)
        NSLayoutConstraint.activate([
            horizontalCenterConstraint,
            segmentedControl.leadingAnchor.constraint(greaterThanOrEqualTo: leadingAnchor, constant: 16),
            segmentedControl.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor, constant: -16),
            segmentedControl.centerYAnchor.constraint(equalTo: centerYAnchor),
            segmentedControl.heightAnchor.constraint(equalToConstant: 40),
            preferredWidth,
        ])
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override var intrinsicContentSize: CGSize {
        CGSize(width: window?.bounds.width ?? UIScreen.main.bounds.width, height: 56)
    }

    override func didMoveToWindow() {
        super.didMoveToWindow()
        lastHostWidth = window?.bounds.width ?? UIScreen.main.bounds.width
        invalidateIntrinsicContentSize()
    }

    override func layoutSubviews() {
        updateHorizontalCenter()
        super.layoutSubviews()
    }

    func updateHorizontalCenter() {
        let hostWidth = window?.bounds.width ?? UIScreen.main.bounds.width
        if abs(lastHostWidth - hostWidth) > 0.5 {
            lastHostWidth = hostWidth
            invalidateIntrinsicContentSize()
        }
        guard bounds.width > 0, let desiredCenter = horizontalCenter(self) else { return }
        let offset = desiredCenter - bounds.midX
        if abs(horizontalCenterConstraint.constant - offset) > 0.5 {
            horizontalCenterConstraint.constant = offset
        }
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
