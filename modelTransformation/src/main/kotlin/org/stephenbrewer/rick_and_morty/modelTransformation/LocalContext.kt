package org.stephenbrewer.rick_and_morty.modelTransformation

data class LocalContext (
    val characters: Map<Int, List<Int>> = emptyMap(),
    val episodes: Map<Int, List<Int>> = emptyMap(),
    val origins: Map<Int, Int> = emptyMap(),
    val locations: Map<Int, Int> = emptyMap(),
) {
    companion object {
        val EMPTY = LocalContext()
    }
}

fun <T>List<PagedResultAndLocalContext<List<T>, T>>.extractLocalContext(): LocalContext {
    val characters = mutableMapOf<Int, List<Int>>()
    val episodes = mutableMapOf<Int, List<Int>>()
    val origins = mutableMapOf<Int, Int>()
    val locations = mutableMapOf<Int, Int>()

    for (page in this) {
        characters.putAll(page.localContext?.characters.orEmpty())
        episodes.putAll(page.localContext?.episodes.orEmpty())
        origins.putAll(page.localContext?.origins.orEmpty())
        locations.putAll(page.localContext?.locations.orEmpty())
    }
    return LocalContext(
        characters = characters,
        episodes = episodes,
        origins = origins,
        locations = locations,
    )
}
