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
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor
import java.util.*

@DgsScalar(name = "Cursor")
class CursorScalar : Coercing<Cursor?, String?> {

    @Throws(CoercingSerializeException::class)
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String? {
        return when (dataFetcherResult) {
            is Cursor -> dataFetcherResult.toString()
            else -> throw CoercingSerializeException()
        }
    }

    @Throws(CoercingParseValueException::class)
    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): Cursor? {
        return when (input) {
            is String -> Cursor(input)
            else -> throw CoercingParseValueException()
        }
    }

    @Throws(CoercingParseLiteralException::class)
    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): Cursor? {
        return when (input) {
            is StringValue -> Cursor(input.value)
            else -> null
        }
    }

    @Throws(CoercingParseValueException::class)
    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> {
        throw CoercingParseValueException()
    }
}
