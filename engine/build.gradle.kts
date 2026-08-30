plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    jvm()
    androidTarget()
    // iOS targets are here so the engine is proven to compile and test on
    // Kotlin/Native, not just the JVM. Android is deliberately absent: adding it
    // would make `:engine:jvmTest` require the Android SDK, and the engine has
    // no Android-specific code to test. Add androidTarget() when app/ lands.
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// The transcript regression suite reads vendor/open-adventure/tests at runtime,
// so it is a JVM-only test -- Kotlin/Native has no portable file access here.
tasks.withType<Test>().configureEach {
    systemProperty("retroadventure.testdir", rootProject.file("vendor/open-adventure/tests").absolutePath)
    // Forward -Dretroadventure.dump=<name> through to the test JVM so a single
    // transcript's actual output can be written out and diffed.
    System.getProperty("retroadventure.dump")?.let { systemProperty("retroadventure.dump", it) }
    testLogging { showStandardStreams = true }
}

android {
    namespace = "com.xndev.retroadventure.engine"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
