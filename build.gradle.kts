import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "com.kingOf0"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")

}

dependencies {
    //spigot 8
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    //spigot vault
    compileOnly("com.github.milkbowl:vault:+") {
        exclude(group = "org.bstats")
    }

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    compileOnly("com.github.cryptomorin:XSeries:9.3.0")
}


tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("KKits.jar")


    relocate("kotlin", "com.kingOf0.kotlin")
    relocate("kotlinx", "com.kingOf0.kotlinx")
    relocate("com.cryptomorin", "com.kingOf0.xseries")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}