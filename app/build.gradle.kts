plugins {
    alias(libs.plugins.android.application)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ch.y.bitite.safespot"
    compileSdk = 35

    defaultConfig {
        applicationId = "ch.y.bitite.safespot"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://safespotapi20250207214631.azurewebsites.net/\"")
        buildConfigField("String", "BASE_URL_IMAGES", "\"https://safespotapi20250207214631.azurewebsites.net/uploads/\"")
        android.buildFeatures.buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    // Add the packagingOptions block here
    packaging {
        resources {
            exclude("META-INF/gradle/incremental.annotation.processors")
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.glide)
    implementation(libs.logging.interceptor)
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler) // Utiliser annotationProcessor au lieu de kapt
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.annotation)
    // To use the Java-compatible @androidx.annotation.OptIn API annotation
    implementation(libs.annotation.experimental)

    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.1") // Use the latest version
    annotationProcessor("androidx.room:room-compiler:2.6.1") // Utiliser annotationProcessor au lieu de kapt

}