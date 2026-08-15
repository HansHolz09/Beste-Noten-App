English | [Deutsch](README.md)
___
<div align="center">
  <a href="https://hansholz09.github.io/Beste-Noten-App" target="_blank">
    <img width="140" src="composeApp/src/commonMain/composeResources/drawable/logo.png">
  </a>
  <h1>Beste-Noten-App</h1>
</div>

This cross-platform school app makes everyday school life more organized and is a simpler, more beautiful, and faster alternative to the official beste.schule app.

### [Web Version](https://hansholz09.github.io/Beste-Noten-App)

**Native Apps:** [see Releases](https://github.com/HansHolz09/Beste-Noten-App/releases)
> [!IMPORTANT]  
> The IPA file provided for iOS/iPadOS is not signed and therefore cannot be installed directly on these devices.
> It is therefore recommended to sideload this file using SideStore ([setup](https://docs.sidestore.io/docs/installation/prerequisites)).
> Alternatively, the app can be compiled directly ([see **Building/Running the App**](#buildingrunning-the-app)) and then installed directly on the desired device via XCode.


## Features
- Login via private access token or directly through beste.schule
- Demo account to try out the app
- Home page with daily overview, current grades, and annual progress
- Simple grade overview with the option to view grade history and configurable average calculation
- Grade charts for comparing different school years
- Clear timetable view with substitute teacher schedule changes, absence entries, and current daily notes
- Alternative block view for the timetable (automatically enabled in upper secondary school) with automatic filtering of unnecessary lessons
- Digital homework planner including Google Calendar synchronization
- Overview of current subjects and teachers with their abbreviations
- Annual information on half-year periods and absence statistics, including a heatmap of attendance by lesson
- Dialogs for viewing lesson times as well as account and school data
- Direct support for changing the school year
- Import/export of app settings and grade weightings as well as grades with the option to view them later without a beste.schule account
- Full offline mode thanks to caching
- Adaptive Material 3 Expressive design on all platforms with dynamically generated background images
- Beautiful animations and transitions
- Immersive haptic feedback on supported devices
- Notifications for new grades with a customizable check interval for Android and iOS
- Optional biometric authentication on every app launch for Android, iOS, and supported desktop devices
- Native desktop apps (including via GraalVM) with platform-specific context menus
- Some customization options
- and more...


## Platforms/Screenshots

<details>
    <summary>Android</summary>
    <img src="screenshots/android_phone.png" height="500"/>
    <img src="screenshots/android_tablet.png" height="500"/>
</details>

<details>
    <summary>iOS/iPadOS</summary>
    <img src="screenshots/ios.png" height="500"/>
    <img src="screenshots/ipados.png" height="500">
</details>

<details>
    <summary>macOS</summary>
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


## Libraries and Plugins Used
- [Ktor Client](https://github.com/ktorio/ktor) - Apache 2.0 - Access to the beste.schule API
- [Kotlin Multiplatform OIDC](https://github.com/kalinjul/kotlin-multiplatform-oidc) - Apache 2.0 - OpenID Connect support for authentication via beste.schule
- [KSafe](https://github.com/ioannisa/KSafe) - Apache 2.0 - Saving settings and login details
- [KoalaPlot Core](https://github.com/koalaplot/koalaplot-core) - MIT - Chart library
- [Jetlime](https://github.com/pushpalroy/Jetlime) - MIT - Timeline components for class overview
- [Haze](https://github.com/chrisbanes/haze) - Apache 2.0 - Background blur effects
- [MaterialKolor](https://github.com/jordond/MaterialKolor) - MIT - Animated color transitions
- [Multiplatform Material You](https://github.com/zacharee/MultiplatformMaterialYou) - MIT - Creating Material Design color palettes for JVM
- [Platform-Tools](https://github.com/kdroidFilter/Platform-Tools) - MIT - Reactive detection of light/dark mode
- [animate-compose](https://github.com/NomanR/animate-compose) - Apache 2.0 - Animation components
- [ConfettiKit](https://github.com/vinceglb/confettikit) - MIT - Confetti animations (Easter eggs)
- [Emoji.kt](https://github.com/kosi-libs/Emoji.kt) - Support for animated emojis
- [Compose Sonner](https://github.com/dokar3/compose-sonner) - Apache 2.0 - Toast component
- [AboutLibraries](https://github.com/mikepenz/AboutLibraries) - Apache 2.0 - Component for displaying the libraries used
- [multiplatform-markdown-renderer](https://github.com/mikepenz/multiplatform-markdown-renderer) - Apache 2.0 - Displaying Markdown text for the in-app updater
- [Capturable](https://github.com/jmseb3/Capturable) - MIT - Share/save Composables as images
- [FileKit](https://github.com/vinceglb/FileKit) - MIT - File dialogs for import/export
- [Alarmee](https://github.com/Tweener/alarmee) - Apache 2.0 - Notifications for Android and iOS
- [KMM Permission](https://github.com/reyazoct/Kmm-Permissions) - MIT - Requesting notification permissions
- [multihaptic](https://github.com/xfqwdsj/multihaptic) - MIT - Highly customizable haptic feedback
- [Advanced MenuBar for Compose Desktop](https://github.com/HansHolz09/Advanced-MenuBar) - Apache 2.0 - German macOS menu bar with more options
- [Nucleus](https://github.com/kdroidFilter/Nucleus) - MIT - Creating optimized Tao windows and app installers for desktop targets
- [Oracle GraalVM](https://www.oracle.com/de/developer/graalvm-developers/) - [GFTC](https://www.oracle.com/downloads/licenses/graal-free-license.html) - Compiling desktop apps to native code
- [Ktlint Gradle](https://github.com/JLLeitschuh/ktlint-gradle) - MIT - Wrapper plugin for [ktlint](https://github.com/pinterest/ktlint)
- [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin) - Apache 2.0 - Automatic generation of BuildConfig class for app version


## Building/Running the App

1. Clone the source code
2. Open it with [Android Studio](https://developer.android.com/studio) or [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
3. To build or run the iOS/iPadOS app, open `/iosApp` in Xcode (macOS only)
4. Run any of the following configurations in Android Studio/IntelliJ IDEA:
    - Run Desktop App / `./gradlew run`
    - Run Web App / `./gradlew wasmJsBrowserDevelopmentRun`
    - Run Android App
    - Package Release as DMG / `./gradlew packageReleaseDmg` / `./gradlew packageGraalvmDmg` (macOS only)
    - Package Release as EXE / `./gradlew packageReleaseNsis` / `./gradlew packageGraalvmNsis` (Windows only)
    - Package Release as DEB / `./gradlew packageReleaseDeb` / `./gradlew packageGraalvmDeb` (Linux only)
    - Package Web App / `./gradlew wasmJsBrowserDistribution`
