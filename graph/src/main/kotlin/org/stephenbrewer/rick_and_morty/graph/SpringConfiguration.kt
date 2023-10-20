package org.stephenbrewer.rick_and_morty.graph

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi

@ConfigurationProperties(prefix = "provider")
data class ProviderProperties(
    val hostname: String,
)

@Configuration
class SpringConfiguration(
    private val providerProperties: ProviderProperties,
) {

    @Bean
    fun characterApi(): CharacterApi {
        return CharacterApi(providerProperties.hostname)
    }

    @Bean
    fun episodeApi(): EpisodeApi {
        return EpisodeApi(providerProperties.hostname)
    }

    @Bean
    fun locationApi(): LocationApi {
        return LocationApi(providerProperties.hostname)
    }
}
