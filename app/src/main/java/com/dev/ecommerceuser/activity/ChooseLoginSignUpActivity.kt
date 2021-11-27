package com.dev.ecommerceuser.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.ecommerceuser.R
import kotlinx.android.synthetic.main.activity_choose_login_sign_up.*

class ChooseLoginSignUpActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_sign_up)
        setUpViews()
    }
    private fun setUpViews() {

       btnLogin.setOnClickListener {
           btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
           startActivity(Intent(this, LoginActivity::class.java))
        }
       btnSignUp.setOnClickListener {
           btnSignUp.startAnimation(AlphaAnimation(1f, 0.5f))
           startActivity(Intent(this, SignUpActivity::class.java))

        }

    }

    override fun onBackPressed() {
        exitApp()
    }
    private fun exitApp() {
        val toast = Toast.makeText(
            this,
            getString(R.string.please_click_back_again_to_exist),
            Toast.LENGTH_SHORT
        )


        if(doubleClick){
            finishAffinity()
            doubleClick=false
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 500)
        }
    }
}