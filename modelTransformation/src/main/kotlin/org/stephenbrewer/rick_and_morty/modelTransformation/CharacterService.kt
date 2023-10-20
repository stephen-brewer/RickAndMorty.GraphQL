package org.stephenbrewer.rick_and_morty.modelTransformation

import org.springframework.stereotype.Service
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.CharacterConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.CharacterEdge
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.PageInfo
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi

@Service
class CharacterService(private val characterService: CharacterApi) {

    suspend fun characters(
        relayPageCalculator: RelayPageCalculator,
    ): SingleResultAndLocalContext<CharacterConnection> {
        val listOfPageData = relayPageCalculator.listOfPages.map {  page ->
            val response = characterService.characterGetWithHttpInfo(
                page = page,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null,
            )
            response.toResult { original ->
                val filteredResults = original.results.orEmpty()
                    .filter {
                        val index = it.id - 1
                        index >= relayPageCalculator.startCursor.index && index <= relayPageCalculator.endCursor.index
                    }

                val totalResults = original.info?.count ?: Int.MAX_VALUE
                val hasPrevious: Boolean = filteredResults.first().id > 1
                val hasNext: Boolean = filteredResults.last().id < totalResults

                val pageCharacterEdges: List<CharacterEdge> = filteredResults
                    .map {
                        val index = it.id - 1
                        CharacterEdge(
                            Cursor.from(index),
                            Character.from(it),
                        )
                    }
                val localContext = extractLocalContext(filteredResults)

                PagedResultAndLocalContext(pageCharacterEdges, localContext, hasPrevious, hasNext)
            }
        }

        val characterEdges = listOfPageData.flatMap { response ->
            response.data.orEmpty()
        }
        val hasPrevious = listOfPageData.first().hasPrevious
        val hasNext = listOfPageData.last().hasNext
        val characterConnection = CharacterConnection(
            edges = characterEdges,
            pageInfo = PageInfo(
                hasPrevious = hasPrevious,
                hasNext = hasNext,
                startCursor = relayPageCalculator.startCursor,
                endCursor = relayPageCalculator.endCursor,
            )
        )
        val localContext: LocalContext = listOfPageData.extractLocalContext()

        return SingleResultAndLocalContext(
            data = characterConnection,
            localContext = localContext,
        )
    }

    suspend fun charactersByIds(ids: List<Int>): SingleResultAndLocalContext<List<Character>> {
        val localIds = ids.ensureMoreThanOne()
        return characterService.characterIdsGetWithHttpInfo(localIds).toResult { original ->
            val characters = original.map {
                Character.from(it)
            }
            val localContext = extractLocalContext(original)

            SingleResultAndLocalContext(characters, localContext)
        }
    }

    suspend fun character(characterId: Int): SingleResultAndLocalContext<Character> {
        return characterService.characterIdGetWithHttpInfo(characterId)
            .toResult { original ->
                val episodes = original.mapOrEmpty { character ->
                    character.id to character.episode?.map {
                        it.path.split("/").last().toInt()
                    }
                }
                val origins = original.mapOrEmpty { character ->
                    val url = character.origin?.url?.path?.split("/")?.last()
                    if (url.isNullOrEmpty()) {
                        null
                    } else {
                        character.id to url.toInt()
                    }
                }
                val locations = original.mapOrEmpty { character ->
                    val url = character.location?.url?.path?.split("/")?.last()
                    if (url.isNullOrEmpty()) {
                        null
                    } else {
                        character.id to url.toInt()
                    }
                }
                val localContext = LocalContext(
                    episodes = episodes,
                    origins = origins,
                    locations = locations,
                )
                SingleResultAndLocalContext(Character.from(original), localContext)
            }
    }

    private fun extractLocalContext(characters: List<org.stephenbrewer.rick_and_morty.provider.generated.models.Character>): LocalContext {
        val episodes = characters.mapOrEmpty<org.stephenbrewer.rick_and_morty.provider.generated.models.Character, List<Int>> { character ->
            character.id to character.episode?.map {
                it.path.split("/").last().toInt()
            }
        }
        val origins = characters.mapOrEmpty<org.stephenbrewer.rick_and_morty.provider.generated.models.Character, Int> { character ->
            val url = character.origin?.url?.path?.split("/")?.last()
            if (url.isNullOrEmpty()) {
                null
            } else {
                character.id to url.toInt()
            }
        }
        val locations = characters.mapOrEmpty<org.stephenbrewer.rick_and_morty.provider.generated.models.Character, Int> { character ->
            val url = character.location?.url?.path?.split("/")?.last()
            if (url.isNullOrEmpty()) {
                null
            } else {
                character.id to url.toInt()
            }
        }
        return LocalContext(
            episodes = episodes,
            origins = origins,
            locations = locations,
        )
    }
}
