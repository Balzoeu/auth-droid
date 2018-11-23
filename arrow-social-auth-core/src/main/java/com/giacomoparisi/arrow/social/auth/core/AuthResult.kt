package com.giacomoparisi.arrow.social.auth.core

sealed class AuthResult<T> {

    data class Completed<T>(val value: T) : AuthResult<T>()
    class Cancelled<T> : AuthResult<T>()
    data class Failed<T>(val throwable: Throwable) : AuthResult<T>()
}

