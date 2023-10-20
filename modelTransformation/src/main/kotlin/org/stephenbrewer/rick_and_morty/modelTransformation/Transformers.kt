package org.stephenbrewer.rick_and_morty.modelTransformation

import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Character
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Episode
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Gender
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Location
import org.stephenbrewer.rick_and_morty.modelTransformation.generated.types.Status

fun Character.Companion.from(rest: org.stephenbrewer.rick_and_morty.provider.generated.models.Character): Character {
    return Character(
        rest.id,
        rest.name,
        Status.from(rest.status),
        rest.species,
        rest.type,
        Gender.from(rest.gender),
        null,
        null,
        rest.image,
        null,
        rest.created,
    )
}

fun Episode.Companion.from(rest: org.stephenbrewer.rick_and_morty.provider.generated.models.Episode): Episode {
    return Episode(
        rest.id,
        rest.name,
        rest.airDate,
        rest.episode,
        null,
        rest.created,
    )
}
fun Location.Companion.from(rest: org.stephenbrewer.rick_and_morty.provider.generated.models.Location): Location {
    return Location(
        rest.id,
        rest.name,
        rest.type,
        rest.dimension,
        null,
        rest.created,
    )
}

fun Status.Companion.from(restStatus: org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Status?): Status? {
    return when (restStatus) {
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Status.ALIVE -> Status.ALIVE
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Status.DEAD -> Status.DEAD
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Status.UNKNOWN -> Status.UNKNOWN
        null -> null
    }
}

fun Gender.Companion.from(restStatus: org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Gender?): Gender? {
    return when (restStatus) {
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Gender.MALE -> Gender.MALE
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Gender.FEMALE -> Gender.FEMALE
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Gender.GENDERLESS -> Gender.GENDERLESS
        org.stephenbrewer.rick_and_morty.provider.generated.models.Character.Gender.UNKNOWN -> Gender.UNKNOWN
        null -> null
    }
}
