package eu.balzo.authdroid.rx.google

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.rx.google.authWithGoogle
import eu.balzo.authdroid.rx.google.googleSignOut
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.google.*

class GoogleRxActivity : FragmentActivity(R.layout.google) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        google.setOnClickListener {
            authWithGoogle(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        google_logout.setOnClickListener {
            googleSignOut(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { Toast.makeText(this, "Done", Toast.LENGTH_LONG).show() })
                    { Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() }
        }
    }
}