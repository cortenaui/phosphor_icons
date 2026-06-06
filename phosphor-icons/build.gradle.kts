import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.maven.publish)
}

val generatedKotlinDir = layout.projectDirectory.dir("src/commonMain/kotlin/generated")

val generatePhosphorIcons by
    tasks.registering(Exec::class) {
        group = "code generation"
        description = "Generates Phosphor font glyph sources from selection.json assets."

        val variants = listOf("regular", "bold", "light", "fill", "thin")
        variants.forEach { weight ->
            inputs.file(
                rootProject.layout.projectDirectory.file("scripts/$weight/selection.json"),
            )
        }
        inputs.file(rootProject.layout.projectDirectory.file("scripts/generate_phosphor_icons.py"))
        outputs.dir(generatedKotlinDir)

        workingDir = rootProject.projectDir
        commandLine(
            "python",
            "scripts/generate_phosphor_icons.py",
            "--output",
            generatedKotlinDir.asFile.absolutePath,
        )
    }

kotlin {
    android {
        namespace = "framework.cortena.icons"
        compileSdk = 37
        minSdk = 24
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources { enable = true }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "framework.cortena.icons.resources"
}

tasks
    .matching { it.name == "compileAndroidMain" || it.name.endsWith("SourcesJar") }
    .configureEach { dependsOn(generatePhosphorIcons) }

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    if (project.hasProperty("signingInMemoryKey")) {
        signAllPublications()
    }

    coordinates(
        groupId = group.toString(),
        artifactId = "phosphor_icons",
        version = version.toString(),
    )

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
            androidVariantsToPublish = listOf("release"),
        )
    )

    pom {
        name.set("CortenaUI Icons")
        description.set("Phosphor icons for Compose Multiplatform")
        url.set("https://github.com/cortenaui/phosphor_icons")
        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("cortenaui")
                name.set("The CortenaOS Project")
                url.set("https://github.com/cortenaui")
            }
        }
        scm {
            url.set("https://github.com/cortenaui/phosphor_icons")
            connection.set("scm:git:git://github.com/cortenaui/phosphor_icons.git")
            developerConnection.set("scm:git:ssh://git@github.com/cortenaui/phosphor_icons.git")
        }
    }
}
