# Do dua

Android app for reading authentic **duas** and **dhikr** in Arabic, with Bangla translation, transliteration, notes, benefits, and sources. Content is bundled locally, so the app works fully offline.

Package: `com.syntax.dodua` · Version: `1.0`

## Features

- **Home** — Hijri date, dhikr of the day, topic shortcuts (sleep, toilet, food, travel, mosque), recommended duas, and a short “today’s dhikr” list
- **Dhikr** — Morning, evening, and after-salah collections with category tabs
- **Dua** — Daily and selected duas with category tabs
- **Reader** — Arabic (Noto Naskh Arabic), Latin transliteration, Bangla meaning, notes, benefits, and hadith/source references
- **Tap-to-count** — Repeats parsed from notes (Arabic or Bangla digits) when a target count is present
- **Light / dark theme** — Toggle on home, lists, and the reader; preference is saved
- **Splash screen** — Brief branded launch before the main tabs

## Content

JSON lives under `app/src/main/java/com/syntax/dodua/data/` and is packaged as **assets**.

| Path | Role |
|------|------|
| `core/languages.json` | Supported languages (`bn`) |
| `core/categories.json` | Category names and slugs |
| `dua-dhikr/morning-dhikr/bn.json` | Morning dhikr |
| `dua-dhikr/evening-dhikr/bn.json` | Evening dhikr |
| `dua-dhikr/dhikr-after-salah/bn.json` | Dhikr after salah |
| `dua-dhikr/daily-dua/bn.json` | Everyday duas |
| `dua-dhikr/selected-dua/bn.json` | Selected duas |

Each item typically includes `title`, `arabic`, `latin`, `translation`, `notes`, `benefits` (or `fawaid`), and `source`.

`ContentRepository` loads these files, splits them into dhikr vs dua, and picks “of the day” items from the day of year.

## Architecture

```
SplashActivity → MainActivity (bottom nav)
                   ├─ HomeFragment
                   ├─ DhikrFragment
                   └─ DuaFragment
ContentActivity   ← opened with a content id (and optional topic search)
```

| Piece | Purpose |
|-------|---------|
| `DoDuaApp` | Applies saved night mode on startup |
| `ContentRepository` | Singleton JSON loader and filters |
| `ContentItem` | Serializable dua/dhikr model |
| `ThemeSettings` | SharedPreferences + `AppCompatDelegate` |
| `SystemBars` | Status/navigation bar styling |

UI is **Java 11**, **View Binding**, Material Components, ConstraintLayout, and RecyclerView. No network permission.

## Requirements

- Android Studio (Ladybug / AGP **8.13.2** or compatible)
- JDK **11**
- Android SDK **36**
- Device or emulator: **API 29+** (Android 10)

## Build & run

Open the project in Android Studio, let Gradle sync, then run the `app` configuration.

From the command line:

```bash
./gradlew assembleDebug
```

Install the debug APK:

```bash
./gradlew installDebug
```

## Adding content

1. Add or edit a JSON array in the matching `dua-dhikr/<slug>/bn.json` file.
2. Register a new collection in `core/categories.json` **and** in `DHIKR_SLUGS` or `DUA_SLUGS` inside `ContentRepository`.
3. Rebuild so assets are packed.

To add another language later, add a locale object in `languages.json` and a parallel `xx.json` next to `bn.json`, then extend the loader (currently Bangla-only).

## Tech stack

- Android Gradle Plugin 8.13.2
- AndroidX AppCompat, Activity, Fragment
- Material 1.14
- ConstraintLayout, RecyclerView
- JUnit / Espresso (default test stubs)

## License

Content is drawn from well-known hadith collections (e.g. Sahih Bukhari, Sahih Muslim, At-Tirmidhi) as cited in each JSON `source` field. App code is provided as-is in this repository.
