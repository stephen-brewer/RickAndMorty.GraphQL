package org.stephenbrewer.rick_and_morty.graph

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.CharacterConnection
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Gender
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Status
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import org.stephenbrewer.rick_and_morty.provider.generated.apis.CharacterApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.EpisodeApi
import org.stephenbrewer.rick_and_morty.provider.generated.apis.LocationApi
import java.net.URI

@SpringBootTest
class CharacterDataFetcherTest(
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
    fun testQueryCharacter() {
        val character = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Character {
                  character(id: 1) {
                    id
                    name
                    status
                    species
                    type
                    gender
                    origin {
                        id
                        name
                        type
                        dimension
                    }
                    location {
                        id
                        name
                        type
                        dimension
                    }
                    image
                    episodes {
                        id
                        name
                        code
                    }
                    created
                  }
                }
            """.trimIndent(),
            "data.character",
            mutableMapOf("id" to 1 as Any),
            Character::class.java,
        )

        Assertions.assertEquals(1, character.id)
        Assertions.assertEquals("Character1", character.name)
        Assertions.assertEquals(Status.ALIVE, character.status)
        Assertions.assertEquals("Species1", character.species)
        Assertions.assertEquals("Type1", character.type)
        Assertions.assertEquals(Gender.GENDERLESS, character.gender)
        Assertions.assertEquals(1, character.origin?.id)
        Assertions.assertEquals("Location1", character.origin?.name)
        Assertions.assertEquals("Type1", character.origin?.type)
        Assertions.assertEquals("Dimension1", character.origin?.dimension)
        Assertions.assertEquals(1, character.location?.id)
        Assertions.assertEquals("Location1", character.location?.name)
        Assertions.assertEquals("Type1", character.location?.type)
        Assertions.assertEquals("Dimension1", character.location?.dimension)
        Assertions.assertEquals(URI.create("https://host/path/1"), character.image)
        Assertions.assertEquals(1, character.episodes?.count())
        Assertions.assertEquals(1, character.episodes?.first()?.id)
        Assertions.assertEquals("Episode1", character.episodes?.first()?.name)
//        Assertions.assertEquals("1", character.created)
    }

    @Test
    fun testQueryCharacters() {

        val characterConnection = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query Characters {
                  characters(
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
                        origin {
                          id
                          name
                        }
                        location {
                          id
                          name
                        }
                        episodes {
                          id
                          code
                        }
                      }
                    }
                  }
                }
            """.trimIndent(),
            "data.characters",
            mutableMapOf("id" to 1 as Any),
            CharacterConnection::class.java,
        )

        Assertions.assertEquals(Cursor("1:1"), characterConnection.pageInfo.startCursor)
        Assertions.assertEquals(Cursor("1:1"), characterConnection.pageInfo.endCursor)
        Assertions.assertEquals(false, characterConnection.pageInfo.hasPrevious)
        Assertions.assertEquals(true, characterConnection.pageInfo.hasNext)
        Assertions.assertEquals(1, characterConnection.edges.size)
        Assertions.assertEquals(Cursor("1:1"), characterConnection.edges.first().cursor)
        Assertions.assertEquals(1, characterConnection.edges.first().node.id)
        Assertions.assertEquals("Character1", characterConnection.edges.first().node.name)
        Assertions.assertEquals("Location1", characterConnection.edges.first().node.origin?.name)
        Assertions.assertEquals(1, characterConnection.edges.first().node.episodes?.size)
        Assertions.assertEquals(1, characterConnection.edges.first().node.episodes?.first()?.id)
    }
}
