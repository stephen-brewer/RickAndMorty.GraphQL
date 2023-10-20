package org.stephenbrewer.rick_and_morty.modelTransformation

import org.springframework.stereotype.Service
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Location
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.LocationConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.LocationEdge
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.PageInfo
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi

@Service
class LocationService(private val locationService: LocationApi) {

    suspend fun locations(
        relayPageCalculator: RelayPageCalculator,
    ): SingleResultAndLocalContext<LocationConnection> {
        val listOfPageData = relayPageCalculator.listOfPages.map {  page ->
            val response = locationService.locationGetWithHttpInfo(
                page = page,
                name = null,
                type = null,
                dimension = null,
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

                val pageLocationEdges: List<LocationEdge> = filteredResults
                    .map {
                        val index = it.id - 1
                        LocationEdge(
                            Cursor.from(index),
                            Location.from(it),
                        )
                    }
                val localContext = extractLocalContext(filteredResults)

                PagedResultAndLocalContext(pageLocationEdges, localContext, hasPrevious, hasNext)
            }
        }

        val locationEdges = listOfPageData.flatMap { pageData ->
            pageData.data.orEmpty()
        }
        val hasPrevious = listOfPageData.first().hasPrevious
        val hasNext = listOfPageData.last().hasNext
        val locationConnection = LocationConnection(
            edges = locationEdges,
            pageInfo = PageInfo(
                hasPrevious = hasPrevious,
                hasNext = hasNext,
                startCursor = relayPageCalculator.startCursor,
                endCursor = relayPageCalculator.endCursor,
            )
        )
        val localContext: LocalContext = listOfPageData.extractLocalContext()

        return SingleResultAndLocalContext(
            data = locationConnection,
            localContext = localContext,
        )
    }

    suspend fun location(id: Int): SingleResultAndLocalContext<Location> = locationService.locationIdGetWithHttpInfo(id).toResult { original ->
        val residents = original.residents.orEmpty().map {
            it.path.split("/").last().toInt()
        }
        val localContext = LocalContext(
            characters = mapOf(original.id to residents),
        )
        val location = Location.from(original)
        SingleResultAndLocalContext(location, localContext)
    }

    private fun extractLocalContext(episodes: List<org.stephenbrewer.rick_and_morty.provider.generated.models.Location>): LocalContext {
        val characters = episodes.associate { location ->
            val characters = location.residents.orEmpty().map {
                it.path.split("/").last().toInt()
            }
            location.id to characters
        }

        return LocalContext(
            characters = characters,
        )
    }
}
