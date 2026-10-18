plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    namespace = "com.lost.data"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 30
    }
    buildTypes {
        // En debug LOST corre en modo demo: GPS simulado con los GPX y aviso al contacto simulado.
        debug { buildConfigField("boolean", "DEMO_MODE", "true") }
        release { buildConfigField("boolean", "DEMO_MODE", "false") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    sourceSets {
        getByName("main").assets.srcDir(rootProject.file("sample-routes"))
    }
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
        unitTests.isReturnDefaultValues = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)
    implementation(libs.play.services.location)
    implementation(libs.lifecycle.service)
    implementation(libs.core.ktx)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}
