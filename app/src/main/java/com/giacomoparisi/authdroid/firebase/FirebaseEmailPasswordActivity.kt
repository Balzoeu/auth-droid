package com.giacomoparisi.authdroid.firebase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.logError
import com.giacomoparisi.authdroid.openProfile
import com.giacomoparisi.authdroid.rx.firebase.signInWithFirebaseEmailPassword
import com.giacomoparisi.authdroid.rx.firebase.signUpWithFirebaseEmailPassword
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.email_password_auth.*

class FirebaseEmailPasswordActivity : FragmentActivity() {

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.email_password_auth)

        this.email_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@FirebaseEmailPasswordActivity.email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@FirebaseEmailPasswordActivity.password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.sign_up.setOnClickListener {
            signUpWithFirebaseEmailPassword(
                    email,
                    password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.sign_in.setOnClickListener {
            signInWithFirebaseEmailPassword(
                    email,
                    password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }
    }
}