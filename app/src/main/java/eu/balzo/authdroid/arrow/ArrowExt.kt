package eu.balzo.authdroid.arrow

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

fun startCoroutine(block: suspend () -> Unit): Unit =
        suspend { block() }.startCoroutine(Continuation(Dispatchers.IO) {})
