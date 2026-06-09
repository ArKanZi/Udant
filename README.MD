Udant
Udant is an offline-first Android news and article reader designed to aggregate content from multiple sources into a single, distraction-free feed.
The project focuses on local-first architecture, smooth reading experience, and long-term extensibility. Articles are cached locally, allowing users to continue reading even when network connectivity is unavailable.
Features
•	Offline-first article reading
•	Multi-source feed aggregation
•	Infinite feed loading
•	Local article caching with Room
•	Saved articles for later reading
•	Feed session management
•	Automatic article enrichment
•	Modern Jetpack Compose UI
•	MVVM architecture
•	Dependency Injection with Hilt
•	Kotlin Coroutines and Flow
Tech Stack
•	Kotlin
•	Jetpack Compose
•	Room Database
•	Hilt
•	Kotlin Coroutines
•	StateFlow
•	Coil
•	RSS Feed Integration
Architecture
Udant follows an MVVM-based architecture:
UI (Compose)
    ↓
ViewModel
    ↓
Repository
    ↓
DAO
    ↓
Room Database
Responsibilities
ViewModel
•	Manages UI state
•	Handles feed session state
•	Coordinates user actions
Repository
•	Fetches articles from remote sources
•	Coordinates data transformations
•	Handles caching and enrichment
DAO
•	Provides local database access
Current Status
Phase 1 - MVP Stabilization ✅
Completed:
•	Feed loading
•	Feed refresh
•	Infinite loading
•	Saved articles
•	Offline caching
•	Rotation handling
•	Feed session management
•	Error handling improvements
•	Lifecycle stability fixes
Planned Features
•	Search
•	Categories
•	Multiple feed source types
o	RSS
o	APIs
o	Web scraping
•	Improved enrichment pipeline
•	Reader customization
•	Extensions system
•	Advanced feed filtering
Project Goals
Udant aims to provide:
•	A local-first reading experience
•	Source independence
•	Fast article access
•	Long-term extensibility
•	Full user control over content sources
License
This project is currently under active development.
