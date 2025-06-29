

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
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



repositories {
    mavenCentral()
    google()
}

dependencies {
    // business包
    implementation(project(":business"))
    // kotlin反射API
    implementation("org.jetbrains.kotlin:kotlin-reflect")


    // mybatis-plus
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12")
    implementation("com.baomidou:mybatis-plus-generator:3.5.12")
    implementation("org.freemarker:freemarker:2.3.32")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
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
