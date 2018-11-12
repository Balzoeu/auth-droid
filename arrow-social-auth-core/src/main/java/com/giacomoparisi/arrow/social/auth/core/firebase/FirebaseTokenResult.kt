package com.giacomoparisi.arrow.social.auth.core.firebase

sealed class FirebaseTokenResult

data class FirebaseTokenRequestCompleted(val token: String) : FirebaseTokenResult()

object FirebaseTokenRequestCancelled : FirebaseTokenResult()

data class FirebaseTokenRequestFailed(val throwable: Throwable) : FirebaseTokenResult()