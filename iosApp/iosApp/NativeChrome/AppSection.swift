import UIKit

enum AppSection: String, CaseIterable {
    case home
    case grades
    case timetable
    case subjectsAndTeachers = "subjects_and_teachers"
    case settings

    var title: String {
        switch self {
        case .home: "Startseite"
        case .grades: "Noten"
        case .timetable: "Stundenplan"
        case .subjectsAndTeachers: "Fächer und Lehrer"
        case .settings: "Einstellungen"
        }
    }

    var imageName: String {
        switch self {
        case .home: "material-home"
        case .grades: "material-school"
        case .timetable: "material-date-range"
        case .subjectsAndTeachers: "material-demography"
        case .settings: "material-settings"
        }
    }
}
