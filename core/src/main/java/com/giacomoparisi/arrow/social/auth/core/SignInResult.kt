package com.giacomoparisi.arrow.social.auth.core

sealed class SignInResult

data class Completed(val user: SocialAuthUser) : SignInResult()

object Cancelled : SignInResult()