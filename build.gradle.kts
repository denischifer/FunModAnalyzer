plugins {
    id("java")
}

group = "dev.denischifer"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.formdev:flatlaf:3.5.1")
}