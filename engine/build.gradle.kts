plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    // Android and iOS targets get added when app/ arrives; keeping the engine
    // JVM-only for now means `./gradlew :engine:jvmTest` runs without the
    // Android SDK installed.

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// The transcript regression suite reads vendor/open-adventure/tests at runtime.
tasks.withType<Test>().configureEach {
    systemProperty("retroadventure.testdir", rootProject.file("vendor/open-adventure/tests").absolutePath)
    testLogging { showStandardStreams = true }
}
