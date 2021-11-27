package com.dev.ecommerceuser.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.rest.ApiUtils
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_otp_verification.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OtpVerificationActivity : AppCompatActivity() {
    lateinit var ref: String
    lateinit var pin: String
    lateinit var user_id: String
    var doubleClick:Boolean=false
    var isLoggedIn : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        setUpViews()
    }
    private fun setUpViews() {
        if(intent.extras != null){
            user_id = intent.extras!!.getString("user_id").toString()
            ref = intent.extras!!.getString("ref").toString()
        }
        
        btnVerify.isEnabled=false
        btnVerify.alpha=0.5f

        firstPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) =
                if(charSeq!!.length==4){
                    btnVerify.isEnabled=true
                    btnVerify.alpha=1f
                    SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this@OtpVerificationActivity, firstPinView)

                }
                else{
                    btnVerify.isEnabled=false
                    btnVerify.alpha=0.5f
                }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        btnVerify.setOnClickListener {
            btnVerify.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndVerification()

        }

        resend.setOnClickListener {
            resend.startAnimation(AlphaAnimation(1f, 0.5f))
            firstPinView.setText("")
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this@OtpVerificationActivity, it)
            resendOtp()


        }


    }
    private fun validateAndVerification() {
        pin = firstPinView.text.toString()
        verifyAccount()
        /*  if (TextUtils.isEmpty(pin)) {
              firstPinView.error=getString(R.string.please_enter_your_otp)
  //            LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.please_enter_your_otp))
  
          }
          else if ((pin.length < 4)) {
              firstPinView.error=getString(R.string.otp_length_valid)
  //            LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.otp_length_valid))
          }
  
          else {
              verifyAccount()
          }*/

    }

    private fun verifyAccount() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "otp", "fcm_token", "device_type", "lang"),
            arrayOf(user_id, pin.trim({ it <= ' ' }),  SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.verifyAccount(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
//                            LogUtils.shortToast(this@OtpVerificationActivity, jsonObject.getString("message"))
                            if(ref=="1"){
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id.toInt())
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                startActivity(Intent(this@OtpVerificationActivity, HomeActivity::class.java))

                            }
                            else{
                                startActivity(Intent(this@OtpVerificationActivity, ResetPasswordActivity::class.java)
                                    .putExtra("user_id", user_id))
                               /* val bundle=Bundle()
                                bundle.putString("user_id", user_id)
                                findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment, bundle)*/
                            }
                            /*val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("id"))
                            val bundle=Bundle()
                            bundle.putString("ref", "2")
                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_otpVerificationFragment, bundle)*/

                        }
                        else {
                            LogUtils.shortToast(this@OtpVerificationActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    private fun resendOtp() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "fcm_token", "device_type", "lang"),
            arrayOf(user_id, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.resendOtp(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(this@OtpVerificationActivity, jsonObject.getString("message"))

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
                LogUtils.shortToast(this@OtpVerificationActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

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