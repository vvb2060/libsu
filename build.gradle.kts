import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension

plugins {
    id("signing")
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
            apply(plugin = "signing")

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
                        pom {
                            name.set("libsu")
                            description.set("An Android library providing a complete solution for apps using root permissions")
                            url.set("https://github.com/topjohnwu/libsu")
                            licenses {
                                license {
                                    name.set("Apache License 2.0")
                                    url.set("https://github.com/topjohnwu/libsu/blob/master/LICENSE")
                                }
                            }
                            developers {
                                developer {
                                    name.set("John Wu")
                                    url.set("https://github.com/topjohnwu")
                                }
                            }
                            scm {
                                connection.set("scm:git:https://github.com/topjohnwu/libsu.git")
                                url.set("https://github.com/topjohnwu/libsu")
                            }
                        }
                    }
                }
                repositories {
                    maven {
                        name = "ossrh"
                        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        credentials(PasswordCredentials::class)
                    }
                }
            }

            signing {
                val signingKey = findProperty("signingKey") as String?
                val signingPassword = findProperty("signingPassword") as String?
                val secretKeyRingFile = findProperty("signing.secretKeyRingFile") as String?
                if (secretKeyRingFile != null && file(secretKeyRingFile).exists()) {
                    sign(publishing.publications)
                } else if (signingKey != null && signingPassword != null) {
                    useInMemoryPgpKeys(signingKey, signingPassword)
                    sign(publishing.publications)
                }
            }
        }
    }
}
