package com.giacomoparisi.arrowsocialauth.core

import com.giacomoparisi.arrowsocialauth.core.firebase.FirebaseGoogleAuthFragment

sealed class AuthMethod(start: () -> AuthFragment)

data class FirebaseGoogleAuth(private val _clientId: String)
    : AuthMethod({ FirebaseGoogleAuthFragment.build(_clientId) })