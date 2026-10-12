# AI Practicum Android QA report

Date: 2026-10-12
App version: 8.12 (`com.aipracticum.app`, versionCode 12)
Branch: `rauf60190-dotcom-android-apk-implementation`

## Audit summary

The repository initially contained only `README.md`, `.gitignore`, and `LICENSE`; no Android application or build workflow was committed. The Android project was taken from the supplied Stage 1 archive. The unmodified bundled HTML initially matched the supplied v8.11 source (SHA-256 `846047bad242903937454a55fa0f509af62f72dfbd57b16cdc6e51db23271a21`); it is now versioned as v8.12 after adding edit support for user-created prompt templates. The supplied attachment remains unchanged. The repository history at audit time consisted of the initial commit `60ba9b8`.

| Priority | Finding | Status |
|---|---|---|
| P0 | No source/build defect that prevents the local debug build was found. | No known P0 |
| P1 | The supplied workflow was nested under the Android subdirectory, where GitHub Actions would not discover it. | Fixed: moved to repository-root `.github/workflows/` |
| P1 | The Android project did not include the required Gradle Wrapper. | Fixed: added the Gradle 8.9 wrapper and distribution checksum |
| P1 | The Android project had no launcher icon resource. | Fixed: added an Android adaptive launcher icon |
| P1 | Android device/WebView runtime behavior has not been exercised; device verification remains a release gate. | Open / not tested |
| P2 | App-state JSON import/export controls and handlers are absent; local storage serialization remains in use for app data. | Confirmed in bundled HTML |

## Results

| Check | Status | Evidence / limitation |
|---|---|---|
| Supplied v8.11 source preserved as baseline | PASS | Initial bundled source SHA-256: `846047bad242903937454a55fa0f509af62f72dfbd57b16cdc6e51db23271a21`; Android bundle now includes the v8.12 template-editing change. |
| Final v8.12 HTML bundle | PASS | SHA-256: `42d762864d70789bd5cee9738a38e541c148c8e70bfa7201222c8901a5bf7229` |
| HTML parser | PASS | Parsed using Python `html.parser` |
| JavaScript syntax and browser startup | PASS | Chromium headless loaded the HTML, executed its inline script, and rendered the home view without a syntax diagnostic. This is a desktop browser check, not Android WebView QA. |
| Android XML resources | PASS | Manifest, theme, adaptive icon, vector drawable, and color resource parsed as XML |
| JSON state import/export UI and handlers | PASS | No file-input/import/export controls or JSON-file MIME handler found in the bundled app. JSON serialization for local app storage remains intentionally present. |
| Local state/template/history/Markdown code paths | PASS (static) | Web Storage, custom templates, prompt history, and three Markdown download call sites are present. This does not prove device persistence or native download behavior. |
| Android permissions and bundled assets | PASS (static) | Manifest declares no permissions; HTML, CSS, and JavaScript are bundled, with no external resource references found. |
| Initial build using the machine's default Java | FAIL (environment, resolved) | The machine exposed Java 8; Android Gradle Plugin 8.7.3 requires a newer JVM. The build was rerun successfully with temporary JDK 17. |
| Gradle debug build | PASS | `gradlew.bat --no-daemon --stacktrace :app:assembleDebug`, using JDK 17 and Android SDK 35 |
| APK exists and is non-empty | PASS | `AI_Practicum_Android/app/build/outputs/apk/debug/app-debug.apk`, 3,201,953 bytes |
| APK metadata, launcher activity, and debug signature | PASS | Package `com.aipracticum.app`, version 8.12, launcher activity `MainActivity`; `apksigner verify` succeeded. |
| GitHub Actions workflow execution and artifact upload | NOT TESTED | Workflow is configured at repository root but was not run on GitHub; no hosted artifact link is claimed. |
| APK install / app launch on Android | NOT TESTED | `adb devices -l` found no attached device or emulator. |
| Android WebView local-origin startup and offline runtime | NOT TESTED | Static configuration uses WebViewAssetLoader at `https://appassets.androidplatform.net/` and no INTERNET permission; device runtime was unavailable. |
| Main lessons, navigation, quiz, and learning flows | NOT TESTED | No Android device/emulator available. |
| Progress persistence after app close/relaunch | NOT TESTED | Local storage code and stable WebView origin are configured, but persistence needs device testing. |
| Custom template create and edit | PASS (desktop smoke test) | Chromium created a template, reopened it in the editor, saved changed fields, and rendered the edited value. Android behavior remains untested. |
| Prompt lab version history and lesson progress action | PASS (desktop smoke test) | Chromium saved one prompt version and completed a lesson. Android behavior and persistence after app restart remain untested. |
| Native Markdown download to Android Downloads | NOT TESTED | MediaStore bridge compiled; actual file creation/opening requires device testing. |
| Android Back, keyboard resizing, narrow screens, Logcat | NOT TESTED | Requires Android runtime. `adjustResize` is declared for the activity. |

## Release readiness

The debug APK is built and signature-verified, but this is not yet a verified test release: install, WebView runtime, persistence, downloads, and device interaction remain untested. Do not promise recovery of local data after app data is cleared or the app is uninstalled; the app has no JSON backup or cloud sync.
