import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // The keyboard must not cover the input field, so unlike a purely
        // visual app this one keeps the bottom safe area.
        ComposeView()
            .ignoresSafeArea(edges: [.top, .leading, .trailing])
    }
}
