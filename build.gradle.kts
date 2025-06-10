import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    id("org.springframework.boot") version "3.3.12"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

allprojects {

    group = "com.sigmoid-98"
    version = "0.0.1-SNAPSHOT"
    description = "springboot nls service with kotlin"

    repositories {
        mavenCentral()
    }

}

// Configuration for all subprojects
subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.google.devtools.ksp")
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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


    // 1. 指定 Kotlin 编译生成的字节码目标为 Java 17
    // 2. 开启 Kotlin 对 JSR-305 注解
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        }
    }

    // 为 Test 的 Gradle 任务配置为使用 JUnit Platform 运行测试
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


tasks.register("kotlinCompilerVersion") {
    doLast {
        println("Kotlin Compiler Version: ${KotlinVersion.CURRENT}")
    }
}