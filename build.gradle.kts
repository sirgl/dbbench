import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("it.unimi.dsi:fastutil:8.5.4")
    implementation("org.lmdbjava:lmdbjava:0.8.1")
    implementation("com.github.jhg023:Pbbl:1.0.2")
    implementation("org.rocksdb:rocksdbjni:6.20.3")
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