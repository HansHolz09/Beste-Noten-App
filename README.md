Deutsch | [English](https://github.com/HansHolz09/Beste-Noten-App/blob/main/README_en.md)
___
<img src="https://github.com/HansHolz09/Beste-Noten-App/blob/5ca8b6602f3fb3446c2f594f6baf22aab3dbbcc1/composeApp/src/commonMain/composeResources/drawable/logo.png?raw=true" width="100" />

# Beste-Noten-App
> **_Hintergrund:_** Diese App ist im Rahmen des praktischen Teils meiner Facharbeit über Compose Multiplatform entstanden

[Beschreibung]

**Zur Web-Version:** [Link](Link-zu-Github-Pages)

**Native Apps:** [siehe Releases](Link-zu-Github-Releases) _(außer iOS)_
> **_Hinweis:_** Die nativen Apps enthalten keine Entwickler-Zertifikate, weshalb wahrscheinlich Warnungen beim Installieren angezeigt werden. 
> Die iOS/iPadOS Version müssen selber kompiliert und installiert werden ([siehe **Bauen der App**](#bauen-der-app)), da es aktuell noch keinen Weg gibt, 
> Apps auf diesen Geräten ohne Apple Developer Account zu sideloaden. 

---
## Funktionen
- Login über Private-Access-Token oder direkt über beste.schule
- Demo-Account zum Ausprobieren der App
- Startseite mit Tagesübersicht und aktuellen Noten
- Einfache Notenübersicht mit Möglichkeit zum Ansehen der Noten-Historien
- Noten-Diagramme zum Vergleich der verschiedenen Schuljahre
- Übersichtliche Stundenplan-Ansicht mit Vertretungsplan-Änderungen
- Übersicht aktueller Fächer und Lehrer mit ihren Abkürzungen
- Adaptives Material-3-Expressive-Design auf allen Platformen
- Schöne Animationen und Übergänge
- Haptisches Feedback auf Android und iOS
- Benachrichtigungen über neue Noten mit anpassbarem Überprüfungsintervall für Android und iOS
- Optionale biometrische Authentifizierung bei jedem Start der App auf Android und iOS
- Einige Anpassungsmöglichkeiten
- Integration nativer Fenstersteuerelemente (außer unter Linux)
- [Smartspacer](https://github.com/KieronQuinn/Smartspacer)-Integration für Android
- und mehr...

---
## Plattformen/Screenshots

### Android
[Screenshot]

### iOS/iPadOS
[Screenshot]

### MacOS
[Screenshot]

### Windows
[Screenshot]

### Linux
[Screenshot]

### Web
[Screenshot]

---
## Genutzte Bibliotheken und Plugins
- [Ktor Client](https://github.com/ktorio/ktor) - Apache 2.0 - Zugriff auf Api von beste.schule
- [Kotlin Multiplatform OIDC](https://github.com/kalinjul/kotlin-multiplatform-oidc) - Apache 2.0 - OpenID Connect Unterstützung für Authentifizierung über beste.schule
- [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) - Apache 2.0 - Speichern von Einstellungen
- [KoalaPlot Core](https://github.com/koalaplot/koalaplot-core) - MIT - Diagramm-Bibliothek
- [Jetlime](https://github.com/pushpalroy/Jetlime) - MIT - Timeline-Komponenten für Schulstunden-Übersicht
- [Haze](https://github.com/chrisbanes/haze) - Apache 2.0 - Hintergrund Unschärfe-Effekte
- [MaterialKolor](https://github.com/jordond/MaterialKolor) - MIT - Erstellen von Material-Design-Farbpaletten auf nicht-Android-Plattformen
- [Platform-Tools](https://github.com/kdroidFilter/Platform-Tools) - MIT - Reaktives Erkennen von Hell/Dunkel-Modus
- [animate-compose](https://github.com/NomanR/animate-compose) - Apache 2.0 - Animations-Komponenten
- [ConfettiKit](https://github.com/vinceglb/confettikit) - MIT - Confetti-Animationen (Easter-Eggs)
- [Emoji.kt](https://github.com/kosi-libs/Emoji.kt) - Unterstützung für animierte Emojis
- [Compose Sonner](https://github.com/dokar3/compose-sonner) - Apache 2.0 - Toast-Komponente
- [Alarmee](https://github.com/Tweener/alarmee) - Apache 2.0 - Benachrichtigungen für Android und iOS
- [AndroidX Security Crypto](https://developer.android.com/jetpack/androidx/releases/security) - Apache 2.0 - Kryptografiebibliothek für Android
- [AndroidX Biometric](https://developer.android.com/jetpack/androidx/releases/biometric) - Apache 2.0 - Unterstützung für biometrische Authentifizierung unter Android
- [SmartSpacer SDK](https://github.com/KieronQuinn/Smartspacer) - GPL 3.0 - Integration für SmartSpacer unter Android
- [JBR API](https://github.com/JetBrains/JetBrainsRuntimeApi) - Apache 2.0 - API von JetBrains Runtime für Zugriff auf Native Fenstersteuerelemente
- [JNA](https://github.com/java-native-access/jna) - Apache 2.0 - Java Native Access für Zugriff auf Native Fenstersteuerelemente
- [Credential Secure Storage for Java](https://github.com/microsoft/credential-secure-storage-for-java) - MIT - Sichere Tokenspeicherung für JVM
- [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) - Apache 2.0 - Automatisches Erzeugen von BuildConfig-Klasse für App-Version

---
## Bauen der App

...
