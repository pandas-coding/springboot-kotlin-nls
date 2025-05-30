plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.12"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-aop")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // implementation("com.alibaba:fastjson")
    // implementation("cn.hutool:hutool-all")
    // implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter")
    // implementation("mysql:mysql-connector-java")
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
    // testImplementation("org.springframework.boot:spring-boot-starter-test")
    //
    // // 阿里云vod上传
    // implementation(files("${projectDir}/src/main/resources/aliyun-java-vod-upload-1.4.15.jar"))


    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
