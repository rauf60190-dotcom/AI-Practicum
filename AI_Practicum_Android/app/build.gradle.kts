plugins {
    id("com.android.application")
}

android {
    namespace = "com.aipracticum.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aipracticum.app"
        minSdk = 29
        targetSdk = 35
        versionCode = 13
        versionName = "8.12.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.12.1")
}
