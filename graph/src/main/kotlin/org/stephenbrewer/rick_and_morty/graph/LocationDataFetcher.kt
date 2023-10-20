package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import graphql.execution.DataFetcherResult
import org.stephenbrewer.rick_and_morty.modelTransformation.LocalContext
import org.stephenbrewer.rick_and_morty.modelTransformation.LocationService
import org.stephenbrewer.rick_and_morty.modelTransformation.RelayPageCalculator
import org.stephenbrewer.rick_and_morty.modelTransformation.SingleResultAndLocalContext
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.DgsConstants
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Location
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.LocationConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor

@DgsComponent
class LocationDataFetcher(
    val locationService: LocationService,
) {
    @DgsQuery
    suspend fun locations(
        @InputArgument first: Int?,
        @InputArgument last: Int?,
        @InputArgument after: Cursor?,
        @InputArgument before: Cursor?,
    ): DataFetcherResult<LocationConnection> {
        return resultWithLocalContext {
            val relayPageCalculator = RelayPageCalculator(
                first,
                last,
                after,
                before,
            )

            locationService.locations(relayPageCalculator)
        }
    }

    @DgsQuery
    suspend fun location(
        @InputArgument id : Int,
    ): DataFetcherResult<Location> = resultWithLocalContext {
        locationService.location(id)
    }

    @DgsData(parentType = DgsConstants.CHARACTER.TYPE_NAME)
    suspend fun origin(
        dfe: DgsDataFetchingEnvironment
    ): DataFetcherResult<Location> = resultWithLocalContext {
        val parentId = dfe.getSource<Character>().id
        val localContext = dfe.getLocalContext<LocalContext>()
        val originId = localContext.origins[parentId]
        if (originId != null) {
            locationService.location(originId)
        } else {
            SingleResultAndLocalContext(null, LocalContext())
        }
    }

    @DgsData(parentType = DgsConstants.CHARACTER.TYPE_NAME)
    suspend fun location(
        dfe: DgsDataFetchingEnvironment
    ): DataFetcherResult<Location> = resultWithLocalContext {
        val parentId = dfe.getSource<Character>().id
        val localContext = dfe.getLocalContext<LocalContext>()
        val locationId = localContext.locations[parentId]
        if (locationId != null) {
            locationService.location(locationId)
        } else {
            SingleResultAndLocalContext(null, LocalContext())
        }
    }
}
