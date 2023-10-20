package org.stephenbrewer.rick_and_morty.graph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("org.stephenbrewer")
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = ["org.stephenbrewer"])
class GraphApplication

fun main(args: Array<String>) {
    runApplication<GraphApplication>(*args)
}
