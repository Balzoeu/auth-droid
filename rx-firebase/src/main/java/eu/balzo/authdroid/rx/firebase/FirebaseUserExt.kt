package eu.balzo.authdroid.rx.firebase

import com.google.firebase.auth.FirebaseUser
import eu.balzo.authdroid.core.SocialAuthUser

fun FirebaseUser.toSocialAuthUser(token: String): SocialAuthUser =
        SocialAuthUser(
                this.uid,
                token,
                null,
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
        when {
            this.email.isNullOrEmpty() -> this.email
            else -> this.providerData
                    .map { it.email }
                    .firstOrNull { it.isNullOrEmpty().not() }
        }
