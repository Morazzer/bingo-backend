plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.morazzer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://pkg.morazzer.dev/releases") }
}

dependencies {
    implementation("org.javacord:javacord:3.8.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("dev.morazzer:discordbot-configurations:1.0.0")

    implementation("org.jetbrains:annotations:22.0.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}