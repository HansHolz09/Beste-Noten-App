import ComposeApp
import OSLog
import SwiftUI

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    init() {
        let logger = Logger(subsystem: Bundle.main.bundleIdentifier ?? "dev.hansholz.bestenotenapp", category: "GradeNotifications")
        GradeNotificationsKt.ensureIosNotificationsInitialized { message in
            logger.error("\(message, privacy: .public)")
        }
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
