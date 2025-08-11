plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization") version "2.1.0"
    id("com.google.devtools.ksp")
}

group = "com.sigmoid-98"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain { 17 }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    compilerOptions.javaParameters.set(true)
}

repositories {
    mavenCentral()
}

val jjwtVersion = "0.12.5"

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // kotlin serialization lib
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    // jjwt for JWT
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion") // For automatic JSON parsing

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.alibaba:fastjson:2.0.57")
    implementation("cn.hutool:hutool-all")
    /*implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")*/
    // mybatis-plus
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.12")
    implementation("com.baomidou:mybatis-plus-jsqlparser:3.5.12")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("com.aliyun:alibabacloud-dysmsapi20170525")
    implementation("com.github.penggle:kaptcha")
    implementation("com.aliyun:aliyun-java-sdk-core")
    implementation("com.aliyun.oss:aliyun-sdk-oss")
    implementation("com.aliyun:aliyun-java-sdk-vod")
    implementation(files("src/main/resources/jar/aliyun-java-vod-upload-1.4.15.jar"))

    implementation("com.alipay.sdk:alipay-easysdk")
    implementation("com.alipay.sdk:alipay-sdk-java")
    // implementation("com.github.pagehelper:pagehelper-spring-boot-starter")
    // implementation("com.alibaba.boot:nacos-config-spring-boot-starter")
    // runtimeOnly("org.springframework.boot:spring-boot-devtools")
    // annotationProcessor("org.projectlombok:lombok")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // kotlin-logging lib
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    // konvert data class mapper
    implementation("io.mcarle:konvert-api:4.1.0")
    implementation("io.mcarle:konvert-spring-annotations:4.1.0")
    // KSP to generate mapping code
    ksp("io.mcarle:konvert:4.1.0")
    ksp("io.mcarle:konvert-spring-injector:4.1.0")

    // spring-dotenv property source
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // // 阿里云vod上传
    implementation(files("${projectDir}/src/main/resources/aliyun-java-vod-upload-1.4.15.jar"))


    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
