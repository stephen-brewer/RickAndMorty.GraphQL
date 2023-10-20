package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.execution.DataFetcherResult
import org.stephenbrewer.rick_and_morty.modelTransformation.CharacterService
import org.stephenbrewer.rick_and_morty.modelTransformation.LocalContext
import org.stephenbrewer.rick_and_morty.modelTransformation.RelayPageCalculator
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.DgsConstants
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.CharacterConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Episode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Location
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor

@DgsComponent
class CharacterDataFetcher(
    val characterService: CharacterService,
) {
    @DgsQuery
    suspend fun characters(
        @InputArgument first: Int?,
        @InputArgument last: Int?,
        @InputArgument after: Cursor?,
        @InputArgument before: Cursor?,
    ): DataFetcherResult<CharacterConnection> {
        return resultWithLocalContext {
            val relayPageCalculator = RelayPageCalculator(
                first,
                last,
                after,
                before,
            )

            characterService.characters(relayPageCalculator)
        }
    }

    @DgsQuery
    suspend fun character(
        @InputArgument id : Int,
    ): DataFetcherResult<Character> = resultWithLocalContext {
        characterService.character(id)
    }

    @DgsData(parentType = DgsConstants.EPISODE.TYPE_NAME)
    suspend fun characters(
        dfe: DgsDataFetchingEnvironment
    ): DataFetcherResult<List<Character>> = resultWithLocalContext {
        val parentId = dfe.getSource<Episode>().id
        val localContext = dfe.getLocalContext<LocalContext>()
        dfe.executionStepInfo.field.singleField.sourceLocation
        characterService.charactersByIds(localContext.characters[parentId].orEmpty())
    }

    @DgsData(parentType = DgsConstants.LOCATION.TYPE_NAME)
    suspend fun residents(
        dfe: DgsDataFetchingEnvironment
    ): DataFetcherResult<List<Character>> = resultWithLocalContext {
        val parentId = dfe.getSource<Location>().id
        val localContext = dfe.getLocalContext<LocalContext>()
        characterService.charactersByIds(localContext.characters[parentId].orEmpty())
    }
}
