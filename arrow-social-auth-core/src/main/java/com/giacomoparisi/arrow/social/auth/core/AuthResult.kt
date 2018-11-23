package com.giacomoparisi.arrow.social.auth.core

sealed class AuthResult {

    data class Completed<T>(val value: T) : AuthResult()
    object Cancelled : AuthResult()
    data class Failed(val throwable: Throwable) : AuthResult()
}

