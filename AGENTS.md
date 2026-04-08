# AGENTS Guide for `trip-link`

## Project Snapshot
- Android app module only (`:app`), built with Kotlin + Jetpack Compose + Material 3.
- Entry point: `app/src/main/java/com/example/triplink/MainActivity.kt` -> `DescubreuqTheme` -> `AppNavigation()`.
- UI text and comments are mostly Spanish; keep new UX copy in the same language unless asked otherwise.
- Existing AI instruction sources search (`**/{.github/copilot-instructions.md,AGENT.md,AGENTS.md,CLAUDE.md,.cursorrules,.windsurfrules,.clinerules,.cursor/rules/**,.windsurf/rules/**,.clinerules/**,README.md}`) returned no files.

## Architecture and Flow (what matters first)
- Navigation is centralized in `core/navigation/AppNavigation.kt` using typed destinations (`composable<MainRoutes.X>`).
- Route definitions live in `core/navigation/MainRoutes.kt` as `@Serializable data object` entries; add new routes there first.
- Feature organization: `features/<featureName>/<Feature>Screen.kt` + `<Feature>ViewModel.kt` (example: `features/login/*`).
- Shared UI building blocks live in `core/components` (`FormField`, `GeneralButton`, `GeneralTopBar`, cards/lists).
- Validation/result flow pattern:
  - Validation state via `core/utils/ValidatedField.kt` (used in login/recovery/reset).
  - Action result via `core/utils/RequestResult.kt` + `StateFlow<RequestResult?>` in ViewModel.
  - Screen observes with `collectAsState()` and reacts in `LaunchedEffect` (snackbar/dialog + reset result).
- Current data/domain layers are mostly placeholders (`data/*/FileForSaving.kt`, `domain/*/FileForSaving.kt`); app behavior is UI-local and mocked.

## Conventions Specific to This Repo
- Prefer existing shared components over raw Material widgets for forms/buttons/top bars.
- Styling often uses custom theme colors from `ui/theme/Color.kt` (for example `PrincipalBlue`, `PrincipalRed`).
- Keep Compose previews for reusable components/screens when already present (`@Preview` pattern is common).
- Navigation back-stack cleanup is intentional after login (`popUpTo(MainRoutes.Home) { inclusive = true }`).
- No DI framework (Hilt/Koin/Dagger) is configured; ViewModels are obtained with `viewModel()` directly.

## Build, Test, and Run Workflows
- Windows commands from project root:
  - `./gradlew.bat :app:assembleDebug`
  - `./gradlew.bat :app:testDebugUnitTest`
  - `./gradlew.bat :app:connectedDebugAndroidTest` (requires emulator/device)
- Toolchain details are pinned in `gradle/libs.versions.toml` (AGP 9.1.0, Kotlin 2.2.21, Compose BOM 2024.09.00).
- `app/build.gradle.kts` sets `compileSdk` to Android 36 with `minorApiLevel = 1`; local SDK must match.

## External Integrations and Cross-Component Dependencies
- Remote images are loaded with Coil 3 (`coil3.compose.AsyncImage`) in `core/components/PublicationCard.kt`.
- INTERNET permission is declared in `app/src/main/AndroidManifest.xml` to support image loading.
- Navigation typed routes depend on Kotlin serialization plugin (`org.jetbrains.kotlin.plugin.serialization`).

## Known Repo Realities (do not "fix silently")
- `features/resetpassword/*` exists but is not wired into `AppNavigation` yet.
- `app/src/androidTest/.../ExampleInstrumentedTest.kt` asserts `com.example.descubre_uq`, while app id is `com.example.triplink`.
- Some files/imports appear unused or legacy; avoid broad cleanup unless explicitly requested.

## When Adding New Work
- Add a new screen under `features/<name>` and hook it in `MainRoutes.kt` + `AppNavigation.kt` together.
- Follow existing ViewModel result contract (`StateFlow<RequestResult?>` + reset method) for user feedback.
- Reuse `FormField` and `GeneralButton` to stay visually consistent with login/register/recovery flows.

