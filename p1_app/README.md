# P1 App: Pizza Party

## Overview
`P1_App` is an Android application built using Kotlin and configured with Gradle's version catalog. It supports SDK versions from 24 to 35. It is based on the "Pizza Party" app example from Chapter 2 (see README.md in root directory for textbook information)

## Prerequisites
Before running the project, ensure you have the following installed:
- **Android Studio Ladybug Feature Drop | 2024.2.2**
- **JDK 11**
- **Android SDK with API level 35 installed**

## Dependencies
This project includes the following libraries:
- Kotlin (`1.9.24`)
- AndroidX Core KTX (`1.15.0`)
- AppCompat (`1.6.1`)
- Material Components (`1.10.0`)
- Activity KTX (`1.10.0`)
- ConstraintLayout (`2.1.4`)
- JUnit (`4.13.2` for unit tests, `1.2.1` for AndroidX tests)
- Espresso (`3.6.1` for UI testing)

## Build Configuration
- **Compile SDK**: 35
- **Minimum SDK**: 24
- **Target SDK**: 35
- **Java Version**: 11

## Setup Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/your-repo.git
   cd your-repo/P1_App
   ```
2. Open Android Studio and select `Open an Existing Project`, then choose `P1_App`.
3. Ensure dependencies are synced by clicking `Sync Project with Gradle Files`.
4. Run the app using an emulator or a physical device with API level 24+.
5. To run tests:
   ```sh
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

## Project Structure
```
P1_App/
├── app/ (Main application module)
├── gradle/libs.versions.toml (Dependency versions)
├── build.gradle.kts (Project build configuration)
├── proguard-rules.pro (ProGuard rules for release build)
```

## License
This project is licensed under the MIT License - see the `LICENSE` file for details.

---
For issues and feature requests, please open an issue on GitHub.


