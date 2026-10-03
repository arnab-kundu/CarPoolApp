# Android app

Native Kotlin and Jetpack Compose client for Together.

## Current behavior

Passenger/driver mode switch; offline sample ride list; pickup, destination, date and seat filtering; ride details; date/time selection; validated ride drafts; drafts in My rides; profile and inbox foundation screens.

Drafts are stored in process memory. Rides, ratings, badges, and prices are sample fixtures. Live location, authentication, booking, payment, and verification are not implemented.

## Setup

Open this `android` directory in Android Studio. Set the Gradle JDK to Java 17, install Android SDK 35, and configure your SDK path in `local.properties` or through Android Studio. The SDK path is machine-specific and ignored by Git.

Sync Gradle and run the `app` configuration on an emulator or device with Android 8.0/API 26 or later.

## Build and test

Run from this directory in PowerShell:

```powershell
# Set JAVA_HOME to your Java 17 installation if needed.
.\gradlew.bat assembleDebug testDebugUnitTest
```

The debug APK is `app/build/outputs/apk/debug/app-debug.apk`, relative to this directory.

For lint and instrumentation test compilation:

```powershell
.\gradlew.bat lintDebug assembleDebugAndroidTest
```

With an emulator running or a device connected:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

On macOS/Linux, use `bash gradlew` in place of `.\gradlew.bat`.

## Layout

- `app/src/main`: application code and resources.
- `app/src/test`: unit tests.
- `app/src/androidTest`: device and Compose UI tests.
- `gradle/wrapper`: Gradle wrapper distribution configuration.

See [project scope](../docs/TASK.md), [architecture](../docs/ARCHITECTURE.md), and [recorded validation](../docs/VALIDATION.md). Maps/provider configuration and release signing remain pending.
