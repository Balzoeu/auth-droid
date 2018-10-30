package com.giacomoparisi.arrow.social.auth.core

sealed class AuthResult

data class Completed(val user: SocialAuthUser) : AuthResult()

object Cancelled : AuthResult()

data class Failed(val throwable: Throwable) : AuthResult()