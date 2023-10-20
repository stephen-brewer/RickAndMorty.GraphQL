package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.stephenbrewer.rick_and_morty.modelTransformation.ErrorClassification
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.ErrorCode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Location
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.LocationConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.ClientError
import java.util.stream.Stream

@SpringBootTest
class LocationDataFetcherTest(
    @Autowired val dgsQueryExecutor: DgsQueryExecutor,
) {
    @MockkBean
    private lateinit var characterApi: CharacterApi

    @MockkBean
    private lateinit var episodeApi: EpisodeApi

    @MockkBean
    private lateinit var locationApi: LocationApi

    @BeforeEach
    fun setUp() {
        setupDynamicFakeData(characterApi, episodeApi, locationApi)
    }

    @Test
    fun testQueryLocation() {
        val location = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Location {
                  location(id: 1) {
                    id
                    name
                    type
                    created
                  }
                }
            """.trimIndent(),
            "data.location",
            mutableMapOf("id" to 1 as Any),
            Location::class.java,
        )

        Assertions.assertEquals(1, location.id)
        Assertions.assertEquals("Location1", location.name)
        Assertions.assertEquals("Type1", location.type)
    }

    @Test
    fun testQueryLocations() {
        val locationConnection = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Locations {
                  locations(
                    first: 1
                  ) {
                    pageInfo {
                      startCursor
                      endCursor
                      hasPrevious
                      hasNext
                    }
                     edges {
                      cursor
                      node {
                        id
                        name
                      }
                    }
                  }
                }
            """.trimIndent(),
            "data.locations",
            mutableMapOf("id" to 1 as Any),
            LocationConnection::class.java,
        )

        Assertions.assertEquals(Cursor("1:1"), locationConnection.pageInfo.startCursor)
        Assertions.assertEquals(Cursor("1:1"), locationConnection.pageInfo.endCursor)
        Assertions.assertEquals(false, locationConnection.pageInfo.hasPrevious)
        Assertions.assertEquals(true, locationConnection.pageInfo.hasNext)
        Assertions.assertEquals(1, locationConnection.edges.size)
        Assertions.assertEquals(Cursor("1:1"), locationConnection.edges.first().cursor)
        Assertions.assertEquals(1, locationConnection.edges.first().node.id)
        Assertions.assertEquals("Location1", locationConnection.edges.first().node.name)
    }

    companion object {
        @JvmStatic
        fun testErrors(): Stream<Arguments> = Stream.of(
            Arguments.of(ErrorClassification.UpstreamError, ErrorCode.UpstreamError, 201),
            Arguments.of(ErrorClassification.UpstreamError, ErrorCode.UpstreamError, 304),
            Arguments.of(ErrorClassification.Data, ErrorCode.NotFound, 404),
            Arguments.of(ErrorClassification.UpstreamError, ErrorCode.UpstreamError, 418),
            Arguments.of(ErrorClassification.UpstreamError, ErrorCode.UpstreamError, 500),
            Arguments.of(ErrorClassification.UpstreamError, ErrorCode.UpstreamError, 501),
        )
    }
    @ParameterizedTest
    @MethodSource("testErrors")
    fun testNotFoundError(
        expectedClassification: ErrorClassification,
        expectedCode: ErrorCode,
        returnCode: Int,
    ) {
        coEvery { locationApi.locationIdGetWithHttpInfo(1) } returns mockk<ClientError<org.stephenbrewer.rick_and_morty.provider.generated.models.Location?>> {
            every { statusCode } returns returnCode
        }

        val result = dgsQueryExecutor.execute(
            """
                query Location {
                  location(id: 1) {
                    id
                  }
                }
            """.trimIndent(),
            mutableMapOf("id" to 1 as Any),
        )

        Assertions.assertEquals(1, result.errors.size)
        Assertions.assertEquals(expectedClassification, result.errors.first().errorType)
        Assertions.assertEquals(expectedCode, result.errors.first().extensions["code"])
        Assertions.assertEquals(null, result.getData<Map<String, *>>()["location"])
    }
}
