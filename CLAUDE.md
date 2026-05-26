# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

mova-testapp is a work-oriented Android test/sandbox app for experimenting with image loading, caching, and processing flows. It's a single-module Compose app — no production concerns, no tests.

## Build & Run

```bash
# Build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Clean build
./gradlew clean assembleDebug
```

No test suites exist in this project.

## Key Versions

| Kotlin | AGP | JVM | compileSdk | minSdk | targetSdk |
|--------|-----|-----|------------|--------|-----------|
| 2.3.20 | 9.1.0 | 17 | 36 | 24 | 36 |

## Architecture

Single `:app` module. All source code lives under `com.example.testapp`.

**Navigation**: Uses Navigation 3 (`nav3-ui` + `lifecycle-viewmodel-navigation3`) with `NavKey` sealed types and `NavDisplay`. Nav keys are `@Serializable` data objects/classes defined alongside their pages (e.g., `Home`, `Product("1")`, `About`, `NavGlideNativeImage`, `NavCropImage`).

**Entry point**: `MainActivity` → `TestappApp()` composable, which sets up `NavigationSuiteScaffold` (3-tab bottom nav: Home, Favorites, Profile) with `NavDisplay` for content routing.

**Page structure**: `Index1Page` (Home), `Index2Page` (Favorites), `Index3Page` (Profile) are the tab root pages. Sub-pages live in `pages/` package (`GlideNativeImagePage`, `CropImagePage`). Navigation into sub-pages pushes `NavKey` entries onto the `NavBackStack`.

**Utility modules** in `funcs/` package:
- `SDWebImageModule(context)` — Glide wrapper: `loadImagePath(url)` downloads and returns cached file path (blocking, run on IO dispatcher), `clearCache()`, `getCacheSize()`
- `FileModule` — Base64 ↔ Bitmap conversion and image cropping via `ImageUtils` (utilcodex)

## Dependencies

- **Glide** (4.12.0) — native image download/caching (used in `SDWebImageModule`)
- **Coil** (2.7.0 compose) — Compose-native image display (`rememberAsyncImagePainter`)
- **utilcodex** (1.31.1) — `ImageUtils` for bitmap ops, `FileUtils` for cache size
- **Logger** (2.2.0 orhanobut) — logging; initialized in `MovaApplication`, extension `Any.d()` in `CommonExts.kt`
- **Navigation 3** (1.0.0) + Material3 adaptive navigation suite

Pattern: Glide downloads to cache file → Coil displays from file path, or manual Bitmap → Base64 → crop pipeline.

## AndroidManifest

`MovaApplication` is the custom Application class (Logger init). Only `INTERNET` permission declared.