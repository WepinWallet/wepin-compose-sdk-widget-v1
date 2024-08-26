import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization").version("1.9.23")
}

version = "0.0.1"

val buildConfigGenerator by tasks.registering(Sync::class) {
    from(
        resources.text.fromString(
            """
        |package com.wepin.cm.widgetlib
        |
        |object BuildConfig {
        |  const val PROJECT_VERSION = "${project.version}"
        |}
        |
      """.trimMargin()
        )
    ) {
        rename { "BuildConfig.kt" } // set the file name
        into("com/wepin/cm/widgetlib") // change the directory to match the package
    }

    into(layout.buildDirectory.dir("generated-src/kotlin"))
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "lib"
            isStatic = true
        }
    }

    cocoapods {
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        ios.deploymentTarget = "13.0"
        version = "0.0.1"

        extraSpecAttributes["pod_target_xcconfig"] = """{
            'KOTLIN_PROJECT_PATH' => ':lib',
            'PRODUCT_MODULE_NAME' => 'lib',
            'FRAMEWORK_SEARCH_PATHS' => '${'$'}(inherited) ${'$'}{PODS_ROOT}/AppAuth'
        }""".trimMargin()

        pod("AppAuth") {
            version = "~> 1.7.5"
        }

        pod("secp256k1") {
            version = "~> 0.1.0"
        }

        pod("JFBCrypt") {
            version = "~> 0.1"
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(buildConfigGenerator.map { it.destinationDir })
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                // ktor
                implementation("io.ktor:ktor-client-core:2.3.11")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("io.ktor:ktor-client-logging:2.3.11")

                // encoding
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                //webview
                api("io.github.kevinnzou:compose-webview-multiplatform:1.9.20")
//                api("io.github.kevinnzou.local:compose-webview-multiplatform:1.9.20")

                //coroutine
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1-native-mt")

                //wepin-login-library
                api("io.wepin:wepin-compose-sdk-login-v1:0.0.9")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.11")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
                implementation("io.ktor:ktor-client-logging:2.3.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")

                // storage
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
                implementation("com.russhwolf:multiplatform-settings-serialization:1.1.1")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.wepin.cm.widgetlib"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-android:1.6.8")
    implementation("androidx.lifecycle:lifecycle-common-jvm:2.8.4")
    implementation("androidx.browser:browser:1.8.0")
}

mavenPublishing {
//    publishToMavenCentral(SonatypeHost.DEFAULT)
    // or when publishing to https://s01.oss.sonatype.org
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates("io.wepin.local", "wepin-compose-sdk-login-v1", "${project.version}")

    pom {
        name.set(project.name)
        description.set("A description of what my library does.")
        inceptionYear.set("2023")
        url.set("https://github.com/username/mylibrary/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("username")
                name.set("User Name")
                url.set("https://github.com/username/")
            }
        }
        scm {
            url.set("https://github.com/username/mylibrary/")
            connection.set("scm:git:git://github.com/username/mylibrary.git")
            developerConnection.set("scm:git:ssh://git@github.com/username/mylibrary.git")
        }
    }
}
