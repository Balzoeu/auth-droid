package eu.balzo.authdroid.core

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

fun <E, A> Either<E, A?>.nullToError(error: E): Either<E, A> =
        flatMap { it?.right() ?: error.left() }

