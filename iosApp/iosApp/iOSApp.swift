import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    init() {
        GradeNotificationsKt.ensureIosNotificationsInitialized()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .onChange(of: scenePhase) { phase in
            if phase == .active {
                GradeNotificationsKt.onIosNotificationsForegrounded()
            } else if phase == .background {
                GradeNotificationsKt.onIosNotificationsBackgrounded()
            }
        }
    }
}
