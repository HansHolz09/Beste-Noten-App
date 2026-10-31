import ComposeApp
import SwiftUI
import UIKit

@available(iOS 26.0, *)
final class StoreUpdateBannerModel: ObservableObject {
    @Published var version: String?
    @Published var isVisible = false

    init() {
        StoreUpdateKt.setStoreUpdatePrompt { [weak self] version in
            DispatchQueue.main.async {
                self?.version = version
                // Wait for the hosting view to take the banner's size before animating it in
                DispatchQueue.main.async {
                    withAnimation(.spring) { self?.isVisible = true }
                }
            }
        }
    }

    func dismiss() {
        withAnimation(.spring) {
            isVisible = false
        } completion: { [weak self] in
            if self?.isVisible == false { self?.version = nil }
        }
    }
}

@available(iOS 26.0, *)
struct StoreUpdateBanner: View {
    @StateObject private var model = StoreUpdateBannerModel()
    @State private var dragOffset: CGFloat = 0

    var body: some View {
        if let version = model.version {
            HStack(spacing: 12) {
                Image(systemName: "arrow.down.app")
                    .font(.title2)
                    .foregroundStyle(.tint)
                VStack(alignment: .leading, spacing: 2) {
                    Text("Update verfügbar")
                        .font(.headline)
                    Text("Version \(version)")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                Button {
                    model.dismiss()
                    UIApplication.shared.open(URL(string: "https://apps.apple.com/app/id6816709897")!)
                } label: {
                    Text("Aktualisieren")
                        .foregroundStyle(.black)
                }
                .buttonStyle(.glassProminent)
            }
            .padding(.leading, 16)
            .padding(.trailing, 10)
            .padding(.vertical, 10)
            .glassEffect(.regular, in: .capsule)
            .offset(y: model.isVisible ? dragOffset : 120)
            .opacity(model.isVisible ? 1 : 0)
            .allowsHitTesting(model.isVisible)
            .gesture(
                DragGesture()
                    .onChanged { dragOffset = max(0, $0.translation.height) }
                    .onEnded { value in
                        if value.translation.height > 30 {
                            model.dismiss()
                        } else {
                            withAnimation(.spring) { dragOffset = 0 }
                        }
                    }
            )
            .onChange(of: model.isVisible) { if model.isVisible { dragOffset = 0 } }
            .task(id: version) {
                try? await Task.sleep(for: .seconds(8))
                model.dismiss()
            }
        }
    }
}
