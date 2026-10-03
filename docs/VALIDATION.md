# Foundation validation - 3 October 2026 (Asia/Kolkata)

- Final debug APK: assembled successfully with Gradle 8.13, AGP 8.9.2, Kotlin/Compose compiler 2.1.20, Java 17 and Android SDK 35.
- APK: android/app/build/outputs/apk/debug/app-debug.apk (application ID com.carpool.app, version 0.1.0, minimum API 26).
- SHA-256: eded02ee17fb082e888cb31706f95353e77153cad5396e384f0f81d0b20a19bc
- Android unit tests: 3 passed, 0 failed (date/route/seats filtering, draft exclusion, invalid-seat rejection).
- Compose instrumentation tests: compiled into test APK. Not executed because the device-test command was declined. No runtime/screenshot/device compatibility claim is made.
- Android lint: passed, 0 errors and 8 non-blocking warnings. These cover target API/library age and backup/Compose usage advisories; no baseline suppresses errors. Target/release-readiness review remains necessary before Play publication.
- NestJS TypeScript build: passed.
- Backend domain tests: 3 passed (booking state transitions and integer pricing).
- Backend HTTP test: passed (working health, invalid/extra phone input, missing locations, invalid coordinates/UUID, unavailable auth and rides).
- PostGIS integration test: 1 explicitly skipped, because TEST_DATABASE_URL is unset. No local Docker/psql installation was found; migration and Docker startup have not been executed locally. CI includes a PostGIS service and migration/integration checks, but CI itself has not run here.
- OpenAPI JSON references and Android XML: parsed and checked.
- Dependency installation: npm reported 0 vulnerabilities. No production audit is implied.

Build compatibility references checked: [AGP 8.9 release notes](https://developer.android.com/build/releases/agp-8-9-0-release-notes), [Compose compiler](https://developer.android.com/jetpack/androidx/releases/compose-compiler), [NestJS first steps](https://docs.nestjs.com/first-steps). Versions are pinned/locked for this foundation, not asserted to be newest.

Provider-backed identity, live routes, booking, payments, foreground tracking, chat, push, admin and production operations remain pending. See TASK.md. This is an installable offline foundation, not a production-ready marketplace.
