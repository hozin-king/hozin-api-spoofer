# Hozin API Spoofer

LSPosed module that spoofs the Android API level for old games/apps.

Many old 32-bit games check `Build.VERSION.SDK_INT` and refuse to run
(or take broken code paths) on new Android versions. This module makes
the app see a fake older API level:

- `Build.VERSION.SDK_INT` → **28** (Android 9)
- `Build.VERSION.RELEASE` → `"9"`
- `Build.VERSION.CODENAME` → `"REL"`
- `Build.VERSION.getPreviewSDKInt()` → `0`

Why API 28: it's the last API before scoped storage (29) and the new
permission model, so old games take their legacy code paths.

## Usage

1. Install the APK
2. Open LSPosed Manager → Modules → enable **Hozin API Spoofer**
3. In scope, check **only** the old games you want to spoof
4. Force-stop and restart the game

Do NOT enable system-wide — spoofing the API level for system apps
or modern apps can break them.

## Build

CI builds the APK on every push (`.github/workflows/build.yml`).
The Xposed API jar (`libs/xposed-api-82.jar`) is compile-only and not
packaged into the APK.
