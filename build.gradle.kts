import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.15.0-Beta.2"
}

group = "me.arpanpathak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    // Kotlin coroutines Core library for Kotlin/JVM
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    // Optional: Kotlin coroutines Android library for Android projects
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.jacobpeterson:alpaca-java:6.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}