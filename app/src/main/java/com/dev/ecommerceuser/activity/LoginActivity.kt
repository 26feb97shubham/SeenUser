package com.dev.ecommerceuser.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.rest.ApiUtils
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.dev.ecommerceuser.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up2.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.side_menu_layout.*
import kotlinx.android.synthetic.main.side_top_view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    var remembered:Boolean=false
    var phone: String = ""
    var password: String = ""
    var spannableString : SpannableString?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        spannableString = SpannableString("SIGNUP")
        spannableString!!.setSpan(UnderlineSpan(), 0, spannableString!!.length, 0)
        tv_signup.setText(spannableString)
        setUpViews()
    }
    private fun setUpViews() {

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsRemembered, false]){
            remembered=true
//            chkRememberMe.isChecked=true
            phone=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Phone, ""]
            password=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Password, ""]
            edtPhone.setText(phone)
            edtPass.setText(password)
        }

        iv_pass_show_hide_login_screen.setOnClickListener {
            Utility.showPassword(iv_pass_show_hide_login_screen, edtPass)
        }

        chkRememberMe.setOnClickListener {
            /*if(remembered){
                remembered=false
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.un_check, 0, 0, 0)
            }
            else{
                remembered=true
                chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
            }*/

            if(remembered){
                remembered=false
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                    chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ellipse, 0, 0, 0)
                }
                else{
                    chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ellipse, 0)
                }

            }
            else{
                remembered=true
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en") {
                    chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
                }
                else{
                    chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
                }
            }
        }

/*       backImg.setOnClickListener {
           backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            onBackPressed()
        }*/

       txtForgotPass.setOnClickListener {
            txtForgotPass.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }


       btnLogin.setOnClickListener {
            btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
           SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnLogin)
//            startActivity(Intent(this, HomeActivity::class.java))
            validateAndLogin()

            /* val navOptions = NavOptions.Builder().setPopUpTo(R.id.my_nav_graph, true).build()
             findNavController().navigate(R.id.action_loginFragment_to_homeFragment, null, navOptions)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 window.setDecorFitsSystemWindows(true)
             } else {
 //                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                 window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
             }*/
        }

        tv_signup.setOnClickListener {
            tv_signup.startAnimation(AlphaAnimation(1f, 0.5f))
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }


    private fun validateAndLogin() {
        phone = edtPhone.text.toString()
        password= edtPass.text.toString()


        if (TextUtils.isEmpty(phone)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.mob_num_length_valid))
        }

        else if (TextUtils.isEmpty(password)) {
            edtPass.requestFocus()
            edtPass.error=getString(R.string.please_enter_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            edtPass.requestFocus()
            edtPass.error=getString(R.string.invalid_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }

        else {
            if(remembered){
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, true)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, phone)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, password)
            }
            else{
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, false)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, "")
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, "")
            }
            getLogin()
        }
    }

    private fun getLogin() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("password", "fcm_token", "device_type","device_id", "mobile", "lang"),
            arrayOf(password.trim({ it <= ' ' }),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], phone.trim({ it <= ' ' }), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.login(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.ProfilePic, data.getString("profile_picture"))
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserName, data.getString("name"))
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserEmail, data.getString("email"))
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))

                        }
                        else if (jsonObject.getInt("response") == 2){
                            val data = jsonObject.getJSONObject("data")
                            LogUtils.shortToast(this@LoginActivity, jsonObject.getString("message"))
                            val bundle=Bundle()
                            bundle.putString("ref", "1")
                            bundle.putString("user_id", data.getInt("user_id").toString())

//                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
//                            findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment, bundle)
                            startActivity(Intent(this@LoginActivity, OtpVerificationActivity::class.java).putExtra("ref", "1")
                                .putExtra("user_id", data.getInt("user_id").toString()))

                        }
                        else {
                            LogUtils.shortToast(this@LoginActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@LoginActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

}