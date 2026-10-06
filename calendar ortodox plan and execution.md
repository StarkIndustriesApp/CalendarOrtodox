# Calendar Ortodox: plan and execution

Session date: 2026-10-05
Project folder: `C:\personal_workspace\calendar`
GitHub: https://github.com/StarkIndustriesApp/CalendarOrtodox (public)

---

## 1. What was asked

A native Android app named **UnguIsReligious** that shows an Orthodox calendar, one month per screen. It uses 36 pre-made images (`imagini\YYYY_MM_monthname.png`, 1080×2400, January 2026 – December 2028).

Requirements given at the start:

- **Startup:** opens on the current month (from the phone's date), full screen. Dates before Jan 2026 open Jan 2026; dates after Dec 2028 open Dec 2028.
- **Navigation:** swipe left = next month, swipe right = previous month. At Jan 2026 / Dec 2028 an extra swipe does nothing (no message, no bounce, no error).
- **Zoom:** pinch to zoom. While zoomed, a one-finger drag pans and never changes the month. Swiping months only works zoomed out. Return to normal by pinching out or by double-tap. Double-tap in normal view zooms in.
- **Fitting:** the whole image is always visible, never cropped. Leftover space is plain white (#FFFFFF).
- **Display:** only the image. No toolbar, title or buttons. System bars hidden (immersive). Exit with system gestures.
- **Orientation:** always portrait.
- **Offline:** images bundled, no internet permission.
- **Minimum Android:** 14 (API 34).
- The folder `Calendar_Ortodox_date_si_iconite` is reserved for a later version (not used).

Working rules: plan first, verify the environment, ask questions with numbered options, and implement only after "OK, implement".

### Decisions made during Q&A

| Topic | Decision |
|---|---|
| UI toolkit | Classic Views: `ViewPager2` + PhotoView (source copied into the project) |
| Language | Kotlin (the copied PhotoView files stay Java) |
| Swipe animation | Slide follows the finger; snaps back if released early |
| Return from background | Keep the month being viewed; a fresh launch shows the current month |
| Camera cutout | Image kept clear of it (white band at top) |
| Zoom levels | Max 4×; double-tap = 2.5× at the tap point; double-tap again = back to 1× |
| Package name | `ro.ungu.unguisreligious` |
| Launcher label | `UnguIsReligious` |
| Icon | From user-provided `icon.svg` |
| Build type | Release APK, R8 minify + resource shrinking, signed via Gradle |
| Keystore | Outside the project: `C:\Users\Daniel\keystores\unguisreligious.jks`. Password in git-ignored `keystore.properties` (created by Claude at the user's request) |
| Images | Copied unchanged into `app\src\main\assets\months\` |
| Env variables | Nothing permanent; `JAVA_HOME` set per build command, `sdk.dir` in `local.properties` |
| Toolchain | AGP 9.4.0 / Gradle 9.6.0 / Kotlin 2.3.20, compileSdk & targetSdk 37, minSdk 34, Java 17 target, Build-Tools 36.0.0 |
| Git | Repo + one initial commit; `imagini/` and `Calendar_Ortodox_date_si_iconite/` ignored |
| Install method | USB cable + adb |
| Verification | Build + emulator smoke test |

---

## 2. Environment found (Windows 11)

- Android Studio 2026.2.1 "Rabbit"; bundled JDK (JBR) 25.0.3 at `C:\Program Files\Android\Android Studio\jbr`
- SDK at `C:\Users\Daniel\AppData\Local\Android\Sdk`: platform `android-37.0`, sources 37, build-tools 36.0.0, platform-tools (adb 37.0.1), emulator, API 37 system image
- `JAVA_HOME`, `ANDROID_HOME`, `ANDROID_SDK_ROOT` **not set** (left that way on purpose). java/adb are not on PATH
- No Git identity was configured globally. A repo-only identity was set (see section 4)
- All chosen versions confirmed compatible (AGP 9.4.0 needs Gradle ≥ 9.6.0 and supports up to API 37; Gradle 9.6 runs on Java 25)

---

## 3. What was achieved

### App built and working
- APK: `app\build\outputs\apk\release\app-release.apk` (~23.9 MB), signed (v2 scheme) with the new keystore, **no permissions requested**
- **Installed on the user's Pixel 9 Pro (Android 17, 1280×2856)**. It launched on October 2026 in full screen with no errors

### Project structure (key files)
```
settings.gradle.kts, build.gradle.kts, gradle.properties, gradle/libs.versions.toml
gradle/wrapper/* + gradlew(.bat)          Gradle 9.6.0, SHA-256 verified
app/build.gradle.kts                      signing read from keystore.properties
app/src/main/AndroidManifest.xml          portrait, no permissions, supportsRtl=false
app/src/main/assets/months/*.png          36 images
app/src/main/java/ro/ungu/unguisreligious/
    MainActivity.kt        immersive mode, cutout padding, pager setup, start month
    MonthCatalog.kt        file name -> month index, clamping to the range
    MonthPagerAdapter.kt   PhotoView pages; locks swiping while zoomed
    MonthImageLoader.kt    background decoding (hardware bitmaps), cache of 5
app/src/main/java/com/github/chrisbanes/photoview/   PhotoView 2.3.0 (Apache 2.0) + LICENSE
app/src/main/res/          adaptive icon (vector conversion of icon.svg), theme, strings
app/src/test/.../MonthCatalogTest.kt      9 unit tests
icon.svg
```

### Changes made to the copied PhotoView (marked `UnguIsReligious:` in the code)
1. `AppCompatImageView` → `android.widget.ImageView` (no AppCompat dependency)
2. A second finger landing blocks the pager from taking the gesture (a pinch never becomes a swipe)
3. Double-tap toggles between 1× and 2.5× (instead of cycling through min, medium and max)
4. A gesture ending just above 1× (below 1.05×) snaps back to exactly 1×

### Verification done
- **Unit tests:** 9/9 pass (month selection, clamping before 2026 and after 2028, file name parsing, gap detection, bundled assets match the expected names)
- **Emulator** (API 37, 1080×2400), using a temporary instrumented test with real touch gestures (removed again before the commit). All 6 passed:
  - starts on the current month
  - 5 swipes each way change the month correctly
  - Jan 2026 / Dec 2028 limits do nothing
  - double-tap zooms to 2.5× and locks swiping; full-width drags while zoomed only pan; double-tap returns to 1× and swiping works again
  - pinch zooms in; pinching back restores swiping
  - zoom is capped at 4×
- **Also checked on the emulator:** portrait lock (forced landscape stays portrait), white letterboxing on a 16:9 screen, launcher icon rendering, "No permissions requested" in App info
- **Investigated:** some `adb input swipe` gestures were dropped. Diagnostic logging showed the emulator stalls 40–110 ms just before the injected finger lifts, which Android correctly treats as "released before halfway, snap back". This is an emulator artifact, not an app bug
- **Not tested on the device itself:** out-of-range dates (the emulator image doesn't allow changing the date; covered by unit tests). Gestures on the real phone have not been confirmed by the user yet

### Git / GitHub
- Repo-only identity: `StarkIndustriesApp <286035801+StarkIndustriesApp@users.noreply.github.com>` (GitHub noreply address, so the Gmail address stays private)
- One commit on `main`, pushed to `origin` (https://github.com/StarkIndustriesApp/CalendarOrtodox), 74 files
- **Not in Git / not on GitHub:** the keystore (`.jks`), `keystore.properties` (password), `local.properties`, build output, `imagini/`, `Calendar_Ortodox_date_si_iconite/`. A scan confirmed no password or key in the history

### Test emulator
- A separate AVD `UnguTest_API_37` was created. The user's existing `Medium_Phone_API_37.0` was not touched. The test AVD can be deleted in Android Studio's Device Manager

---

## 4. Where to pick up next

### Immediate to-dos
1. **Back up the signing key and password:** copy `C:\Users\Daniel\keystores\unguisreligious.jks` and `C:\personal_workspace\calendar\keystore.properties` to a safe place (USB stick, password manager). Without them, updates can't be installed over the existing app.
2. **Test on the phone by hand:** swipes, double-tap zoom, pan while zoomed (the month must not change), pinch in and out, both range ends, rotation. Report anything odd.
3. **Image rights:** the repo is public, so the calendar images can be downloaded by anyone. Make the repo private on GitHub if needed.
4. **This file** is committed and pushed to GitHub (2026-10-06).

### How to rebuild and reinstall
```powershell
cd C:\personal_workspace\calendar
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat testDebugUnitTest assembleRelease
& 'C:\Users\Daniel\AppData\Local\Android\Sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\release\app-release.apk
```
- With AGP 9, unit tests run only on the debug variant (`testDebugUnitTest`; `testReleaseUnitTest` does not exist)
- For each new release, increase `versionCode` (and `versionName`) in `app/build.gradle.kts`

### Next goal: publish on Google Play
Not started yet. Steps to plan in the next session:
1. **Google Play Console account** (one-time registration fee, identity verification). New personal accounts must run a **closed test with at least 12 testers for 14 days in a row** before they can publish to production. Check the current rules in the Play Console.
2. **Build an Android App Bundle (.aab)** instead of an APK: `.\gradlew.bat bundleRelease` → `app\build\outputs\bundle\release\app-release.aab`. Play requires AAB for new apps.
3. **Play App Signing:** Google holds the final app signing key. The existing keystore (`unguisreligious.jks`) can serve as the **upload key**. Decide this when creating the app in the Console.
4. **Store listing:** title, short and long description, app icon 512×512 PNG (rasterize `icon.svg`), feature graphic 1024×500, at least 2 phone screenshots, category, contact email.
5. **Policy forms:** privacy policy URL (the app collects no data, but the Console may still require one; a simple page on GitHub Pages works), Data safety form ("no data collected or shared"), content rating questionnaire, target audience, ads declaration ("no ads").
6. **Rights to the calendar images:** you must own the content or have permission to publish it.
7. **Version bump** for every upload (`versionCode` must increase).
8. Before publishing, re-check the target API requirement in the Play Console (the app targets API 37, which is currently the newest).

### Possible next version (ideas, not decided)
- Use the reserved folder `Calendar_Ortodox_date_si_iconite` (data and icons) for a richer, data-driven calendar
- Add the years after 2028: drop new `YYYY_MM_monthname.png` files into `app/src/main/assets/months/`. `MonthCatalog` derives the range from the files and requires the months to be contiguous; also update `MonthCatalogTest`
- Optional: a monochrome (themed) launcher icon; a debug build variant with an id suffix so it can sit next to the release build

### Known OS behavior (not app bugs)
- Android shows a one-time "Viewing full screen" hint on first launch
- An edge swipe briefly shows the system bars, which then hide again automatically
