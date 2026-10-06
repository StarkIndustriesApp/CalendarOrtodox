# Calendar Ortodox: Google Play release plan

Status: **Parts A + B done (2026-10-06); Part C (Play Console) is next. See `store/play-console-steps.md`.**
Date: 2026-10-06

## Context
The app (currently "UnguIsReligious", package `ro.ungu.unguisreligious`) is built, tested and installed on the user's Pixel 9 Pro (see `calendar ortodox plan and execution.md`). The goal is now to publish it on Google Play under a new name and with a new icon.

## Decisions (from Q&A)
| Topic | Decision |
|---|---|
| App name / label | **Calendar Ortodox** |
| Package ID | **`ro.calendarortodox.app`** (permanent after the first upload) |
| Play account | Personal, created after 13 Nov 2023 → **closed test with ≥12 testers for 14 consecutive days** is required |
| Testers | User has 12+ people |
| New icon | User provides a new square **SVG** in the project folder |
| Public contact email | A **new dedicated address** created by the user (personal Gmail stays private) |
| Listing language | **Romanian** only |
| Privacy policy | Page in this repo, served by **GitHub Pages** |
| Screenshots + feature graphic | **Claude makes them** (screenshots from the Pixel via adb; graphic from icon + name); user approves |
| Image rights | User confirms they made the images / have permission |
| Price / countries | **Free, all countries** |
| Target audience | **13+** (no Families policy) |
| Category | **Lifestyle** |
| App signing | **Play App Signing with a Google-generated key**; existing keystore = **upload key** |

Current requirements checked (2026-10-06): new apps must target API ≥ 36 (ours: 37 ✅). Play requires an Android App Bundle (.aab).

## Part A: code changes (Claude)
1. **Rename the package** to `ro.calendarortodox.app`:
   - `app/build.gradle.kts`: `namespace` + `applicationId`
   - move the Kotlin sources `ro/ungu/unguisreligious/` → `ro/calendarortodox/app/` (main + test), update the `package` lines
   - the vendored PhotoView package (`com.github.chrisbanes.photoview`) stays unchanged
2. **Rename the app**: `strings.xml` `app_name` = `Calendar Ortodox`; `settings.gradle.kts` `rootProject.name = "CalendarOrtodox"`; theme name `Theme.CalendarOrtodox`.
3. **New icon**: convert the user's SVG into the adaptive icon vectors (background + foreground, safe-zone check), replacing the current ones.
4. **Version**: `versionCode = 1`, `versionName = "1.0"` (first Play release; the package is new, so it starts fresh).
5. Run the unit tests, build the release **AAB** (`bundleRelease`) and the APK, and verify the package, label and signature.
6. **Phone:** uninstall the old `ro.ungu.unguisreligious` and install the new APK (the new package counts as a different app). Re-test the gestures quickly.
7. Update `.gitignore` if needed, commit, push.

## Part B: store assets (Claude, user approves)
1. **Play icon 512×512 PNG**: render the new SVG headless with Microsoft Edge (installed, v154) → PNG.
2. **Feature graphic 1024×500 PNG**: HTML layout (icon + "Calendar Ortodox" + calendar preview on a matching background) rendered with Edge.
3. **Phone screenshots** (at least 2, from the Pixel via adb): normal month view, zoomed view, maybe a second month. Saved to a `store/` folder in the repo.
4. **Store texts in Romanian** (saved in `store/listing-ro.md`): title (≤30), short description (≤80), full description (≤4000). User reviews.
5. **Privacy policy page** (`docs/privacy.html` or `docs/index.md`, Romanian, plus a short English version): the app collects, stores and shares no data, has no internet permission, no ads, no accounts; contact = the new dedicated email.

## Part C: Play Console (user does it, Claude gives exact step-by-step text)
1. Create the dedicated contact email; enable **GitHub Pages** (repo Settings → Pages → branch `main`, folder `/docs`).
2. Play Console → **Create app**: name "Calendar Ortodox", default language Romanian, App, Free, accept declarations.
3. **App content** forms: privacy policy URL, ads = No, app access = all features available without login, content rating questionnaire (reference/religious, no violence etc.), target audience 13+, Data safety = no data collected or shared, government app = No, financial features = No, health = No, news = No.
4. **Store listing**: paste the texts, upload the icon, feature graphic and screenshots, category Lifestyle, contact email.
5. **Closed testing track**: create a tester list (Google group or email list) with ≥12 testers, upload `app-release.aab`, accept Play App Signing (Google-generated key), send the opt-in link to testers. Testers install it from Play and **stay opted in for 14 days**.
6. After 14 days → **apply for production access** (the three-part questionnaire; Claude drafts answers) → Google review (usually ≤7 days) → **production release**.

## What Claude will ask the user for, and when
| When | Needed |
|---|---|
| Part A step 3 | the new icon SVG (file name in the project folder) |
| Part A step 6 | phone connected via USB, OK to uninstall the old app |
| Part B step 5 | the new dedicated contact email address |
| Part B | approval of the icon PNG, feature graphic, screenshots and texts |
| Part C | user performs the Console steps; reports any question the Console asks |

## Timeline estimate
Parts A + B: one session. Part C: Console setup ~1–2 hours; closed test **14 days**; production review up to ~7 days. Earliest public release ≈ 3 weeks after the closed test starts.

## Risks / notes
- The package ID cannot change after the first upload, so Part A must be finished before any upload.
- The upload keystore (`C:\Users\Daniel\keystores\unguisreligious.jks` + `keystore.properties`) must be backed up. With Play App Signing, a lost upload key can be reset through Google support.
- During the 14 days, testers who opt out restart the count, so recruit a few more than 12.
- Free apps cannot be changed to paid later.
