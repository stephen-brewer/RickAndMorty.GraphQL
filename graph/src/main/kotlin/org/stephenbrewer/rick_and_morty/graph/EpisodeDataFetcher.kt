package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.execution.DataFetcherResult
import org.stephenbrewer.rick_and_morty.modelTransformation.EpisodeService
import org.stephenbrewer.rick_and_morty.modelTransformation.LocalContext
import org.stephenbrewer.rick_and_morty.modelTransformation.RelayPageCalculator
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.DgsConstants
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Episode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.EpisodeConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor

@DgsComponent
class EpisodeDataFetcher(
    val episodeService: EpisodeService,
) {
    @DgsQuery
    suspend fun episodes(
        @InputArgument first: Int?,
        @InputArgument last: Int?,
        @InputArgument after: Cursor?,
        @InputArgument before: Cursor?,
    ): DataFetcherResult<EpisodeConnection> {
        return resultWithLocalContext {
            val relayPageCalculator = RelayPageCalculator(
                first,
                last,
                after,
                before,
            )

            episodeService.episodes(relayPageCalculator)
        }
    }

    @DgsQuery
    suspend fun episode(
        @InputArgument id : Int,
    ): DataFetcherResult<Episode> = resultWithLocalContext {
        episodeService.episode(id)
    }

    @DgsData(parentType = DgsConstants.CHARACTER.TYPE_NAME)
    suspend fun episodes(
        dfe: DgsDataFetchingEnvironment
    ): DataFetcherResult<List<Episode>> = resultWithLocalContext {
        val parentId = dfe.getSource<Character>().id
        val localContext = dfe.getLocalContext<LocalContext>()
        episodeService.episodesByIds(localContext.episodes[parentId].orEmpty())
    }
}
