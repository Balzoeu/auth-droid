package com.giacomoparisi.authdroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.facebook.FacebookActivity
import com.giacomoparisi.authdroid.firebase.FirebaseActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenuActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.main_menu)
        this.firebase.setOnClickListener {
            this.startActivity(Intent(this, FirebaseActivity::class.java))
        }
        this.facebook.setOnClickListener {
            this.startActivity(Intent(this, FacebookActivity::class.java))
        }
    }
}