# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Single-module Android compass app (`:app`), Kotlin + XML Views with ViewBinding (no Compose). Source package/namespace is `com.naohstudio.compass`, but the published `applicationId` is `com.naohstudio.compassfree` — don't conflate them. Monetized with an AdMob banner gated by UMP consent. Portrait-only, single activity (`feature/home/MainActivity`).

## Commands

```bash
./gradlew assembleDebug            # debug APK (uses AdMob test banner ID via BuildConfig.DEBUG)
./gradlew installDebug             # install on connected device
./gradlew bundleRelease            # release AAB (R8 minify + resource shrinking enabled)
./gradlew lint
./gradlew test                     # JVM unit tests
./gradlew test --tests "com.naohstudio.compass.ExampleUnitTest"   # single test class
./gradlew connectedAndroidTest     # instrumented tests (needs device/emulator)
```

Only template tests exist. Heading logic depends on real sensors (`TYPE_ROTATION_VECTOR`), so verify behavior on a physical device; emulators give poor compass data.

No `signingConfig` is defined in Gradle (signing happens outside the build script, e.g. Android Studio's "Generate Signed Bundle"). Signed outputs in `app/release/` (`.aab`, `.apk`) are committed to git. Bump `versionCode`/`versionName` in `app/build.gradle.kts` for each release.

## Architecture

Heading pipeline (spans several files):

1. `MainActivity.CompassSensorEventListener` receives rotation-vector events (`SENSOR_DELAY_GAME`), registered in `onResume`/unregistered in `onPause`. Low sensor accuracy shows a one-shot "figure 8" calibration toast.
2. `feature/utils/MathUtils` turns the rotation vector into a magnetic azimuth, remapping axes for display rotation. Two algorithms:
   - `calculateCoordinates` — iOS-style heading: uses the top-of-screen axis when flat and the back-camera axis when upright (avoids gimbal lock of `getOrientation()`). Used once a location/declination is known.
   - `calculateLegacyCoordinates` — original `getOrientation()` azimuth, used when no location is available.
3. `CompassViewModel.updateMagneticAzimuth` adds magnetic declination (true north), applies a low-pass filter on sin/cos (to avoid 359°→0° wraparound spins), and only publishes to `coordinates` LiveData when the change ≥ 0.1°.
4. `feature/view/CompassView` (a `ConstraintLayout`) rotates the rose image and repositions the cardinal/degree `TextView`s with `ConstraintSet.constrainCircle` on every update; text sizes and radii are computed as fractions of view width from float `dimens` (`*_text_size_factor`, `*_text_ratio`). Also emits haptic ticks every 2°.

True north without location permission: `feature/data/IpLocationService` fetches approximate lat/lon from the public IP (`ipwho.is`, fallback `ipapi.co`) using plain `HttpURLConnection`; the ViewModel computes declination with `GeomagneticField` and caches it in SharedPreferences (`compass_prefs` / `declination`). The app intentionally requests only `INTERNET` — don't add location permissions.

Other notes:
- `coreproject/base/BaseActivity<VB>` drives lifecycle hooks: `bindingView()` → `initConfig` → `initObserver` → `initListener`.
- Compass themes: `feature/model/CompassStyle` enum maps each style to a rose drawable (`compass_background_*`), label string, and optional rose text color (`null` = default colors from the layout). Selection is persisted in `compass_prefs` / `compass_style` and chosen via a horizontal RecyclerView (`CompassStyleAdapter`). Adding a style = new enum entry + drawable + `compass_style_*` string in every locale.
- Light/dark: `values-night/` resources plus an in-app switch using `AppCompatDelegate`; the manual choice is reset to follow-system when the device's night mode changes (tracked in a process-lifetime companion field).
- Ads: `Ads.getBannerAds()` returns the Google test ID in debug and the real unit ID in release; `BannerHelper` loads an adaptive banner only after `GoogleMobileAdsConsentManager.gatherConsent` and when `canRequestAds`.
- Hilt is a dependency and `MainActivity` has `@AndroidEntryPoint`, but the Hilt Gradle plugin, kapt compiler, and `@HiltAndroidApp` Application are not set up — DI is effectively unused. Wire it up fully before relying on injection.
- Java/JVM target 1.8, AGP 9.5 with built-in Kotlin (no `kotlin-android`/kapt plugins), Gradle 9.9, compile/target SDK 36, min SDK 24.
- R8 runs in strict full mode with optimized resource shrinking (AGP 9 defaults). Old transitive libraries can lose reflectively-created classes — `androidx.work:work-runtime` is pinned for this reason (the ads SDK's WorkManager 2.7 crashed release on start). Always smoke-test a minified release build after dependency/AGP changes.

## Localization

Strings are translated into ~22 locales (`values-ar`, `values-de`, … `values-zh`). Any new user-facing string in `values/strings.xml` must be added to every `values-*/strings.xml`. Degree labels and the `degrees` format string are `translatable="false"` and live only in `values/`.
