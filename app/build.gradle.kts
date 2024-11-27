plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pandouland"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pandouland"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
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
    implementation(libs.coordinatorlayout)

    // Naviguation entre les 5 fragments
    implementation("com.google.android.material:material:1.9.0") // Vérifiez la dernière version
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Jspu c'est quoi
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Delphine <3
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // Pour convertir la réponse JSON en objet Java
    implementation ("com.google.android.gms:play-services-fitness:21.0.0") //Pour les biblios google fit (pas)

    //Pour les Gifs
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
//pour capteur de pas
    implementation ("androidx.activity:activity-ktx:1.7.2") //
    implementation ("androidx.fragment:fragment-ktx:1.6.1") //
    implementation ("androidx.core:core-ktx:1.12.0")
}

