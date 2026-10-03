# Invincibles Jarvis

An Android starter assistant with typed commands, Android speech recognition, spoken replies, date/time, web search, YouTube, Google, and Android Settings shortcuts. This version does **not** include a connected generative-AI backend; unsupported commands are reported clearly rather than pretending to execute.

## Build APK using GitHub (recommended from a phone)
1. Extract this ZIP.
2. Create a GitHub repository and upload the *contents* of the `InvinciblesJarvis` folder (so `.github`, `app`, `build.gradle.kts`, and `settings.gradle.kts` are at repository root).
3. Open **Actions** and run **Build Invincibles Jarvis APK** (or push to `main`).
4. Open the completed workflow run, download the `Invincibles-Jarvis-debug` artifact, extract it, and install `app-debug.apk`.

## Build locally
Requires JDK 17, Gradle 8.9, and Android SDK platform/build-tools for API 35. From this directory run:

```bash
gradle --no-daemon assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. On Android/Termux, a complete Android SDK toolchain is not installed by default; GitHub Actions is the supported phone-friendly build route.

## Notes
- Microphone access is requested at runtime when you tap **Speak**.
- Speech recognition depends on an installed Android speech service and may require internet.
- Web actions open the browser; they do not scrape or control other apps.
- No API keys or secrets are embedded in this project.
