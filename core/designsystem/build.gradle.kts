plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.lost.designsystem"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.ui)
    api(libs.ui.graphics)
    api(libs.compose.material3)
    api(libs.compose.foundation)
    api(libs.ui.tooling.preview)
    api(libs.compose.ui.tooling)
    api(libs.wear.tooling.preview)
    debugImplementation(libs.ui.tooling)
}
