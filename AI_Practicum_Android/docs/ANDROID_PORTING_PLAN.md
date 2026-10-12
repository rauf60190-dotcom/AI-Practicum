# Android porting plan — AI Practicum

## Stage 1 — wrapper scaffold (current)
- Bundle the adapted v8.12 HTML locally.
- Use WebViewAssetLoader origin for local persistence.
- Enable JavaScript and DOM storage only as required by the existing app.
- Disable file/content access, universal file URL access, mixed content, and network permission.
- Add a narrow native bridge for text/Markdown downloads.

## Stage 2 — build and runtime verification
- Build debug APK with SDK 35 / JDK 17 (Android Studio or GitHub Actions workflow).
- Install on the target Android phone.
- Verify startup, route navigation, lessons, bookmarks, quizzes, prompt lab, project workspace and pipeline.
- Verify localStorage after activity recreation, process kill, and relaunch.
- Verify downloads land in Downloads/AI Practicum and content is UTF-8.
- Verify Android Back, keyboard, orientation, and small screen layout.

## Stage 3 — hardening
- Fix issues found in Stage 2; increment version without overwriting previous files.
- Add app icon and final branding.
- Validate upgrade behavior and data retention.
- Create signed release APK only after regression passes.

## Known product tradeoff
JSON import/export has been removed by product decision. Local data can be lost if the app's data is cleared or the app is uninstalled; no cloud sync or user-facing backup is included.
