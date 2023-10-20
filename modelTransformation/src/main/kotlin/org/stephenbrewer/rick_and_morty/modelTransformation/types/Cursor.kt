package org.stephenbrewer.rick_and_morty.modelTransformation.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import org.stephenbrewer.rick_and_morty.modelTransformation.RelayPageCalculator
import kotlin.math.max

data class Cursor(
    @JsonProperty
    @JsonValue
    val string: String,
) {

    companion object {
        val UNKNOWN = Cursor("1:0")

        fun from(index: Int): Cursor {
            val page = (index / RelayPageCalculator.PAGE_SIZE) + 1
            val pageIndex = index.mod(RelayPageCalculator.PAGE_SIZE) + 1

            return Cursor("$page:$pageIndex")
        }

        fun from(value: String?): Cursor {
            if (value == null) return UNKNOWN

            return Cursor(value)
        }
    }

    val page = string.split(':')[0].toInt()
    val pageIndex = string.split(':')[1].toInt()
    val index = (page - 1) * RelayPageCalculator.PAGE_SIZE + pageIndex - 1

    operator fun plus(value: Int): Cursor {
        return from(index + value)
    }

    operator fun minus(value: Int): Cursor {
        return from(max(index - value, 0))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return if (other is Cursor) {
            (page == other.page) && (pageIndex == other.pageIndex)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return page.hashCode().xor(pageIndex.hashCode())
    }

    override fun toString(): String {
        return "$page:$pageIndex"
    }
}
