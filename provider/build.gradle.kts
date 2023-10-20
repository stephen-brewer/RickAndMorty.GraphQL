plugins {
    kotlin("jvm")
    alias(libs.plugins.openapi.codegen)
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
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.moshi.kotlin)
    api(libs.okhttp)

    testImplementation(libs.junit.jupiter)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("generateProviders", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("kotlin")
    ignoreFileOverride.set("$projectDir/.openapi-generator-ignore")
    inputSpec.set("$projectDir/specs/RickAndMorty.yaml")
    outputDir.set("$buildDir/generated")
    cleanupOutput.set(true)
    packageName.set("org.stephenbrewer.rick_and_morty.provider.generated")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "enumPropertyNaming" to "UPPERCASE",
            "library" to "jvm-okhttp4",
            "nullableReturnType" to "true",
            "useCoroutines" to "true",
        )
    )
}

sourceSets["main"].kotlin.srcDirs(file("$buildDir/generated/src/main/kotlin"))

tasks.compileKotlin {
    dependsOn(tasks.named("generateProviders"))
}
