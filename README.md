# AI Practicum

AI Practicum is a standalone Russian-language HTML learning app with an Android WebView wrapper. The Android source project is in [`AI_Practicum_Android/`](AI_Practicum_Android/); its HTML, styles, and JavaScript are bundled in the APK, and learning state is stored locally by WebView.

## Build a debug APK

Run **Actions → Build AI Practicum APK → Run workflow** on GitHub, then download the `ai-practicum-debug-apk` artifact from the completed run. The workflow can also be started by pushing changes to `main` under `AI_Practicum_Android/`. The artifact is a debug build for testing, not a signed release APK.

For local builds, open `AI_Practicum_Android/` in Android Studio with JDK 17 and Android SDK Platform 35 installed, or run `gradlew.bat assembleDebug` on Windows (`bash ./gradlew assembleDebug` on macOS/Linux). The APK is written to `AI_Practicum_Android/app/build/outputs/apk/debug/app-debug.apk`.

App data remains on the device and is not backed up or synchronized. Clearing app data or uninstalling the app can permanently delete progress, templates, bookmarks, and history. Markdown exports are separate user-created documents saved through Android's Downloads collection.

See the [Android QA report](AI_Practicum_Android/docs/QA_REPORT.md) for verified checks and device tests still outstanding.
