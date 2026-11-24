plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    // Only needed for the stretch goal in step 3 (MutableSharedFlow)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}
