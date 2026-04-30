# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Æther is a Kotlin Multiplatform (KMP) travel app targeting Android and iOS, built with Compose Multiplatform. Package ID: `com.oeuvio.aether`.

## Build Commands

```shell
# Debug/release APKs
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:assembleRelease

# Install on connected Android device
./gradlew :composeApp:installDebug

# Unit tests
./gradlew :composeApp:test

# Instrumented Android tests (requires connected device/emulator)
./gradlew :composeApp:connectedAndroidTest

# Android lint
./gradlew :composeApp:lint
```

For iOS: open `iosApp/iosApp.xcodeproj` in Xcode and build from there.

## Architecture

Single Gradle module (`:composeApp`) with three source sets:

- **`commonMain`** — shared Kotlin/Compose code for all platforms. All new business logic and UI goes here.
- **`androidMain`** — Android `actual` implementations and `MainActivity`.
- **`iosMain`** — iOS `actual` implementations and `MainViewController` (embedded in Swift via `UIViewControllerRepresentable`).

Platform abstractions use the Kotlin `expect`/`actual` pattern: declare `expect fun` in `commonMain`, implement `actual fun` in each platform source set.

iOS integration: KMP compiles as a static framework named `ComposeApp`. `ContentView.swift` wraps `MainViewControllerKt.MainViewController()` and `iOSApp.swift` is the SwiftUI `@main` entry.

## Key Versions

| Tool                  | Version        |
|-----------------------|----------------|
| Kotlin                | 2.3.20         |
| Compose Multiplatform | 1.10.3         |
| Android compileSdk    | 37 / minSdk 30 |
| Gradle                | 9.4.1          |

All dependency versions are managed in `gradle/libs.versions.toml` — never hardcode versions in build files.

## Platform Targets

iOS targets are `iosArm64` and `iosSimulatorArm64` only (Apple Silicon Macs + physical devices). No Intel simulator (`iosX64`) support.

## Gradle Notes

Configuration cache and build cache are both enabled. First build is slow; subsequent builds are incremental. Daemon heap is set to 4 GB (`gradle.properties`).
