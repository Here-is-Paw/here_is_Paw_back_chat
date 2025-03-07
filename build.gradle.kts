plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ll"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    //actuator 설정
    implementation ("org.springframework.boot:spring-boot-starter-actuator")

    //postgreSQL & PostGIS
//    runtimeOnly("org.postgresql:postgresql")
//    implementation("org.hibernate.orm:hibernate-spatial:6.6.5.Final")
//    implementation("org.locationtech.jts:jts-core:1.19.0")
//    implementation("org.postgresql:postgresql:42.7.5")
//    implementation("org.hibernate:hibernate-spatial:6.2.13.Final")
//    implementation ("org.hibernate.orm:hibernate-spatial")
//    implementation("org.hibernate.orm:hibernate-spatial")


    // PostgreSQL 드라이버 (한 번만 선언하고 버전 명시)
    implementation("org.postgresql:postgresql:42.7.5")

// Hibernate Spatial 지원
    implementation("org.hibernate.orm:hibernate-spatial:6.6.5.Final")

// JTS 지리 데이터 지원
    implementation("org.locationtech.jts:jts-core:1.19.0")

// PostgreSQL JDBC 드라이버의 PostGIS 확장 추가
    implementation("net.postgis:postgis-jdbc:2.5.1")
    implementation("net.postgis:postgis-geometry:2.5.1")


    //websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    //validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // SpringBoot Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // OAuth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // JSON Simple
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    //S3 bucket
    implementation(platform("software.amazon.awssdk:bom:2.24.0"))
    implementation("software.amazon.awssdk:s3")

    //swegger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    //kafka
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // meiliesearch
    implementation("com.meilisearch.sdk:meilisearch-java:0.14.2")

    //docker-compose
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
