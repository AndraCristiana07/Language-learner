plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.languagelearner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.languagelearner"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.gridlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation("com.squareup.retrofit2:retrofit:2.3.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.3.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.1")
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
    implementation("io.reactivex.rxjava2:rxjava:2.1.13")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.google.android.gms:play-services-auth:17.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    implementation("androidx.core:core-splashscreen:1.0.1")


}