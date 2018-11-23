package com.giacomoparisi.arrow.social.auth

import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.arrow.social.auth.core.*
import com.giacomoparisi.kotlin.functional.extensions.android.toast.showLongToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T> AuthResult<T>.showMessage(activity: FragmentActivity) {
    CoroutineScope(Dispatchers.Main).launch {
        when (this@showMessage) {
            is AuthResult.Cancelled -> activity.showLongToast("AuthCancelled")
            is AuthResult.Completed -> activity.showLongToast(this@showMessage.value.toString())
            is AuthResult.Failed -> activity.showLongToast(this@showMessage.throwable.message.orEmpty())
        }
    }
}