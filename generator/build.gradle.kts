

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    id("com.qqviaja.gradle.MybatisGenerator") version "2.5"
}

group = "com.sigmoid-98"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}


configurations {
    mybatisGenerator
}

mybatisGenerator {
    // missing default true
    overwrite  = true
    // missing default false
    verbose = true
    // missing default src/main/resources/generator/generatorConfig.xml
    configFile = "src/main/resources/generator-config-business.xml"
    dependencies {
        // Dependencies for the MyBatis Generator tool itself
        mybatisGenerator("mysql:mysql-connector-java:8.0.22")
        mybatisGenerator("org.mybatis.generator:mybatis-generator-core:1.4.1") // <-- 主要改动在这里
    }
}


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("mysql:mysql-connector-java:8.0.22")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
