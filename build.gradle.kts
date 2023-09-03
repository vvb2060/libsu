import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension

plugins {
    id("maven-publish")
    id("com.android.library") version "8.1.1" apply false
}

fun Project.android(configuration: BaseExtension.() -> Unit) =
        extensions.getByName<BaseExtension>("android").configuration()

fun Project.androidLibrary(configuration: LibraryExtension.() -> Unit) =
        extensions.getByName<LibraryExtension>("android").configuration()

subprojects {
    afterEvaluate {
        android {
            compileSdkVersion(34)
            buildToolsVersion = "34.0.0"

            defaultConfig {
                if (minSdkVersion == null)
                    minSdk = 19
                targetSdk = 34
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }

        if (plugins.hasPlugin("com.android.library")) {
            apply(plugin = "maven-publish")

            androidLibrary {
                buildFeatures {
                    buildConfig = false
                }

                publishing {
                    singleVariant("release") {
                        withSourcesJar()
                        withJavadocJar()
                    }
                }
            }

            publishing {
                publications {
                    register<MavenPublication>("libsu") {
                        afterEvaluate {
                            from(components["release"])
                        }
                        groupId = "com.github.topjohnwu.libsu"
                        version = "5.2.0"
                        artifactId = project.name
                    }
                }
            }
        }
    }
}
