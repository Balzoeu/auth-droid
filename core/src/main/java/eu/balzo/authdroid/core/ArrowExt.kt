package eu.balzo.authdroid.core

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.flatMap
import arrow.core.left
import arrow.fx.typeclasses.ConcurrentFx

fun <F, E, A> Either<E, Kind<F, Either<E, A>>>.accumulateError(
        fx: ConcurrentFx<F>
): Kind<F, Either<E, A>> =
        fx.concurrent {

            !this@accumulateError.fold({ just(it.left()) }, { it })

        }

fun <E, A> Either<E, Option<A>>.noneToError(error: E): Either<E, A> =
        flatMap { it.toEither { error } }

