import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.springframework.boot)
    kotlin("jvm")
    kotlin("plugin.spring") version "1.9.10"
    id("test-report-aggregation")
    alias(libs.plugins.kover)
}

group = "org.stephenbrewer"
version = "1.3.37"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modelTransformation"))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.graphql.dgs.extended.scalars)
    implementation(libs.graphql.dgs.spring.boot.starter)
    implementation(libs.graphql.java)

    testImplementation(libs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
        exclude(module = "mockito-junit-jupiter")
    }
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.mockk)

    kover(project(":modelTransformation"))
    kover(project(":provider"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

koverReport {
    filters {
        excludes {
            classes("*.generated.*")
        }
    }
}

tasks.check {
    dependsOn(tasks.testAggregateTestReport)
    dependsOn(tasks.koverHtmlReport)
}
