# Æther - AI Agent Guide

Welcome to the Æther project. This document provides essential context for AI agents to understand the project structure, technology stack, and development conventions.

## Project Overview
**Æther** is a modern travel application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It targets Android and iOS with a high degree of code sharing for both business logic and UI.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Compose Multiplatform (Jetpack Compose for Desktop/Android/iOS)
- **Design System:** Material 3
- **Dependency Management:** Gradle Version Catalog (`gradle/libs.versions.toml`)
- **Lifecycle:** AndroidX Lifecycle (Multiplatform)

## Project Structure
- `/composeApp`: The main module containing the shared code and the Android application entry point.
    - `src/commonMain`: Shared UI (Compose) and business logic.
    - `src/androidMain`: Android-specific implementations and `MainActivity`.
    - `src/iosMain`: iOS-specific implementations (e.g., `MainViewController`).
- `/iosApp`: The Xcode project for the iOS application, which wraps the shared KMP framework.
- `/gradle`: Contains the Gradle wrapper and dependency version catalog.

## Development Conventions
1. **Shared First:** Always prioritize placing logic and UI in `commonMain`.
2. **Platform Specifics:** Use `expect`/`actual` declarations or interfaces for platform-specific APIs (found in `androidMain` and `iosMain`).
3. **Dependency Management:** Add all dependencies to `gradle/libs.versions.toml` and reference them using `libs.*` in build scripts.
4. **UI Guidelines:** Follow Material 3 principles and use the `compose.material3` library.
5. **KMP Best Practices:**
    - Use `kotlinx.coroutines` for asynchronous programming.
    - Use `kotlinx.serialization` if JSON parsing is needed.
    - Keep `iosApp` as a thin wrapper around the shared Compose code.

## Key Files
- `composeApp/src/commonMain/kotlin/com/oeuvre/aether/App.kt`: Main entry point for the shared UI.
- `gradle/libs.versions.toml`: The source of truth for all library versions.
