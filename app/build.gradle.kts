plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.lost.watch"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.lost.watch"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            // Pseudolocales en-XA (texto ~30 % más largo) y ar-XB (derecha a izquierda) para probar cortes.
            isPseudoLocalesEnabled = true
        }
        release {
            // R8: reduce, optimiza y ofusca el código y quita los recursos que no se usan
            // (los iconos de material-icons-extended pesan casi todo el APK sin esto).
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    useLibrary("wear-sdk")
    androidResources {
        // Los GPX de sample-routes llegan como assets; el script y el README no hacen falta en el APK.
        ignoreAssetsPattern = "!*.py:!*.md:!.*"
        // Idiomas por app (Android 13+): español base, inglés y portugués.
        generateLocaleConfig = true
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":domain"))
    implementation(project(":ai"))
    implementation(project(":data"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.ui.tooling)
    implementation(libs.core.splashscreen)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.guava)
    implementation(libs.play.services.wearable)
    implementation(libs.protolayout)
    implementation(libs.protolayout.material3)
    implementation(libs.tiles)
    implementation(libs.tiles.tooling.preview)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.watchface.complications.data.source.ktx)
    implementation(libs.wear.tooling.preview)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.tiles.renderer)
    debugImplementation(libs.tiles.tooling)
    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
}
