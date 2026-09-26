import ComposeApp
import SwiftUI
import UIKit

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        if #available(iOS 26.0, *) {
            NativeChromeViewController()
        } else {
            MainViewControllerKt.mainViewController(nativeBridge: nil)
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea()
    }
}
