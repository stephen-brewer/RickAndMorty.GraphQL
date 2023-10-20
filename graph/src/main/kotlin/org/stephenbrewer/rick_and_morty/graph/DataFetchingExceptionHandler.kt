package org.stephenbrewer.rick_and_morty.graph

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import org.stephenbrewer.rick_and_morty.modelTransformation.DataFetcherException
import org.stephenbrewer.rick_and_morty.modelTransformation.toGraphQLError
import java.util.concurrent.CompletableFuture

@Component
class DataFetcherExceptionHandler : DataFetcherExceptionHandler {
    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        return when (val exception = handlerParameters.exception) {
            is DataFetcherException -> {
                val graphQLError = exception.toGraphQLError(handlerParameters)
                val result = DataFetcherExceptionHandlerResult
                    .newResult(graphQLError)
                    .build()
                CompletableFuture.completedFuture(result)
            }
            else -> super.handleException(handlerParameters)
        }
    }
}
