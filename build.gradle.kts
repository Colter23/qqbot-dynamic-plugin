import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("kapt") version "1.4.0"
//    id("com.github.johnrengelman.shadow") version "5.2.0"
//    id("net.mamoe.mirai-console") version "1.0-RC-1"
}

group = "top.colter"
version = "1.0"

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {

    val core = "1.2.3"
    val console = "1.0-M4"
    compileOnly("net.mamoe:mirai-console:$console")
    compileOnly("net.mamoe:mirai-core:$core")
    compileOnly("net.mamoe:mirai-console-pure:$console")
    testImplementation("net.mamoe:mirai-console:$console")
    testImplementation("net.mamoe:mirai-core:$core")
    testImplementation("net.mamoe:mirai-console-pure:$console")

    // 解析JSON
    implementation("com.alibaba:fastjson:1.2.74")

    val autoService = "1.0-rc7"
    kapt("com.google.auto.service", "auto-service", autoService)
    compileOnly("com.google.auto.service", "auto-service-annotations", autoService)

    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}