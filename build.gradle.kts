import org.gradle.kotlin.dsl.annotationProcessor

plugins {
    id("java")
    kotlin("jvm")
}

group = "me.tortel"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.4.1"
val jomlVersion = "1.10.8"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-nuklear")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}