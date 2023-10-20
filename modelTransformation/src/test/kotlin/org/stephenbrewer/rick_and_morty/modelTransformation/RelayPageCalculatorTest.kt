package org.stephenbrewer.rick_and_morty.modelTransformation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.stephenbrewer.rick_and_morty.modelTransformation.types.Cursor

class RelayPageCalculatorTest {

    companion object {
        @JvmStatic
        fun initialiseErrorData(): List<Arguments> = listOf(
            Arguments.of(null, null, null, null),
            Arguments.of(1, 2, null, null),
            Arguments.of(1, null, null, Cursor.from("1:1")),
            Arguments.of(1, null, Cursor.from("1:1"), Cursor.from("1:1")),
            Arguments.of(null, 2, Cursor.from("1:1"), null),
            Arguments.of(null, 2, Cursor.from("1:1"), Cursor.from("1:1")),
            Arguments.of(null, 2, null, null),
        )

        @JvmStatic
        fun initialiseSuccessData(): List<Arguments> = listOf(
            Arguments.of(1, null, null, null),
            Arguments.of(1, null, Cursor.from("1:1"), null),
            Arguments.of(null, 1, null, Cursor.from("1:1")),
        )

        @JvmStatic
        fun testListOfPagesData(): List<Arguments> = listOf(
            Arguments.of(0, null, null, null, emptyList<Int>()),
            Arguments.of(0, null, Cursor.from("1:1"), null, emptyList<Int>()),
            Arguments.of(null, 0, null, Cursor.from("1:1"), emptyList<Int>()),

            Arguments.of(1, null, null, null, listOf(1)),
            Arguments.of(20, null, null, null, listOf(1)),
            Arguments.of(21, null, null, null, listOf(1,2)),
            Arguments.of(30, null, null, null, listOf(1,2)),
            Arguments.of(40, null, null, null, listOf(1,2)),
            Arguments.of(41, null, null, null, listOf(1,2,3)),

            Arguments.of(1, null, Cursor.from("1:1"), null, listOf(1)),
            Arguments.of(19, null, Cursor.from("1:1"), null, listOf(1)),
            Arguments.of(20, null, Cursor.from("1:1"), null, listOf(1,2)),
            Arguments.of(21, null, Cursor.from("1:1"), null, listOf(1,2)),
            Arguments.of(30, null, Cursor.from("1:1"), null, listOf(1,2)),
            Arguments.of(40, null, Cursor.from("1:1"), null, listOf(1,2,3)),
            Arguments.of(41, null, Cursor.from("1:1"), null, listOf(1,2,3)),

            Arguments.of(1, null, Cursor.from("2:1"), null, listOf(2)),
            Arguments.of(5, null, Cursor.from("2:1"), null, listOf(2)),
            Arguments.of(20, null, Cursor.from("2:1"), null, listOf(2,3)),
            Arguments.of(21, null, Cursor.from("2:1"), null, listOf(2,3)),
            Arguments.of(30, null, Cursor.from("2:1"), null, listOf(2,3)),
            Arguments.of(40, null, Cursor.from("2:1"), null, listOf(2,3,4)),
            Arguments.of(41, null, Cursor.from("2:1"), null, listOf(2,3,4)),

            Arguments.of(1, null, Cursor.from("2:2"), null, listOf(2)),
            Arguments.of(5, null, Cursor.from("2:2"), null, listOf(2)),
            Arguments.of(17, null, Cursor.from("2:2"), null, listOf(2)),
            Arguments.of(18, null, Cursor.from("2:2"), null, listOf(2)),
            Arguments.of(19, null, Cursor.from("2:2"), null, listOf(2,3)),
            Arguments.of(20, null, Cursor.from("2:2"), null, listOf(2,3)),
            Arguments.of(21, null, Cursor.from("2:2"), null, listOf(2,3)),
            Arguments.of(30, null, Cursor.from("2:2"), null, listOf(2,3)),
            Arguments.of(40, null, Cursor.from("2:2"), null, listOf(2,3,4)),
            Arguments.of(41, null, Cursor.from("2:2"), null, listOf(2,3,4)),

            Arguments.of(1, null, Cursor.from("1:1"), null, listOf(1)),
            Arguments.of(1, null, Cursor.from("1:2"), null, listOf(1)),
            Arguments.of(1, null, Cursor.from("1:19"), null, listOf(1)),
            Arguments.of(1, null, Cursor.from("1:20"), null, listOf(2)),
            Arguments.of(1, null, Cursor.from("2:10"), null, listOf(2)),
            Arguments.of(1, null, Cursor.from("2:20"), null, listOf(3)),
        )

        @JvmStatic
        fun testCursorAdd(): List<Arguments> = listOf(
            Arguments.of(Cursor.UNKNOWN, 1, Cursor.from("1:1")),
            Arguments.of(Cursor.UNKNOWN, 5, Cursor.from("1:5")),
            Arguments.of(Cursor.UNKNOWN, 10, Cursor.from("1:10")),
            Arguments.of(Cursor.UNKNOWN, 19, Cursor.from("1:19")),
            Arguments.of(Cursor.UNKNOWN, 20, Cursor.from("1:20")),
            Arguments.of(Cursor.UNKNOWN, 21, Cursor.from("2:1")),

            Arguments.of(Cursor.from("1:0"), 1, Cursor.from("1:1")),
            Arguments.of(Cursor.from("1:0"), 5, Cursor.from("1:5")),
            Arguments.of(Cursor.from("1:0"), 10, Cursor.from("1:10")),
            Arguments.of(Cursor.from("1:0"), 19, Cursor.from("1:19")),
            Arguments.of(Cursor.from("1:0"), 20, Cursor.from("1:20")),
            Arguments.of(Cursor.from("1:0"), 21, Cursor.from("2:1")),

            Arguments.of(Cursor.from("1:1"), 0, Cursor.from("1:1")),
            Arguments.of(Cursor.from("1:1"), 1, Cursor.from("1:2")),
            Arguments.of(Cursor.from("1:1"), 5, Cursor.from("1:6")),
            Arguments.of(Cursor.from("1:1"), 10, Cursor.from("1:11")),
            Arguments.of(Cursor.from("1:1"), 19, Cursor.from("1:20")),
            Arguments.of(Cursor.from("1:1"), 20, Cursor.from("2:1")),
            Arguments.of(Cursor.from("1:1"), 21, Cursor.from("2:2")),

            Arguments.of(Cursor.from("2:1"), 0, Cursor.from("2:1")),
            Arguments.of(Cursor.from("2:1"), 1, Cursor.from("2:2")),
            Arguments.of(Cursor.from("2:1"), 5, Cursor.from("2:6")),
            Arguments.of(Cursor.from("2:1"), 10, Cursor.from("2:11")),
            Arguments.of(Cursor.from("2:1"), 19, Cursor.from("2:20")),
            Arguments.of(Cursor.from("2:1"), 20, Cursor.from("3:1")),
            Arguments.of(Cursor.from("2:1"), 21, Cursor.from("3:2")),
        )

        @JvmStatic
        fun testCursorSubtract(): List<Arguments> = listOf(
            Arguments.of(Cursor.UNKNOWN, 1, Cursor.from("1:1")),
            Arguments.of(Cursor.UNKNOWN, 2, Cursor.from("1:1")),
            Arguments.of(Cursor.UNKNOWN, 10, Cursor.from("1:1")),
            Arguments.of(Cursor.UNKNOWN, 20, Cursor.from("1:1")),
            Arguments.of(Cursor.UNKNOWN, 21, Cursor.from("1:1")),

            Arguments.of(Cursor.from("1:2"), 1, Cursor.from("1:1")),
            Arguments.of(Cursor.from("1:20"), 1, Cursor.from("1:19")),
            Arguments.of(Cursor.from("2:1"), 1, Cursor.from("1:20")),

            Arguments.of(Cursor.from("2:1"), 20, Cursor.from("1:1")),
            Arguments.of(Cursor.from("2:3"), 21, Cursor.from("1:2")),
            Arguments.of(Cursor.from("2:3"), 22, Cursor.from("1:1")),
            Arguments.of(Cursor.from("2:3"), 31, Cursor.from("1:1")),
            Arguments.of(Cursor.from("2:20"), 19, Cursor.from("2:1")),
            Arguments.of(Cursor.from("2:20"), 20, Cursor.from("1:20")),
            Arguments.of(Cursor.from("2:20"), 21, Cursor.from("1:19")),

            Arguments.of(Cursor.from("3:20"), 41, Cursor.from("1:19")),
        )
    }

