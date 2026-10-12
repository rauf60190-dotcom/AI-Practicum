# AI Practicum Android wrapper (v8.12)

This Android wrapper packages the existing standalone HTML application at `app/src/main/assets/index.html`.

## Architecture
- Native Android `Activity` in Java hosting a WebView.
- HTML is bundled at `app/src/main/assets/index.html`; no web server is required.
- `WebViewAssetLoader` serves the local app at `https://appassets.androidplatform.net/assets/index.html`, giving Web Storage a stable origin.
- DOM storage is enabled; file/content access and mixed content are disabled.
- No `INTERNET` permission is declared.
- Markdown/text downloads from the HTML app are bridged to Android `MediaStore` and saved under `Downloads/AI Practicum`.
- `allowBackup=false` is intentional for this stage; there is no app-level JSON backup or cloud sync. It also means Android device backup should not be assumed.
- The launcher icon is defined in the Android resources.

## Build requirements
- Android Studio with Android SDK Platform 35 installed.
- JDK 17.
- Gradle 8.9-compatible Android Gradle Plugin 8.7.3; Android Studio can resolve Gradle/plugin dependencies.

## Build APK without a local PC (GitHub Actions)
The repository-root `.github/workflows/build-android-apk.yml` workflow builds this project. Open **Actions → Build AI Practicum APK → Run workflow**, then download the `ai-practicum-debug-apk` artifact from the completed run. Changes under `AI_Practicum_Android/` on `main` also trigger the workflow. The artifact is a debug APK for testing, not a signed release build.

## Build APK in Android Studio
1. Open this folder (`AI_Practicum_Android`) in Android Studio.
2. Let Gradle sync complete.
3. Run `gradlew.bat assembleDebug` on Windows or `bash ./gradlew assembleDebug` on macOS/Linux, or use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Debug APK output should be under `app/build/outputs/apk/debug/app-debug.apk`.

## Before release
- Test localStorage across force-close/relaunch and app upgrade.
- Test Markdown document downloads.
- Test Android Back navigation, keyboard, orientation and narrow screen layouts.
- Review WebView security and confirm no external navigation is needed.
- Produce a signed release APK only after device regression passes.

## Current verification boundary
An APK is not considered verified until the Gradle build succeeds and the resulting file is confirmed non-empty. Source/static checks do not substitute for an Android build or device test.
