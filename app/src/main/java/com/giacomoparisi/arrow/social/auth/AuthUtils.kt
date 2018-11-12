package com.giacomoparisi.arrow.social.auth

import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.AuthCancelled
import com.giacomoparisi.arrow.social.auth.core.AuthCompleted
import com.giacomoparisi.arrow.social.auth.core.AuthFailed
import com.giacomoparisi.kotlin.functional.extensions.android.toast.showLongToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun AuthResult.showMessage(activity: FragmentActivity) {
    CoroutineScope(Dispatchers.Main).launch {
        when (this@showMessage) {
            is AuthCancelled -> activity.showLongToast("AuthCancelled")
            is AuthCompleted -> activity.showLongToast(this@showMessage.user.toString())
            is AuthFailed -> activity.showLongToast(this@showMessage.throwable.message.orEmpty())
        }
    }
}