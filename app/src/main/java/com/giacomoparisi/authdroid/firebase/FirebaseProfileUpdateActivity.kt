package com.giacomoparisi.authdroid.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.logError
import com.giacomoparisi.authdroid.rx.firebase.updateFirebaseProfile
import com.giacomoparisi.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase_password_update.update
import kotlinx.android.synthetic.main.firebase_profile_update.*

class FirebaseProfileUpdateActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.setContentView(R.layout.firebase_profile_update)

        this.update.setOnClickListener {
            updateFirebaseProfile(this.name_field.text.toString(), this.photo_field.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ this.showToast("Done") }) { it.logError(this) }
        }
    }
}