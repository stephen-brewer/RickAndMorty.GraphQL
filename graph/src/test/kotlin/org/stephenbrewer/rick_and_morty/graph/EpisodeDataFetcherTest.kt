package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Episode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.EpisodeConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi

@SpringBootTest
class EpisodeDataFetcherTest(
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
    fun testQueryEpisode() {

        val episode = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Episode {
                  episode(id: 1) {
                    id
                    name
                    created
                  }
                }
            """.trimIndent(),
            "data.episode",
            mutableMapOf("id" to 1 as Any),
            Episode::class.java,
        )

        Assertions.assertEquals(1, episode.id)
        Assertions.assertEquals("Episode1", episode.name)
    }

    @Test
    fun testQueryEpisodes() {

        val characterConnection = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Episodes {
                  episodes(
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
                        characters {
                          id
                        }
                      }
                    }
                  }
                }
            """.trimIndent(),
            "data.episodes",
            mutableMapOf("id" to 1 as Any),
            EpisodeConnection::class.java,
        )

        Assertions.assertEquals(Cursor("1:1"), characterConnection.pageInfo.startCursor)
        Assertions.assertEquals(Cursor("1:1"), characterConnection.pageInfo.endCursor)
        Assertions.assertEquals(false, characterConnection.pageInfo.hasPrevious)
        Assertions.assertEquals(true, characterConnection.pageInfo.hasNext)
        Assertions.assertEquals(1, characterConnection.edges.size)
        Assertions.assertEquals(Cursor("1:1"), characterConnection.edges.first().cursor)
        Assertions.assertEquals(1, characterConnection.edges.first().node.id)
        Assertions.assertEquals("Episode1", characterConnection.edges.first().node.name)
    }
}
