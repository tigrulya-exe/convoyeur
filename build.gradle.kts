import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    `maven-publish`
}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "JitPack"
            url = uri("https://jitpack.io")
        }
    }

    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])
        }
    }
}

group = "ru.nsu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.DelicateCoroutinesApi")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    val log4jVersion = "2.17.0"
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}