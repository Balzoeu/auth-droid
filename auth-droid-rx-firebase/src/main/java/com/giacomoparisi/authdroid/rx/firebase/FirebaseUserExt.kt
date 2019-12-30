package com.giacomoparisi.authdroid.rx.firebase

import com.giacomoparisi.authdroid.core.SocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.core.fold
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                this.uid,
                this.displayName,
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(0),
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(1),
                this.getProviderEmail(),
                this.photoUrl?.toString())

private fun FirebaseUser.getProviderEmail() =
        this.email.isNullOrEmpty()
                .fold(
                        { this.email },
                        {
                            this.providerData
                                    .map { it.email }
                                    .firstOrNull { it.isNullOrEmpty().not() }
                        })