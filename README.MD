# Udant

Udant is an offline-first Android news and article reader focused on content ownership, reliability, and a distraction-free reading experience.

Instead of treating articles as temporary content, Udant allows users to save, archive, and manage articles locally, ensuring long-term access even after the original webpage becomes unavailable.

---

# Features

### Reading

- Feed-based article discovery
- Infinite feed loading
- Automatic article enrichment
- Saved articles
- Offline article reading
- Local-first article storage

### Offline Archive System

- Full webpage archiving (MHT)
- Archive creation
- Archive opening
- Archive deletion
- Archive status tracking
- Archive lifecycle management
- Persistent storage permissions (SAF)

### Background Job System

- Generic background job architecture
- Persistent download queue
- Generic request/response pipeline
- Serialized job payloads
- Event-driven communication
- Extensible payload codec system
- Feature-specific job handlers

### Notifications

- Foreground archive service
- Archive progress notifications
- Determinate & indeterminate progress
- Notification navigation
- Generic notification architecture

### Modern Android

- Jetpack Compose UI
- MVVM architecture
- Hilt Dependency Injection
- Kotlin Coroutines & Flow
- Room persistence

---

# Tech Stack

- Kotlin
- Jetpack Compose
- Room Database
- Hilt
- Kotlin Coroutines
- Flow / StateFlow
- Kotlin Serialization
- OkHttp
- Jsoup
- Rome RSS Parser
- Android Storage Access Framework (SAF)
- Android Foreground Services
- Android Notifications
- PendingIntent
- WebView

---

# Architecture

Udant follows an MVVM architecture combined with feature-specific managers and a generic background job system.

```text
                    UI (Compose)
                          │
                          ▼
                     ViewModel
                          │
                          ▼
                     Manager Layer
            ┌─────────────┴─────────────┐
            ▼                           ▼
      Repository                  JobManager
                                        │
                                        ▼
                               DownloadManager
                           ┌─────────┴─────────┐
                           ▼                   ▼
                  Payload Codec        Job Handler
                                              │
                                              ▼
                                        Feature Job
                                              │
                                              ▼
                                           Service
                                              │
                                              ▼
                                   Room & Local Storage
```

## Layer Responsibilities

### ViewModel

- Manages UI state
- Handles user interactions
- Exposes reactive application state

### Repository

- Provides application data
- Coordinates persistence
- Manages local caching

### Manager

- Coordinates feature workflows
- Orchestrates business logic
- Bridges UI and infrastructure

### JobManager

- Provides a unified background job API
- Routes requests to specialized managers

### DownloadManager

- Maintains the persistent download queue
- Executes queued jobs
- Publishes job events
- Delegates work to feature handlers

### Job Handler

- Converts generic jobs into feature-specific execution
- Translates feature results back into generic responses

### Payload Codec

- Serializes feature payloads
- Deserializes persisted payloads
- Keeps the job system independent of feature models

### Services

- Execute long-running Android operations
- Handle platform-specific functionality

---

# Current Status

## Phase 1 — MVP Stabilization ✅ Completed

- Feed loading
- Feed refresh
- Infinite scrolling
- Local caching
- Saved articles
- Feed session management
- Lifecycle improvements
- Error handling improvements

---

## Phase 2 — Offline Reading System ✅ Completed

- MHT article archiving
- Offline reading
- Archive management
- Archive lifecycle
- Archive status tracking
- SAF integration
- Storage permission persistence

---

## Phase 2.5 — Background Infrastructure 🚧 In Progress

### Completed

- Generic background job architecture
- Persistent download queue
- Generic request pipeline
- Generic response pipeline
- Payload serialization
- Payload codec registry
- Generic notification architecture
- Archive notification system
- Event-driven communication
- Feature-specific job handlers

### In Progress

- Archive reliability improvements
- Queue recovery
- Progress reporting refinements
- Error recovery
- Notification polish

---

# Roadmap

## Phase 3 — User Experience

- Theme settings
- Reader preferences
- Archive preferences
- UI/UX improvements

---

## Phase 4 — Architecture Hardening

- Reliability improvements
- Performance optimization
- Background processing improvements

---

## Phase 4.5 — Stability

- ANR prevention
- Memory optimization
- Database optimization
- Large-scale validation

---

## Phase 5 — Source Platform

- Source abstraction layer
- Extension architecture
- Plugin-based source integration
- Multi-source feed support

---

## Phase 5.5 — Scaling

- Multi-source stress testing
- Resource management
- Operational stability
- Performance validation

---

# Project Goals

Udant aims to provide:

- Offline-first reading
- Local content ownership
- Reliable long-term article preservation
- Source independence
- Fast local access
- Modular architecture
- Extensible background processing
- Full user control over content

---

# License

This project is currently under active development and is not yet production ready.
