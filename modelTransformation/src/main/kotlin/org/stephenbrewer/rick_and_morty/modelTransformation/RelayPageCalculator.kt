package org.stephenbrewer.rick_and_morty.modelTransformation

import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor

class RelayPageCalculator(
    private val first: Int?,
    private val last: Int?,
    private val after: Cursor?,
    private val before: Cursor?,
) {

    companion object {
        const val PAGE_SIZE = 20
    }

    val listOfPages: List<Int>
    val startCursor: Cursor
    val endCursor: Cursor

    init {
        checkInputs()

        val localFirst = first ?: 0
        val localLast = last ?: 0
        val localAfter = after ?: Cursor.UNKNOWN
        val localBefore = before ?: Cursor.UNKNOWN

        if (localFirst == 0 && localLast == 0) {
            listOfPages = emptyList()
            startCursor = Cursor.UNKNOWN
            endCursor = Cursor.UNKNOWN
        } else {
            startCursor = if (localFirst > 0) localAfter + 1 else localBefore - localLast
            endCursor = if (localFirst > 0) localAfter + localFirst else localBefore - 1
            val pagesList = mutableListOf<Int>()
            for (page in startCursor.page..endCursor.page) {
                pagesList.add(page)
            }
            listOfPages = pagesList
        }
    }

    private fun checkInputs() {
        if (
            (first == null && last == null)
            ||
            (first != null && last != null)
        ) {
            throw IllegalArgumentException("Requires first or last, but not both.")
        }
        if (after != null && before != null) {
            throw IllegalArgumentException("Optional before or after, but not both.")
        }
        if (first != null && before != null) {
            throw IllegalArgumentException("Optional before with first, not supported.")
        }
        if (last != null && after != null) {
            throw IllegalArgumentException("Optional after with last, not supported.")
        }
        if (last != null && before == null) {
            throw IllegalArgumentException("Requires before with last.")
        }
    }
}
