package com.giacomoparisi.arrow.social.auth.core

sealed class AuthResult

data class AuthCompleted(val user: SocialAuthUser) : AuthResult()

object AuthCancelled : AuthResult()

data class AuthFailed(val throwable: Throwable) : AuthResult()