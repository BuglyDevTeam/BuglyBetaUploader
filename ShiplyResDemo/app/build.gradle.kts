plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.shiplyresdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shiplyresdemo"
        minSdk = 29
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.tencent.shiply:reshub-sdk:1.8.19-RC02")//ResHub核心SDK
    implementation ("com.tencent.shiply:reshub-net:1.8.19-RC02")//可选，默认的简单网络下载实现
    implementation ("com.tencent.shiply:reshub-report:1.8.19-RC02")//可选，默认的上报SDK
    implementation ("com.tencent.shiply:reshub-patch-sdk:1.8.19-RC02")        //可选，差量更新SDK
}