# Installation

The Phosphor Icons library is published to **Maven Central** under the `io.github.cortenaui` group ID.

## Repository

Maven Central is enabled by default in modern Gradle setups. If you have customized your repositories, declare it explicitly:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
```

## Adding the Dependency

The recommended pattern uses a Gradle version catalog:

```toml
# gradle/libs.versions.toml
[versions]
cortenaui-icons = "1.0.0"

[libraries]
cortena-icons-phosphor = { module = "io.github.cortenaui:phosphor_icons", version.ref = "cortenaui-icons" }
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.cortena.icons.phosphor)
}
```

If you don't use a version catalog, the inline form works just as well:

```kotlin
dependencies {
    implementation("io.github.cortenaui:phosphor_icons:1.0.0")
}
```

## Requirements

- **Compile SDK**: 37 (Android 17) or newer.
- **Min SDK**: 24.
- **Kotlin**: 2.3+.
- **Compose Multiplatform**: 1.10.3+.
