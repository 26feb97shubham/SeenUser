package com.seen.user.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Secure
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.seen.user.R
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.changeLanguage(
            this,
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setContentView(R.layout.activity_splash)
        getFCMToken()
        deviceId()
        setUpViews()

    }

    private fun setUpViews() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsWelcomeShow, false]) {
                if (!TextUtils.isEmpty(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""])) {
                    Utility.changeLanguage(this, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""])
                  /*  startActivity(Intent(this, HomeActivity::class.java))
                    finishAffinity()*/
                    if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsLogin, false]) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finishAffinity()
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finishAffinity()
                    }
                } else {
                    startActivity(Intent(this, ChooseLangActivity::class.java))
                    finishAffinity()
                }


            }
            else{
                startActivity(Intent(this, IntroductionActivity::class.java))
                finishAffinity()
            }
        },2000)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("getInstanceId", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    val fcmToken = task.result
                    Log.e("getInstanceId", fcmToken)
                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.FCMTOKEN,fcmToken.toString())

                })

    }
    private fun deviceId(){
        val deviceId = Secure.getString(this.contentResolver, Secure.ANDROID_ID)
        Log.e("deviceId", deviceId)
        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.DeviceId,deviceId.toString())
    }

}