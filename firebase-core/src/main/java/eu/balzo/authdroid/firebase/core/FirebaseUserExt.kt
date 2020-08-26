package eu.balzo.authdroid.firebase.core

import arrow.core.*
import com.google.firebase.auth.FirebaseUser
import eu.balzo.authdroid.core.SocialAuthUser

fun FirebaseUser.toSocialAuthUser(token: String): SocialAuthUser =
        SocialAuthUser(
                uid,
                token,
                None,
                displayName.emptyOrBlankToNone(),
                displayName
                        ?.split(" ")
                        ?.getOrNull(0)
                        .emptyOrBlankToNone(),
                displayName
                        ?.split(" ")
                        ?.getOrNull(1)
                        .toOption(),
                getProviderEmail(),
                photoUrl?.toString().emptyOrBlankToNone()
        )

private fun FirebaseUser.getProviderEmail(): Option<String> =
        email.emptyOrBlankToNone()
                .fold(
                        {
                            providerData
                                    .map { it.email }
                                    .mapNotNull { it.emptyOrBlankToNone().orNull() }
                                    .firstOrNone()
                        },
                        { it.some() }
                )

private fun String?.emptyOrBlankToNone(): Option<String> =
        toOption().flatMap { if (it.isEmpty() || it.isBlank()) None else it.some() }
