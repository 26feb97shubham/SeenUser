package com.seen.user.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.annotation.RequiresApi
import com.seen.user.R
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.rest.ApiUtils
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import com.seen.user.utils.Utility.Companion.showPassword
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.frag_other_backImg
import kotlinx.android.synthetic.main.activity_sign_up2.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class SignUp2Activity : AppCompatActivity() {
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var selectCountryCode = ""
    var isChecked: Boolean=false
    var imagePath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.changeLanguage(
            this,
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setContentView(R.layout.activity_sign_up2)
        if (intent!=null){
            var bundle = Bundle()
            bundle = intent.extras!!
            name = bundle.getString("userName")!!
            email = bundle.getString("email")!!
            phone = bundle.getString("mobile")!!
            selectCountryCode = bundle.getString("country_code")!!
            imagePath = bundle.getString("imagePath")!!
        }
        setUpViews()
    }

    private fun setUpViews(){
        frag_other_backImg1.setOnClickListener {
            frag_other_backImg1.startAnimation(AlphaAnimation(1f, 0.5f))
            finish()
        }


        iv_pass_show_hide_login.setOnClickListener {
            showPassword(iv_pass_show_hide_login, edtPassword)
        }

        iv_pass_show_hide_login_verify.setOnClickListener {
            showPassword(iv_pass_show_hide_login_verify, edtCnfrmPassword)
        }

        imgChk.setOnClickListener {
            imgChk.startAnimation(AlphaAnimation(1f, 0.5f))
            if(isChecked){
                isChecked=false
                imgChk.setImageResource(R.drawable.un_check)
            }
            else{
                isChecked=true
                imgChk.setImageResource(R.drawable.check)
            }
        }

        scrollView2.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                  edtPassword.clearFocus()
                edtCnfrmPassword.clearFocus()
                return false
            }

        })

        edtCnfrmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
         val pass =edtPassword.text.toString()

         if(!TextUtils.isEmpty(pass)){
             if(!pass.equals(charSeq.toString(), false)){
                 edtCnfrmPassword.error=getString(R.string.password_doesnt_match_with_verify_password)
             }
         }
         else{
            edtPassword.error=getString(R.string.please_first_enter_your_password)
         }
        }

        override fun afterTextChanged(p0: Editable?) {}
        })

        btnSignUp.setOnClickListener {
            validateAndProceed()
        }



        val word = SpannableString(resources.getString(R.string.i_accept) + " ")
        word.setSpan(ForegroundColorSpan(Color.GRAY), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtPlsAccept2!!.text = word

        val termsCondSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(this@SignUp2Activity, TermsAndConditionsActivity::class.java).putExtra("title", getString(R.string.terms_amp_conditions)))
            }
            @RequiresApi(Build.VERSION_CODES.M)
            override fun updateDrawState(drawState: TextPaint) {
                super.updateDrawState(drawState)
                drawState.isUnderlineText = true
                drawState.color = getColor(R.color.txt_dark_gray)
            }
        }

        val wordTwo = SpannableString(resources.getString(R.string.terms_amp_conditions))
        wordTwo.setSpan(termsCondSpan, 0, wordTwo.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtPlsAccept2!!.append(wordTwo)
        txtPlsAccept2!!.movementMethod = LinkMovementMethod.getInstance()

        txtPlsAccept2!!.setOnLongClickListener {
            Log.e("tag", "To stop crash on Long press")
            true
        }

    }



    private fun validateAndProceed() {
        password= edtPassword.text.toString().trim()
        confirmPassword= edtCnfrmPassword.text.toString().trim()

        if (TextUtils.isEmpty(password)){
            edtPassword.requestFocus()
            scrollView2.scrollTo(0, 240)
            edtPassword.error=getString(R.string.please_enter_your_password)
        }else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            edtPassword.requestFocus()
            scrollView2.scrollTo(0, 240)
            edtPassword.error=getString(R.string.password_length_valid)
        }else if (TextUtils.isEmpty(confirmPassword)) {
            scrollView2.scrollTo(0, 270)
            edtCnfrmPassword.requestFocus()
            edtCnfrmPassword.error=getString(R.string.please_verify_your_password)
        }else if (!confirmPassword.equals(password)) {
            scrollView2.scrollTo(0, 270)
            edtCnfrmPassword.requestFocus()
            edtCnfrmPassword.error=getString(R.string.password_doesnt_match_with_verify_password)
        } else if(!isChecked){
            LogUtils.shortToast(this, getString(R.string.please_accept_terms_conditions))
        }else{
            getSignUp()
        }
    }

    private fun getSignUp() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar_signup2.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("email", "password", "fcm_token", "device_type", "device_id", "name", "mobile", "country_code", "lang"),
                arrayOf(email.trim({ it <= ' ' }), password.trim({ it <= ' ' }),
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""], ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], name.trim { it <= ' ' }, phone.trim({ it <= ' ' }), selectCountryCode, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            builder!!.addFormDataPart("profile_picture", file.name, requestBody)
        }


        val call = apiInterface.signUp(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar_signup2.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
//                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            /* val bundle = Bundle()
                             bundle.putString("ref", "1")
                             bundle.putString("user_id", data.getInt("user_id").toString())
                             findNavController().navigate(R.id.action_signUpFragment_to_otpVerificationFragment, bundle)*/
                            val bundle = Bundle()
                            bundle.putString("ref", "1")
                            bundle.putString("user_id", data.getInt("user_id").toString())
                            startActivity(Intent(this@SignUp2Activity, OtpVerificationActivity::class.java).putExtras(bundle))
                            finish()

                        }
                        /*else if (jsonObject.getInt("response") == 2){
                            *//*val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("id"))*//*
                            val bundle=Bundle()
                            bundle.putString("ref", "1")
                            findNavController().navigate(R.id.action_signUpFragment_to_otpVerificationFragment, bundle)

                        } */
                        else {
                            LogUtils.shortToast(this@SignUp2Activity, jsonObject.getString("message"))
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(this@SignUp2Activity, getString(R.string.check_internet))
                progressBar_signup2.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
}