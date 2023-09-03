plugins {
    id("com.android.library")
}

android {
    namespace = "com.topjohnwu.superuser"
    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
    }
}

dependencies {
    compileOnly("androidx.annotation:annotation:1.6.0")
 }
