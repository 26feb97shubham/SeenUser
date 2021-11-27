package com.dev.ecommerceuser.utils

import android.content.Context
import android.content.res.Configuration
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import java.util.*

class Utility {
    companion object{
        var showPass = false
        fun changeLanguage(context:Context, language:String){
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }

        fun showPassword(iv_pass_show_hide_login_verify: ImageView, editText : EditText) {
            if (showPass){
                showPass = false
                editText.transformationMethod = null
                iv_pass_show_hide_login_verify.setImageResource(R.drawable.visible)
            }else{
                showPass = true
                editText.transformationMethod = PasswordTransformationMethod()
                iv_pass_show_hide_login_verify.setImageResource(R.drawable.invisible)
            }
        }
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
    }
}