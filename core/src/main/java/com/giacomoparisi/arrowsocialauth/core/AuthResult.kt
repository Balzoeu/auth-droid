package com.giacomoparisi.arrowsocialauth.core

sealed class SignInResult

data class Completed(val user: SocialAuthUser) : SignInResult()

data class Failed(val exception: Exception) : SignInResult()

object Cancelled : SignInResult()