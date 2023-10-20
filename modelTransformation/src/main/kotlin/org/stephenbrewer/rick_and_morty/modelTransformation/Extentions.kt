package org.stephenbrewer.rick_and_morty.modelTransformation


fun <T, R>T.mapOrEmpty(code: (it: T) -> Pair<Int, R?>?) = buildMap<Int, R> {
    val a = code(this@mapOrEmpty)
    val b = a?.second
    if (b != null) {
        this@buildMap.put(a.first, b)
    }
}
fun <T, R>List<T>.mapOrEmpty(code: (it: T) -> Pair<Int, R?>?) = buildMap<Int, R> {
    this@mapOrEmpty.forEach {
        val a = code(it)
        val b = a?.second
        if (b != null) {
            this@buildMap.put(a.first, b)
        }
    }
}

fun <T>List<T>.ensureMoreThanOne(): List<T> {
    return when {
        size > 1 -> this
        else -> listOf(this[0], this[0])
    }
}
