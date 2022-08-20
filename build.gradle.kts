plugins {
    id("org.springframework.boot") version("2.5.4")
    id("io.spring.dependency-management") version("1.0.11.RELEASE")
    id("java")
}

group = "com.repin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.telegram:telegrambots-spring-boot-starter:6.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}