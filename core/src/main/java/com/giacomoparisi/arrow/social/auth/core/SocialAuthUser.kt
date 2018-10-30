package com.giacomoparisi.arrow.social.auth.core

import arrow.core.Option
import arrow.core.toOption
import com.google.firebase.auth.FirebaseUser

data class SocialAuthUser(
        val name: Option<String>,
        val email: Option<String>,
        val profileImage: Option<String>
)

fun FirebaseUser.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                this.displayName.toOption(),
                this.email.toOption(),
                this.photoUrl.toOption().map { it.toString() }
        )