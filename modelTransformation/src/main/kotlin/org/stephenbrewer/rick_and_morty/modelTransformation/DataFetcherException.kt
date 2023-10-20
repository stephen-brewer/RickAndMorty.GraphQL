package org.stephenbrewer.rick_and_morty.modelTransformation

import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandlerParameters
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.ErrorCode
import org.stephenbrewer.rick_and_morty.modelTransformation.ErrorClassification.Data
import org.stephenbrewer.rick_and_morty.modelTransformation.ErrorClassification.Internal
import org.stephenbrewer.rick_and_morty.modelTransformation.ErrorClassification.Unknown
import org.stephenbrewer.rick_and_morty.modelTransformation.ErrorClassification.UpstreamError

sealed class DataFetcherException(
    val code: ErrorCode,
    val classification: ErrorClassification,
    message: String,
    cause: Throwable?,
) : Exception(
    message,
    cause,
)

fun DataFetcherException.toGraphQLError(handlerParameters: DataFetcherExceptionHandlerParameters): GraphQLError {
    return GraphQLError.newError()
        .message(this.message)
        .location(handlerParameters.sourceLocation)
        .path(handlerParameters.path)
        .extensions(mutableMapOf<String, Any>("code" to this.code))
        .errorType(this.classification)
        .build()
}

class NotFound(
    message: String = "NotFound",
    cause: Throwable? = null,
) : DataFetcherException(
    ErrorCode.NotFound,
    Data,
    message,
    cause,
)

class MissingData(
    message: String = "MissingData",
    cause: Throwable? = null,
) : DataFetcherException(
    ErrorCode.MissingData,
    Data,
    message,
    cause,
)

class UpstreamError(
    message: String = "UpstreamError",
    cause: Throwable? = null,
) : DataFetcherException(
    ErrorCode.UpstreamError,
    UpstreamError,
    message,
    cause,
)

class NotImplemented(
    message: String = "NotImplemented",
    cause: Throwable? = null,
) : DataFetcherException(
    ErrorCode.NotImplemented,
    Internal,
    message,
    cause,
)

class Unknown(
    message: String = "Unknown",
    cause: Throwable? = null,
) : DataFetcherException(
    ErrorCode.Unknown,
    Unknown,
    message,
    cause,
)

enum class ErrorClassification : graphql.ErrorClassification {
    Data,
    UpstreamError,
    Internal,
    Unknown,
}
