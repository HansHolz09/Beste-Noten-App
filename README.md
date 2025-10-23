Deutsch | [English](README_en.md)
___
<div align="center">
  <a href="https://hansholz09.github.io/beste-noten-app" target="_blank">
    <img width="140" src="composeApp/src/commonMain/composeResources/drawable/logo.png">
  </a>
  <h1>Beste-Noten-App</h1>
</div>

> **_Hintergrund:_** Diese App ist im Rahmen des praktischen Teils meiner Facharbeit über Compose Multiplatform entstanden

Diese plattformübergreifende Schul-App macht den Schulalltag übersichtlicher und ist eine einfachere, schönere und schnellere Alternative zur offiziellen beste.schule-App.

### [Zur Web-Version](https://hansholz09.github.io/beste-noten-app)

**Native Apps:** [siehe Releases](https://github.com/HansHolz09/Beste-Noten-App/releases) _(außer iOS)_
> **_Hinweis:_** Die nativen Apps enthalten keine Entwickler-Zertifikate, weshalb wahrscheinlich Warnungen beim Installieren angezeigt werden. 
> Die iOS/iPadOS Version müssen selber kompiliert und installiert werden ([siehe **Bauen der App**](#bauenstarten-der-app)), da es aktuell noch keinen Weg gibt, 
> Apps auf diesen Geräten ohne Apple Developer Account zu sideloaden. 


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


## Plattformen/Screenshots

<details>
    <summary>Android</summary>
    <img src="screenshots/android_phone.png" height="500"/>
    <img src="screenshots/android_tablet.png" height="500"/>
</details>

<details>
    <summary>iOS/iPadOS</summary>
    <img src="screenshots/ios.png" height="500"/>
    <img src="screenshots/ipados.png" height="500"/>
</details>

<details>
    <summary>MacOS</summary>
    <img src="screenshots/macos.png" height="500"/>
</details>

<details>
    <summary>Windows</summary>
    <img src="screenshots/windows.png" height="500"/>
</details>

<details>
    <summary>Linux</summary>
    <img src="screenshots/linux.png" height="500"/>
</details>

<details>
    <summary>Web</summary>
    <img src="screenshots/web.png" height="500"/>
</details>

[Weitere Screenshots](SCREENSHOTS.md)


## Genutzte Bibliotheken und Plugins
- [Ktor Client](https://github.com/ktorio/ktor) - Apache 2.0 - Zugriff auf Api von beste.schule
- [Kotlin Multiplatform OIDC](https://github.com/kalinjul/kotlin-multiplatform-oidc) - Apache 2.0 - OpenID Connect Unterstützung für Authentifizierung über beste.schule
- [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) - Apache 2.0 - Speichern von Einstellungen
- [KoalaPlot Core](https://github.com/koalaplot/koalaplot-core) - MIT - Diagramm-Bibliothek
- [Jetlime](https://github.com/pushpalroy/Jetlime) - MIT - Timeline-Komponenten für Schulstunden-Übersicht
- [Haze](https://github.com/chrisbanes/haze) - Apache 2.0 - Hintergrund Unschärfe-Effekte
- [MaterialKolor](https://github.com/jordond/MaterialKolor) - MIT - Animierte Farb-Übergänge
- [Multiplatform Material You](https://github.com/zacharee/MultiplatformMaterialYou) - MIT - Erstellen von Material-Design-Farbpaletten für JVM
- [Platform-Tools](https://github.com/kdroidFilter/Platform-Tools) - MIT - Reaktives Erkennen von Hell/Dunkel-Modus
- [animate-compose](https://github.com/NomanR/animate-compose) - Apache 2.0 - Animations-Komponenten
- [ConfettiKit](https://github.com/vinceglb/confettikit) - MIT - Confetti-Animationen (Easter-Eggs)
- [Emoji.kt](https://github.com/kosi-libs/Emoji.kt) - Unterstützung für animierte Emojis
- [Compose Sonner](https://github.com/dokar3/compose-sonner) - Apache 2.0 - Toast-Komponente
- [Alarmee](https://github.com/Tweener/alarmee) - Apache 2.0 - Benachrichtigungen für Android und iOS
- [KMM Permission](https://github.com/reyazoct/Kmm-Permissions) - MIT - Anfragen der Benachrichtigungsberechtigung
- [AndroidX Security Crypto](https://developer.android.com/jetpack/androidx/releases/security) - Apache 2.0 - Kryptografiebibliothek für Android
- [AndroidX Biometric](https://developer.android.com/jetpack/androidx/releases/biometric) - Apache 2.0 - Unterstützung für biometrische Authentifizierung unter Android
- [SmartSpacer SDK](https://github.com/KieronQuinn/Smartspacer) - GPL 3.0 - Integration für SmartSpacer unter Android
- [JBR API](https://github.com/JetBrains/JetBrainsRuntimeApi) - Apache 2.0 - API von JetBrains Runtime für Zugriff auf Native Fenstersteuerelemente
- [JNA](https://github.com/java-native-access/jna) - Apache 2.0 - Java Native Access für Zugriff auf Native Fenstersteuerelemente
- [Credential Secure Storage for Java](https://github.com/microsoft/credential-secure-storage-for-java) - MIT - Sichere Tokenspeicherung für JVM
- [Ktlint Gradle](https://github.com/JLLeitschuh/ktlint-gradle) - MIT - Wrapper-Plugin für [ktlint](https://github.com/pinterest/ktlint)
- [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) - Apache 2.0 - Automatisches Erzeugen von BuildConfig-Klasse für App-Version


## Bauen/Starten der App

1. Klone den Quellcode
2. Öffne ihn mit [Android Studio](https://developer.android.com/studio) oder [Intellij IDEA (Community Edition)](https://www.jetbrains.com/idea/download)
3. Zum bauen bzw. starten der iOS/iPadOS App öffne `/iosApp` in XCode (Nur unter MacOS)
4. Starte eine beliebige Konfiguration in Android Studio/Intellij IDEA:
    - Run Desktop App / `./gradlew run`
    - Run Web App / `./gradlew wasmJsBrowserDevelopmentRun`
    - Run Android App
    - Package Release as DMG / `./gradlew createReleaseDmg` (Nur unter MacOS)
    - Package Release as EXE / `./gradlew packageReleaseExe` (Nur unter Windows)
    - Package Release as DEB / `./gradlew packageReleaseDeb` (Nur unter Linux)
    - Package Web App / `./gradlew wasmJsBrowserDistribution`