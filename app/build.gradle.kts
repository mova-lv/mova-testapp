plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)

//    alias(libs.plugins.kotlin.android)
//    id("com.google.devtools.ksp")
//    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.example.testapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.testapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.bundles.nav3)
    implementation(libs.bundles.camerax)
    implementation(libs.compose.material.icons.extended)

    implementation("com.github.bumptech.glide:glide:4.12.0")
//    ksp("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.vectordrawable:vectordrawable:1.2.0")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.2.0")

    implementation("com.blankj:utilcodex:1.31.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.orhanobut:logger:2.2.0")

}