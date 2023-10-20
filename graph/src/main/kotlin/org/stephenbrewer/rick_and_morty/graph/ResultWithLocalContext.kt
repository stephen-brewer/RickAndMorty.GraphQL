package org.stephenbrewer.rick_and_morty.graph

import graphql.execution.DataFetcherResult
import org.stephenbrewer.rick_and_morty.modelTransformation.ResultAndLocalContext

suspend fun <T>resultWithLocalContext(code: suspend () -> ResultAndLocalContext<T>): DataFetcherResult<T> {
    val response = code()

    return DataFetcherResult
        .newResult<T>()
        .data(response.data)
        .localContext(response.localContext)
        .build()
}
