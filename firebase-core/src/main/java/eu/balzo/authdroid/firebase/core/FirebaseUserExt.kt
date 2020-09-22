package eu.balzo.authdroid.firebase.core

import com.google.firebase.auth.FirebaseUser
import eu.balzo.authdroid.core.SocialAuthUser

fun FirebaseUser.toSocialAuthUser(token: String): SocialAuthUser =
        SocialAuthUser(
                uid,
                token,
                null,
                displayName?.emptyOrBlankToNull(),
                displayName
                        ?.split(" ")
                        ?.getOrNull(0)
                        ?.emptyOrBlankToNull(),
                displayName
                        ?.split(" ")
                        ?.getOrNull(1),
                getProviderEmail(),
                photoUrl?.toString()?.emptyOrBlankToNull()
        )

private fun FirebaseUser.getProviderEmail(): String? =
        email?.emptyOrBlankToNull() ?: providerData.map { it.email }
                .mapNotNull { it?.emptyOrBlankToNull() }
                .firstOrNull()

private fun String.emptyOrBlankToNull(): String? =
        if (isEmpty() || isBlank()) null else this
