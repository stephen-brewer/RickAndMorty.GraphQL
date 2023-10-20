package org.stephenbrewer.rick_and_morty.modelTransformation

import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.ApiResponse
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.ClientError
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.Informational
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.Redirection
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.ServerError
import org.stephenbrewer.rick_and_morty.provider.generated.infrastructure.Success
import org.stephenbrewer.rick_and_morty.modelTransformation.LocalContext.Companion

interface ResultAndLocalContext<T> {
    val data: T?
    val localContext: LocalContext?
}

data class PagedResultAndLocalContext<L: List<T>, T>(
    override val data: L? = null,
    override val localContext: LocalContext? = LocalContext.EMPTY,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false,
) : ResultAndLocalContext<L>

data class SingleResultAndLocalContext<T>(
    override val data: T? = null,
    override val localContext: LocalContext = LocalContext.EMPTY,
) : ResultAndLocalContext<T>

suspend fun <R, T> ApiResponse<T?>.toResult(code: suspend (T) -> R): R {
    return when (this) {
        is Success -> {
            when (val serverData = this.data) {
                null -> throw MissingData()
                else -> code(serverData)
            }
        }

        is Informational -> {
            throw NotImplemented()
        }

        is Redirection -> {
            throw NotImplemented()
        }

        is ClientError -> {
            when (statusCode) {
                404 -> throw NotFound()
                else -> throw UpstreamError()
            }
        }

        is ServerError -> throw UpstreamError()
        else -> throw Unknown()
    }
}
