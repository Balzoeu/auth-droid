package com.giacomoparisi.arrow.social.auth.core

import arrow.core.Option
import arrow.core.toOption
import arrow.syntax.collections.firstOption
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.getOrEmpty
import com.giacomoparisi.kotlin.functional.extensions.core.fold
import com.google.firebase.auth.FirebaseUser

data class SocialAuthUser(
        val uId: String,
        val displayName: Option<String>,
        val firstName: Option<String>,
        val lastName: Option<String>,
        val email: Option<String>,
        val profileImage: Option<String>
) {
    override fun toString(): String =
            "display name: ${displayName.getOrEmpty()}, " +
                    "first name: ${firstName.getOrEmpty()}, " +
                    "last name: ${lastName.getOrEmpty()}, " +
                    "email: ${email.getOrEmpty()}, " +
                    "profileImage: ${profileImage.getOrEmpty()}"
}

fun FirebaseUser.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                this.uid,
                this.displayName.toOption(),
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(0)
                        .toOption(),
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(1)
                        .toOption(),
                this.getProviderEmail(),
                this.photoUrl.toOption().map { it.toString() })

private fun FirebaseUser.getProviderEmail() =
        this.email.isNullOrEmpty()
                .fold({ this.email.toOption() }) {
                    this.providerData.firstOption { it.email.isNullOrEmpty().not() }.flatMap { it.email.toOption() }
                }