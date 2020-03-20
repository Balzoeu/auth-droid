package eu.balzo.authdroid.arrow

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe

fun <A> Kind<ForIO, A>.unsafeRunAsync(): Unit =
        unsafe {
            runNonBlocking({ this@unsafeRunAsync }) {}
        }