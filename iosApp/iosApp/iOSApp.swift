import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
    init() {
        GradeNotificationsKt.ensureIosNotificationsInitialized()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
