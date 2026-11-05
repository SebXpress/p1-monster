// Cliente móvil Android de la Comercializadora de Teléfonos Celulares (Grupo 1 - NRC 36841)
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ec.edu.monster.climov"
    compileSdk = 35

    defaultConfig {
        applicationId = "ec.edu.monster.climov.comercializadora"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core)
}
