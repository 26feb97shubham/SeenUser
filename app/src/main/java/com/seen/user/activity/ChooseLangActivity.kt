package com.seen.user.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seen.user.R
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_choose_lang.*

class ChooseLangActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    private var selectLang:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_lang)
        setUpViews()
    }
    private fun setUpViews() {

        if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("en")){
            selectEnglish()

        }
        else if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
            selectArabic()
        }

        arabicView.setOnClickListener {
          /*  if(selectLang != "ar") {
                arabicView.startAnimation(AlphaAnimation(1f, 0.5f))
                selectArabic()
            }*/
            arabicView.startAnimation(AlphaAnimation(1f, 0.5f))
            selectArabic()
        }
        englishView.setOnClickListener {
            /*if(selectLang != "en") {
                englishView.startAnimation(AlphaAnimation(1f, 0.5f))
                selectEnglish()
            }*/
            englishView.startAnimation(AlphaAnimation(1f, 0.5f))
            selectEnglish()
        }

        btnNext.setOnClickListener {
            btnNext.startAnimation(AlphaAnimation(1f, 0.5f))
            if(TextUtils.isEmpty(selectLang)){
                LogUtils.shortToast(this, getString(R.string.please_choose_your_language))
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isLangSelected, false)
            }
            else{
                Utility.changeLanguage(this, selectLang)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SelectedLang, selectLang)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isLangSelected, true)
              /*  startActivity(Intent(this, ChooseLoginSignUpActivity::class.java))
                finish()*/
                Log.e("isSelected", ""+SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isSelected,""))
                if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isSelected,"").equals("Login")){
                    Log.e("isSelected", ""+SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isSelected,""))
                    startActivity(Intent(this, LoginActivity::class.java))
                } else if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isSelected,"").equals("Create Account")){
                    Log.e("isSelected", ""+SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isSelected,""))
                    startActivity(Intent(this, SignUpActivity::class.java))
                }else{
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
              /*  startActivity(Intent(this, HomeActivity::class.java))*/
//                finishAffinity()
            }

        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
//            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }*/


    }

    private fun selectArabic() {
        selectLang = "ar"
        imgTick1.visibility = View.VISIBLE
        imgTick2.visibility = View.GONE
    }

    private fun selectEnglish() {
        selectLang = "en"
        imgTick2.visibility = View.VISIBLE
        imgTick1.visibility = View.GONE
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