    @ParameterizedTest
    @MethodSource("initialiseErrorData")
    fun testInitialiseError(first: Int?, last: Int?, next: Cursor?, before: Cursor?) {
        val exception: Throwable = assertThrows<IllegalArgumentException> {
            RelayPageCalculator(
                first,
                last,
                next,
                before,
            )
        }
    }

    @ParameterizedTest
    @MethodSource("initialiseSuccessData")
    fun testInitialiseSuccess(first: Int?, last: Int?, next: Cursor?, before: Cursor?) {
        RelayPageCalculator(
            first,
            last,
            next,
            before,
        )
    }

    @ParameterizedTest(name = "{index} first :{0}: last :{1}: next :{2}: before :{3}:")
    @MethodSource("testListOfPagesData")
    fun testListOfPages(first: Int?, last: Int?, next: Cursor?, before: Cursor?, expected: List<Int>) {
        val calculator = RelayPageCalculator(
            first,
            last,
            next,
            before,
        )

        Assertions.assertEquals(expected, calculator.listOfPages)
    }

    @ParameterizedTest
    @MethodSource("testCursorAdd")
    fun testCursorAdd(cursor: Cursor, itemsToAdd: Int, expected: Cursor) {
        val result = cursor + itemsToAdd

        Assertions.assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("testCursorSubtract")
    fun testCursorSubtract(cursor: Cursor, itemsToAdd: Int, expected: Cursor) {
        val result = cursor - itemsToAdd

        Assertions.assertEquals(expected, result)
    }
}