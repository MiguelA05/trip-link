import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)

}

// Read Mapbox token from local.properties, environment variable, or directly from file
val mapboxToken: String = try {
    // First try environment variable
    val envToken = System.getenv("MAPBOX_ACCESS_TOKEN")
    if (envToken != null && envToken.isNotEmpty()) {
        envToken
    } else {
        // Then try gradle property
        val gradleToken = project.findProperty("MAPBOX_ACCESS_TOKEN")?.toString()
        if (gradleToken != null && gradleToken.isNotEmpty()) {
            gradleToken
        } else {
            // Finally try reading from local.properties file directly
            val localPropsFile = File(rootProject.projectDir, "local.properties")
            if (localPropsFile.exists()) {
                val props = Properties()
                props.load(localPropsFile.inputStream())
                props.getProperty("MAPBOX_ACCESS_TOKEN", "")
            } else {
                ""
            }
        }
    }
} catch (e: Exception) {
    ""
}

android {
    namespace = "com.example.triplink"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.triplink"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // Expose Mapbox token to the app via BuildConfig. Set MAPBOX_ACCESS_TOKEN in local.properties or environment.
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${mapboxToken ?: ""}\"")
        resValue("string", "mapbox_access_token", mapboxToken ?: "")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.firebase.auth)
    implementation(libs.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.hiltAndroid)
    ksp(libs.hiltCompiler)
    implementation(libs.androidxHiltNavigationCompose)
    implementation(libs.data.store)
    // Mapbox SDK
    implementation(libs.mapsAndroid)
    implementation(libs.mapsCompose)

}
