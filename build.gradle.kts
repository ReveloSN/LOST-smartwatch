plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt.yml"))
        source.setFrom(files("src/main/java", "src/main/kotlin", "src/test/java", "src/test/kotlin"))
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach { jvmTarget = "17" }

    // detekt 1.23 incluye su propio compilador de Kotlin; se fija su versión para que no choque con la del proyecto.
    configurations.matching { it.name == "detekt" }.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") useVersion("2.0.21")
        }
    }
}

// ktlint se ejecuta con su CLI porque el Kotlin integrado de AGP 9 no expone los source sets al plugin de Gradle.
val ktlint: Configuration by configurations.creating

dependencies {
    ktlint(libs.ktlint.cli) {
        attributes { attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL)) }
    }
}

val ktlintSources = listOf("**/src/**/*.kt", "**/*.gradle.kts", "!**/build/**")

tasks.register<JavaExec>("ktlintCheck") {
    group = "verification"
    description = "Revisa el estilo del código Kotlin con ktlint."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = ktlintSources
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Corrige el estilo del código Kotlin con ktlint."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args = listOf("-F") + ktlintSources
}
