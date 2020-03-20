package eu.balzo.authdroid.google.core

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import eu.balzo.authdroid.core.SocialAuthUser

fun GoogleSignInAccount.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                id.orEmpty(),
                idToken.orEmpty(),
                displayName,
                displayName?.split(" ")?.getOrNull(0),
                displayName?.split(" ")?.getOrNull(1),
                email,
                photoUrl?.toString()
        )