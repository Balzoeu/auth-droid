package com.giacomoparisi.authdroid

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.rx.facebook.authWithFacebook
import com.giacomoparisi.authdroid.rx.facebook.facebookSignOut
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.facebook.*

class FacebookActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.setContentView(R.layout.facebook)

        this.facebook.setOnClickListener {
            authWithFacebook(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.facebook_logout.setOnClickListener {
            facebookSignOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}