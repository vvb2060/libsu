plugins {
    id("com.android.library")
}

android {
    namespace = "com.topjohnwu.superuser.nio"
    defaultConfig {
        minSdk = 21
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {
    compileOnly("androidx.annotation:annotation:1.6.0")
}
