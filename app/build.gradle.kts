plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    //Safe Args
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.zalune.fm_zeroa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zalune.fm_zeroa"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)


    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.androidx.room.compiler.v260)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.android.compiler)

    // Fragment
    implementation(libs.androidx.fragment.ktx)
    // Activity
    implementation(libs.androidx.activity.ktx)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.lifecycle.livedata.ktx)

    //Corrutinas
    implementation(libs.kotlinx.coroutines.android)

    implementation (libs.material)
// Navigation KTX (para findNavController())
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // Material Icons Extended (íconos adicionales)
    implementation ("androidx.compose.material:material-icons-extended:1.7.8")
    // Material Components (incluye íconos básicos)
    implementation ("com.google.android.material:material:1.12.0") // Usa la última versión

    implementation(libs.androidx.navigation.fragment.ktx.v290)
    implementation(libs.androidx.navigation.ui.ktx.v290)
    implementation ("com.google.android.material:material:1.12.0")


}