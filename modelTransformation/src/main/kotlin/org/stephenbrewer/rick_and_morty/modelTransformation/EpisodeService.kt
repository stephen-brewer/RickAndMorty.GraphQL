package org.stephenbrewer.rick_and_morty.modelTransformation

import org.springframework.stereotype.Service
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Episode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.EpisodeConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.EpisodeEdge
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.PageInfo
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi

@Service
class EpisodeService(private val episodeService: EpisodeApi) {

    suspend fun episodes(
        relayPageCalculator: RelayPageCalculator,
    ): SingleResultAndLocalContext<EpisodeConnection> {
        val listOfPageData = relayPageCalculator.listOfPages.map {  page ->
            val response = episodeService.episodeGetWithHttpInfo(
                page = page,
                name = null,
                episode= null,
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

                val pageEpisodeEdges: List<EpisodeEdge> = filteredResults
                    .map {
                        val index = it.id - 1
                        EpisodeEdge(
                            Cursor.from(index),
                            Episode.from(it),
                        )
                    }
                val localContext = extractLocalContext(filteredResults)

                PagedResultAndLocalContext(pageEpisodeEdges, localContext, hasPrevious, hasNext)
            }
        }

        val episodeEdges = listOfPageData.flatMap { pageData ->
            pageData.data.orEmpty()
        }
        val hasPrevious = listOfPageData.first().hasPrevious
        val hasNext = listOfPageData.last().hasNext
        val episodeConnection = EpisodeConnection(
            edges = episodeEdges,
            pageInfo = PageInfo(
                hasPrevious = hasPrevious,
                hasNext = hasNext,
                startCursor = relayPageCalculator.startCursor,
                endCursor = relayPageCalculator.endCursor,
            )
        )
        val localContext: LocalContext = listOfPageData.extractLocalContext()

        return SingleResultAndLocalContext(
            data = episodeConnection,
            localContext = localContext,
        )
    }

    suspend fun episodesByIds(ids: List<Int>): SingleResultAndLocalContext<List<Episode>> {
        return episodeService.episodeIdsGetWithHttpInfo(ids).toResult { original ->
            val episodes = original.map {
                Episode.from(it)
            }
            val localContext = extractLocalContext(original)
            SingleResultAndLocalContext(episodes, localContext)
        }
    }

    suspend fun episode(id: Int): SingleResultAndLocalContext<Episode> = episodeService.episodeIdGetWithHttpInfo(id).toResult { original ->
        val characters = original.characters.orEmpty().map {
            it.path.split("/").last().toInt()
        }
        val localContext = LocalContext(
            characters = mapOf(original.id to characters),
        )
        val episode = Episode.from(original)
        SingleResultAndLocalContext(episode, localContext)
    }

    private fun extractLocalContext(episodes: List<org.stephenbrewer.rick_and_morty.provider.generated.models.Episode>): LocalContext {
        val characters = episodes.associate { episode ->
            val characters = episode.characters.orEmpty().map {
                it.path.split("/").last().toInt()
            }
            episode.id to characters
        }

        return LocalContext(
            characters = characters,
        )
    }
}
