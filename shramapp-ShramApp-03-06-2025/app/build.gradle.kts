import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
     alias(libs.plugins.google.services)
}

android {
    namespace = "com.uvk.shramapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.uvk.shramapplication"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }


    /*packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
        }
    }*/

    packaging {
        resources {
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.activity)
    implementation(libs.intuit.ssp)
    implementation(libs.intuit.sdp)
    implementation(libs.cardview)
    implementation(libs.circularimage)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.retrofit.okhttp)



    // Lottie for animated vector graphics
    implementation(libs.lottianim)
    implementation(libs.text.anim)
    implementation(libs.playservicesmap)
    implementation(libs.playserviceslocation)
    implementation(libs.maputils)
    implementation(libs.dhavalimagepicker)



    implementation(libs.ucrop)
    implementation(libs.zoomimage)
    implementation(libs.emoji)
    implementation(libs.emojiview)
    implementation(libs.shimmer)
    implementation(libs.razor)
    implementation(libs.translate) {
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
        exclude(group = "com.google.api.grpc", module = "proto-google-iam-v1")
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }

    // implementation(libs.flexbox)


    implementation(libs.dotsindicator)
    implementation(libs.glide)
    implementation(libs.otp)
    implementation(libs.autoslider)
    implementation(libs.googlelogin)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.cast.framework)
    implementation(libs.play.services.mlkit.text.recognition.devanagari)
    implementation(libs.mlkit)
    implementation(libs.face.detection)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}