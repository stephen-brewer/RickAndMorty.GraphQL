import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.dgs.codegen)
    kotlin("jvm")
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
    api(project(":provider"))

    implementation(libs.graphql.java)
    implementation(libs.springframework.spring.context)
    implementation(libs.jackson.annotations)

    testImplementation(libs.junit.jupiter)
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

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    schemaPaths = mutableListOf("${rootDir}/modelTransformation/src/main/resources/schema.graphql")
    generateClient = false
    generateClientv2 = false
    packageName = "org.stephenbrewer.rick_and_morty.modelTransformation.generated"
    language = "kotlin"
    typeMapping = mutableMapOf(
        "DateTime" to "java.time.OffsetDateTime",
        "Uri" to "java.net.URI",
        "Cursor" to "org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor",
    )
}
