package org.stephenbrewer.rick_and_morty.graph

import io.mockk.coEvery
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.Success
import org.stephenbrewer.rick_and_morty.provider.generated.models.Character
import org.stephenbrewer.rick_and_morty.provider.generated.models.Episode
import org.stephenbrewer.rick_and_morty.provider.generated.models.Location
import org.stephenbrewer.rick_and_morty.provider.generated.models.LocationInfo
import org.stephenbrewer.rick_and_morty.provider.generated.models.PageCharacter
import org.stephenbrewer.rick_and_morty.provider.generated.models.PageEpisode
import org.stephenbrewer.rick_and_morty.provider.generated.models.PageInfo
import org.stephenbrewer.rick_and_morty.provider.generated.models.PageLocation
import java.net.URI
import java.time.OffsetDateTime

fun setupDynamicFakeData(
    characterApi: CharacterApi,
    episodeApi: EpisodeApi,
    locationApi: LocationApi,
) {
    coEvery {
        characterApi.characterGetWithHttpInfo(
            page = any(),
            name = any(),
            status = any(),
            species = any(),
            type = any(),
            gender = any(),
        )
    } answers {
        val id: Int = firstArg()
        Success(
            dynamicFakeCharactersByPage(id)
        )
    }
    coEvery {
        episodeApi.episodeGetWithHttpInfo(
            page = any(),
            name = any(),
            episode = any(),
        )
    } answers {
        val id: Int = firstArg()
        Success(
            dynamicFakeEpisodesByPage(id)
        )
    }
    coEvery {
        locationApi.locationGetWithHttpInfo(
            page = any(),
            name = any(),
            type = any(),
            dimension = any(),
        )
    } answers {
        val id: Int = firstArg()
        Success(
            dynamicFakeLocationsByPage(id)
        )
    }
    coEvery { characterApi.characterIdGetWithHttpInfo(any()) } answers {
        val id: Int = firstArg()
        Success(
            dynamicFakeCharacter(id)
        )
    }
    coEvery { episodeApi.episodeIdGetWithHttpInfo(any()) } answers {
        val id: Int = firstArg()
        Success(
            dynamicFakeEpisode(id)
        )
    }
    coEvery { locationApi.locationIdGetWithHttpInfo(any()) } answers {
        val id: Int = firstArg()
        Success(dynamicFakeLocation(id))
    }
    coEvery { characterApi.characterIdsGetWithHttpInfo(any()) } answers {
        val ids: List<Int> = firstArg()
        Success(dynamicFakeCharacters(ids))
    }
    coEvery { episodeApi.episodeIdsGetWithHttpInfo(any()) } answers {
        val ids: List<Int> = firstArg()
        Success(dynamicFakeEpisodes(ids))
    }
    coEvery { locationApi.locationIdsGetWithHttpInfo(any()) } answers {
        val ids: List<Int> = firstArg()
        Success(dynamicFakeLocations(ids))
    }
}

fun dynamicFakeCharacter(id: Int) = Character(
    id = id,
    name = "Character$id",
    status = Character.Status.ALIVE,
    species = "Species$id",
    type = "Type$id",
    gender = Character.Gender.GENDERLESS,
    origin = dynamicFakeLocationInfo(id),
    location = dynamicFakeLocationInfo(id),
    image = dynamicUriWithId(id),
    episode = dynamicListOfUri(id),
    url = dynamicUriWithId(id),
    created = OffsetDateTime.now(),
)

fun dynamicFakeEpisode(id: Int) = Episode(
    id = id,
    name = "Episode$id",
    airDate = "AirDate$id",
    episode = "Episode$id",
    characters = dynamicListOfUri(id),
    url = dynamicUriWithId(id),
    created = OffsetDateTime.now(),
)

fun dynamicFakeLocation(id: Int) = Location(
    id = id,
    name = "Location$id",
    type = "Type$id",
    dimension = "Dimension$id",
    residents = dynamicListOfUri(id),
    url = dynamicUriWithId(id),
    created = OffsetDateTime.now(),
)

fun dynamicFakeCharacters(ids: List<Int>): List<Character> {
    val characters = mutableListOf<Character>()
    ids.forEach { id ->
        characters.add(dynamicFakeCharacter(id))
    }

    return characters
}

fun dynamicFakeEpisodes(ids: List<Int>): List<Episode> {
    val episodes = mutableListOf<Episode>()
    ids.forEach { id ->
        episodes.add(dynamicFakeEpisode(id))
    }

    return episodes
}

fun dynamicFakeLocations(ids: List<Int>): List<Location> {
    val locations = mutableListOf<Location>()
    ids.forEach { id ->
        locations.add(dynamicFakeLocation(id))
    }

    return locations
}

fun dynamicFakeLocationInfo(id: Int) = LocationInfo(
    name = "Location$id",
    url = dynamicUriWithId(id),
)

fun dynamicListOfUri(count: Int): List<URI> {
    val uris = mutableListOf<URI>()
    for (id in 1..count) {
        uris.add(dynamicUriWithId(id))
    }
    return uris
}

fun dynamicUriWithId(id: Int): URI = URI.create("https://host/path/$id")

const val PAGE_TOTAL = 5
const val PAGE_SIZE = 20
fun dynamicFakeCharactersByPage(page: Int): PageCharacter {
    val characters = mutableListOf<Character>()
    val idStart = (page - 1) * PAGE_SIZE
    for (id in 1..PAGE_SIZE) {
        characters.add(dynamicFakeCharacter(idStart + id))
    }
    return PageCharacter(
        info = PageInfo(
            count = PAGE_TOTAL + PAGE_SIZE,
            prev = if (page > 1) URI.create("https://host/path/any") else null,
            next = if (page < PAGE_TOTAL) URI.create("https://host/path/any") else null,
        ),
        results = characters,
    )
}
fun dynamicFakeEpisodesByPage(page: Int): PageEpisode {
    val episodes = mutableListOf<Episode>()
    val idStart = (page - 1) * PAGE_SIZE
    for (id in 1..PAGE_SIZE) {
        episodes.add(dynamicFakeEpisode(idStart + id))
    }
    return PageEpisode(
        info = PageInfo(
            count = PAGE_TOTAL + PAGE_SIZE,
            prev = if (page > 1) URI.create("https://host/path/any") else null,
            next = if (page < PAGE_TOTAL) URI.create("https://host/path/any") else null,
        ),
        results = episodes,
    )
}
fun dynamicFakeLocationsByPage(page: Int): PageLocation {
    val locations = mutableListOf<Location>()
    val idStart = (page - 1) * PAGE_SIZE
    for (id in 1..PAGE_SIZE) {
        locations.add(dynamicFakeLocation(idStart + id))
    }
    return PageLocation(
        info = PageInfo(
            count = PAGE_TOTAL + PAGE_SIZE,
            prev = if (page > 1) URI.create("https://host/path/any") else null,
            next = if (page < PAGE_TOTAL) URI.create("https://host/path/any") else null,
        ),
        results = locations,
    )
}
