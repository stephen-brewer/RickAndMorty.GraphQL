pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("graphql-dgs", "7.5.3")
            version("graphql-java") {
                prefer("20.6")
                strictly("[20,21[")
            }
            version("junit", "5.10.0")
            version("ktor", "2.3.4")
            version("okhttp", "4.10.0")
            version("retrofit", "2.9.0")
            version("spring.boot") {
                require("3.1.3")
            }

            plugin("dgs-codegen", "com.netflix.dgs.codegen").version("6.0.2")
            plugin("kover", "org.jetbrains.kotlinx.kover").version("0.7.4")
            plugin("openapi-codegen", "org.openapi.generator").version("7.0.1")
            plugin("springframework-boot", "org.springframework.boot").versionRef("spring.boot")

            library("graphql-dgs-extended-scalars", "com.netflix.graphql.dgs", "graphql-dgs-extended-scalars").versionRef("graphql-dgs")
            library("graphql-dgs-spring-boot-starter", "com.netflix.graphql.dgs", "graphql-dgs-spring-boot-starter").versionRef("graphql-dgs")
            library("graphql-java","com.graphql-java", "graphql-java").versionRef("graphql-java")
            library("jackson-annotations","com.fasterxml.jackson.core", "jackson-annotations").version("2.15.2")
            library("moshi-kotlin","com.squareup.moshi", "moshi-kotlin").version("1.14.0")
            library("okhttp","com.squareup.okhttp3", "okhttp").versionRef("okhttp")
            library("okhttp-logging-interceptor","com.squareup.okhttp3", "logging-interceptor").versionRef("okhttp")
            library("retrofit2","com.squareup.retrofit2", "retrofit").versionRef("retrofit")
            library("retrofit2-converter-moshi","com.squareup.retrofit2", "converter-moshi").versionRef("retrofit")
            library("retrofit2-converter-scalars","com.squareup.retrofit2", "converter-scalars").versionRef("retrofit")
            library("springframework-spring-context","org.springframework", "spring-context").version("6.0.11")
            library("spring-boot", "org.springframework.boot", "spring-boot").versionRef("spring.boot")
            library("spring-boot-starter", "org.springframework.boot", "spring-boot-starter").versionRef("spring.boot")
            library("spring-boot-starter-actuator", "org.springframework.boot", "spring-boot-starter-actuator").versionRef("spring.boot")
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.7.3")

            // testing libraries
            library("junit-jupiter","org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("spring-boot-starter-test","org.springframework.boot", "spring-boot-starter-test").versionRef("spring.boot")
            library("spring-mockk", "com.ninja-squad", "springmockk").version("4.0.2")
        }
    }
}

rootProject.name = "DGS Rick and Morty"

include("provider")
include("modelTransformation")
include("graph")
