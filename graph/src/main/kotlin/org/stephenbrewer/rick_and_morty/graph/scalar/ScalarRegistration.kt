package org.stephenbrewer.rick_and_morty.graph.scalar

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsRuntimeWiring
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring

@DgsComponent
class ScalarRegistration {
    @DgsRuntimeWiring
    fun addCursorScalar(builder: RuntimeWiring.Builder): RuntimeWiring.Builder {
        val scalar = GraphQLScalarType.newScalar()
            .name("Cursor")
            .description("Cursor scalar for Relay")
            .coercing(CursorScalar())
            .build()
        return builder.scalar(scalar)
    }
}
