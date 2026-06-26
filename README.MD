# Udant

Udant is an Android news and article reader focused on content ownership, offline access, and a distraction-free reading experience.

The application aggregates articles into a unified feed while keeping user data local. Articles can be saved and archived for long-term offline access, allowing readers to preserve content beyond the lifecycle of the original web page.

## Features

* Feed-based article discovery
* Infinite feed loading
* Local article caching with Room
* Saved articles
* Full article archiving (MHT)
* Offline article reading
* Archive management (create, open, delete)
* Archive status tracking
* Archive progress notifications
* Foreground archive service
* Notification navigation to Saved Articles
* Automatic article enrichment
* Modern Jetpack Compose UI
* MVVM architecture
* Dependency Injection with Hilt
* Kotlin Coroutines and Flow

## Tech Stack

* Kotlin
* Jetpack Compose
* Room Database
* Hilt
* Kotlin Coroutines
* StateFlow
* OkHttp
* Jsoup
* Rome RSS Parser
* Android Storage Access Framework (SAF)
* Android Foreground Services
* Android Notifications
* PendingIntent
* WebView

## Architecture

Udant follows an MVVM-based architecture:

```text
UI (Compose)
    ↓
ViewModel
    ↓
Manager / Repository
    ↓
DAO / Services
    ↓
Room Database & Local Storage
```

### Responsibilities

**ViewModel**

* Manages UI state
* Coordinates user actions
* Exposes reactive application state

**Repository**

* Handles data access
* Coordinates caching and persistence
* Provides application data to ViewModels

**Manager**

* Coordinates multi-step workflows
* Handles archive operations and business logic

**DAO**

* Provides local database access

**Services**

* Executes long-running background tasks

## Current Status

### Phase 1 — MVP Stabilization ✅ Completed

Completed:

* Feed loading
* Feed refresh
* Infinite loading
* Local caching
* Saved articles
* Feed session management
* Error handling improvements
* Lifecycle stability fixes

### Phase 2 — Offline Reading System ✅ Completed

Completed:

* Article archiving (MHT)
* Offline article reading
* Archive management (create, open, delete)
* Archive status tracking
* Archive storage location selection
* SAF integration
* Persisted storage permissions
* Archive lifecycle management

### Phase 2.5 — Feature Completion & Reliability 🚧 In Progress

Completed:

* Generic notification architecture
* Notification controller layer
* Archive-specific notification layer
* Foreground service notifications
* Progress notifications
* Determinate and indeterminate progress support
* PendingIntent notification navigation

In Progress:

* Archive queue / DownloadManager
* Archive reliability improvements
* Edge-case handling

## Roadmap

### Phase 3

* Theme settings
* UI/UX improvements
* Reader preferences
* Archive preferences

### Phase 4

* Architecture hardening
* Reliability improvements

### Phase 4.5

* Performance validation
* ANR prevention
* Memory and database optimization

### Phase 5

* Source platform
* Extension architecture
* Source abstraction layer

### Phase 5.5

* Scaling and operational stability
* Multi-source stress testing
* Resource management

## Project Goals

Udant aims to provide:

* Local-first content ownership
* Reliable offline reading
* Source independence
* Fast article access
* Long-term extensibility
* Full user control over content sources

## License

This project is currently under active development.
