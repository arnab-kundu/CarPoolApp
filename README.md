# Together - Carpool App

A carpool application foundation with a native Android client and a NestJS backend.

## Repository

- [Android](android/README.md): Kotlin and Jetpack Compose app, setup, builds, and tests.
- [Backend](backend/README.md): API foundation, local infrastructure, migrations, and tests.
- [Documentation](docs/ARCHITECTURE.md): architecture and project design.
- [API](docs/API.md) and [database](docs/DATABASE.md): service and storage specifications.
- [Project scope](docs/TASK.md) and [validation](docs/VALIDATION.md): delivery boundary and recorded checks.

## Current scope

The Android app provides an offline demo with ride search, ride details, driver drafts, and profile/inbox foundations. The backend provides health checks, request validation, and domain scaffolding.

Live authentication, booking, payments, location tracking, and verification remain pending. This repository delivers the phase 0/1 foundation for review before those features are implemented.

Follow each component's README for setup and development. Shared Docker Compose configuration and its environment template live at the repository root.
