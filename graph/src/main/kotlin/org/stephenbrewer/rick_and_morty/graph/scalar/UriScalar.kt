package org.stephenbrewer.rick_and_morty.graph.scalar

import com.netflix.graphql.dgs.DgsScalar
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.net.URI
import java.util.*

@DgsScalar(name = "Uri")
class UriScalar : Coercing<URI?, String?> {
    @Throws(CoercingSerializeException::class)
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String? {
        return if (dataFetcherResult is URI) {
            val uri = dataFetcherResult.toASCIIString()
            uri.ifBlank { null }
        } else {
            throw CoercingSerializeException("Not a valid URI")
        }
    }

    @Throws(CoercingParseValueException::class)
    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): URI {
        return URI.create(input.toString())
    }

    @Throws(CoercingParseLiteralException::class)
    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): URI {
        if (input is StringValue) {
            return URI.create(input.toString())
        }
        throw CoercingParseLiteralException("Value is not a valid URI")
    }
}
