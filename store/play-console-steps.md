# Calendar Ortodox: Play Console step by step (Part C)

Google renames menus from time to time. If a label differs, look for the closest match, and tell Claude about any question the Console asks that isn't covered here.

Files you will need (all in this repo):
- `app\build\outputs\bundle\release\app-release.aab` (rebuild with the command at the end if missing)
- `store\icon-512.png`, `store\feature-graphic.png`, `store\screenshot-1-month.png`, `store\screenshot-2-zoom.png`
- `store\listing-ro.md` (texts)

---

## 0. Before the Console
1. **Enable GitHub Pages:** github.com/StarkIndustriesApp/CalendarOrtodox → **Settings → Pages** → Source: *Deploy from a branch* → Branch: `main`, folder: `/docs` → Save.
   After 1–2 minutes, check that https://starkindustriesapp.github.io/CalendarOrtodox/privacy.html opens.
2. Make sure `danielbring.writes@gmail.com` works (Google may send verification mail there).

## 1. Create the app
Play Console → **Create app**
| Field | Value |
|---|---|
| App name | `Calendar Ortodox` |
| Default language | Romanian – ro |
| App or game | App |
| Free or paid | Free |
| Declarations | tick Developer Program Policies + US export laws |

## 2. Set up your app (Dashboard → "Set up your app" / Policy → App content)
| Form | Answer |
|---|---|
| **Privacy policy** | `https://starkindustriesapp.github.io/CalendarOrtodox/privacy.html` |
| **App access** | All functionality is available without special access (no login) |
| **Ads** | No, my app does not contain ads |
| **Content rating** | Category: *Reference, News, or Educational* (or the closest non-game category). Answer **No** to violence, sexuality, language, controlled substances, gambling, user interaction/sharing, location sharing, digital purchases. Email: danielbring.writes@gmail.com. Expected result: rated for everyone / PEGI 3 |
| **Target audience and content** | Age groups: **13–15, 16–17, 18 and over** (do NOT tick anything under 13). "Could the app unintentionally appeal to children?" → No |
| **News app** | No |
| **COVID-19 / health apps** | Not applicable / No health features |
| **Data safety** | "Does your app collect or share any of the required user data types?" → **No**. (Encryption in transit and account deletion questions disappear when no data is collected.) |
| **Government app** | No |
| **Financial features** | None |
| **Health** | None |
| **Advertising ID** | No (the app does not use it) |

## 3. Store listing (Grow users → Store presence → Main store listing)
- App name, short description, full description: copy from `store\listing-ro.md`
- App icon: `store\icon-512.png`
- Feature graphic: `store\feature-graphic.png`
- Phone screenshots: `store\screenshot-1-month.png`, `store\screenshot-2-zoom.png`
- Tablet screenshots: leave empty (phone-only app)

**Store settings:** Category **Lifestyle**, tags e.g. Religion; contact email **danielbring.writes@gmail.com**; website optional (GitHub repo URL).

## 4. Countries
Release → Production → **Countries / regions** → add all countries (needed later; closed testing has its own country list, also select all there).

## 5. Closed test (mandatory: 12+ testers, 14 days in a row)
1. Release → Testing → **Closed testing** → **Create track** (or use the default "Closed testing – Alpha").
2. **Testers** tab → create an **email list**, e.g. "Testeri Calendar Ortodox", and add the Gmail addresses (Google accounts) of **at least 12 people, ideally 15–16** in case someone drops out. Save. Set the feedback email/URL to danielbring.writes@gmail.com.
3. **Countries / regions** for the track → all countries.
4. **Create new release** → when asked about app signing, choose **"Use Google-generated key" / Play App Signing (recommended)**. Your existing keystore becomes the **upload key** automatically when you upload the first bundle.
5. Upload `app-release.aab`. Release name: `1.0 (1)`. Release notes (Romanian):
   ```
   <ro-RO>
   Prima versiune: calendarul ortodox 2026–2028, o lună pe ecran, mărire cu două degete sau atingere dublă.
   </ro-RO>
   ```
6. **Review release → Start rollout to Closed testing.** Google reviews it first (hours to a few days).
7. After approval, copy the **opt-in link** from the Testers tab and send it to the testers (text below).
8. **Day 0 = the day the 12th tester has opted in.** Testers must stay opted in for **14 consecutive days**. Ask them to keep the app installed and open it now and then. If someone opts out, the count restarts.

### Invitation text for testers (Romanian)
```
Bună! Am făcut o aplicație Android: „Calendar Ortodox" (calendarul ortodox 2026–2028, offline, fără reclame).
Înainte să o pot publica pe Google Play, Google cere ca cel puțin 12 persoane să o testeze 14 zile la rând.

Te rog:
1. Deschide linkul acesta pe telefonul Android, cu contul Google pe care mi l-ai dat: <LINK DE ÎNSCRIERE>
2. Apasă „Devino tester" / „Become a tester".
3. Instalează aplicația din Google Play (linkul apare pe aceeași pagină).
4. Păstreaz-o instalată cel puțin 14 zile și deschide-o din când în când. Nu te dezabona din test.

Dacă vezi vreo problemă sau ai o idee, scrie-mi la danielbring.writes@gmail.com. Mulțumesc mult!
```

## 6. Apply for production access (after 14 days)
Dashboard → **Apply for production** (appears when the 12 testers / 14 days are met). Draft answers:

**About your closed test**
- *How easy was it to recruit testers?* "Easy: friends and family who use an Orthodox calendar."
- *How did testers engage with the app?* "They used it daily to look up saints, feasts and fasting days, tested swiping between months and zooming in to read the text."
- *Feedback received and how you acted on it:* fill in honestly from what testers report (if none: "No issues reported. Testers confirmed the swipe and zoom gestures work and the text is readable when zoomed.")

**About your app**
- *Intended audience:* "Romanian-speaking Orthodox Christians (Romania, Moldova and the diaspora), 13+."
- *How does your app provide value?* "A simple, offline, ad-free Orthodox church calendar for 2026–2028: one month per screen with the saints of each day, feasts, Sunday readings and fasting days."
- *Expected installs in the first year:* choose a modest range (e.g. under 10,000).

**Production readiness**
- *What changes did you make based on testing?* list any fixes (or "None needed; the app was stable throughout the test.")
- *How did you decide the app is ready?* "All testers used it for 14+ days without crashes or blocking issues; it requests no permissions and collects no data."

Google's review usually takes up to 7 days. After approval: Release → **Production** → Create release → *add from library* the same bundle (or a newer versionCode) → roll out.

---

## Rebuilding the bundle
```powershell
cd C:\personal_workspace\calendar
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat testDebugUnitTest bundleRelease
```
Every new upload needs a higher `versionCode` in `app\build.gradle.kts` (1 → 2 → 3 …).
