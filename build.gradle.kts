import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.12"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sigmoid-98"
version = "0.0.1-SNAPSHOT"
description = "springboot nls"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

// Configuration for all subprojects
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    // apply(plugin = "org.jetbrains.kotlin.jvm")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    dependencyManagement {
        dependencies {
            dependency("com.alibaba:fastjson:2.0.57") // dependency("com.alibaba:fastjson:1.2.83")
            dependency("cn.hutool:hutool-all:5.8.10")
            dependency("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
            dependency("mysql:mysql-connector-java:8.0.28") // Note: newer versions use com.mysql:mysql-connector-j
            dependency("com.aliyun:alibabacloud-dysmsapi20170525:2.0.24")

            dependency("com.github.penggle:kaptcha:2.3.2") {
                // CORRECTED EXCLUSION:
                // exclude("javax.servlet:javax.servlet-api")
                exclude(mapOf("group" to "javax.servlet", "name" to "javax.servlet-api"))
            }

            dependency("com.aliyun:aliyun-java-sdk-core:4.5.1")
            dependency("com.aliyun.oss:aliyun-sdk-oss:3.10.2")
            dependency("com.aliyun:aliyun-java-sdk-vod:2.16.11")
            dependency("com.alipay.sdk:alipay-easysdk:2.2.3")
            dependency("com.alipay.sdk:alipay-sdk-java:4.38.149.ALL")
            dependency("com.github.pagehelper:pagehelper-spring-boot-starter:1.4.6")
            dependency("com.alibaba.boot:nacos-config-spring-boot-starter:0.3.0-RC") // Be cautious with RC versions
        }
    }

    dependencies {
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// dependencies {
//     implementation("org.springframework.boot:spring-boot-starter")
//     implementation("org.jetbrains.kotlin:kotlin-reflect")
//     testImplementation("org.springframework.boot:spring-boot-starter-test")
//     testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//     testRuntimeOnly("org.junit.platform:junit-platform-launcher")
// }

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
