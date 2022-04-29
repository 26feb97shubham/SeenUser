package com.seen.user.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import com.seen.user.R
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import java.util.*


class Utility {
    companion object{
        var showPass = false
        var total_discount = 0.0
        var payment_flag = false
        var price_category = ""
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

    fun intentIntegration(myContext: Context, intentUri: String?, packageName: String) {
        // Make sure the Skype for Android client is installed.
        if (!isIntentAppInstalled(myContext, packageName)) {
            goToMarket(myContext, packageName)
            return
        }
        val uri: Uri = Uri.parse(intentUri)
        val myIntent = Intent(Intent.ACTION_VIEW, uri)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myContext.startActivity(myIntent)
    }


    private fun isIntentAppInstalled(myContext: Context, myPackage: String): Boolean {
        val myPackageMgr = myContext.packageManager
        try {
            myPackageMgr.getPackageInfo(myPackage, PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    private fun goToMarket(myContext: Context, packageMName: String) {
        val marketUri: Uri = Uri.parse("market://details?id=$packageMName")
        val myIntent = Intent(Intent.ACTION_VIEW, marketUri)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myContext.startActivity(myIntent)
    }
}