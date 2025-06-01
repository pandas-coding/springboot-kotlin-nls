plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    // id("org.springframework.boot") version "3.3.12"
    // id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sigmoid-98"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.alibaba:fastjson:2.0.57")
    // implementation("cn.hutool:hutool-all")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")
    implementation("mysql:mysql-connector-java:8.0.28")
    // implementation("com.aliyun:alibabacloud-dysmsapi20170525")
    // implementation("com.github.penggle:kaptcha")
    // implementation("com.aliyun:aliyun-java-sdk-core")
    // implementation("com.aliyun.oss:aliyun-sdk-oss")
    // implementation("com.aliyun:aliyun-java-sdk-vod")
    // implementation("com.alipay.sdk:alipay-easysdk")
    // implementation("com.alipay.sdk:alipay-sdk-java")
    // implementation("com.github.pagehelper:pagehelper-spring-boot-starter")
    // implementation("com.alibaba.boot:nacos-config-spring-boot-starter")
    // runtimeOnly("org.springframework.boot:spring-boot-devtools")
    // annotationProcessor("org.projectlombok:lombok")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // kotlin-logging lib
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    // // 阿里云vod上传
    // implementation(files("${projectDir}/src/main/resources/aliyun-java-vod-upload-1.4.15.jar"))


    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// kotlin {
//     compilerOptions {
//         freeCompilerArgs.addAll("-Xjsr305=strict")
//     }
// }

tasks.withType<Test> {
    useJUnitPlatform()
}
