package com.dev.ecommerceuser.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.rest.ApiUtils
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_forgot_password.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var phone: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setUpViews()
    }
    private fun setUpViews() {
        edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(charSeq!!.length>7){
                    imgTick.visibility= View.VISIBLE

                }
                else{
                    imgTick.visibility= View.GONE
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        frag_other_backImg.setOnClickListener {
            frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            onBackPressed()
        }

        btnSend.setOnClickListener {
            btnSend.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnSend)
            validateAndForgot()
        }
    }
    private fun validateAndForgot() {
        phone = edtPhone.text.toString()

        if (TextUtils.isEmpty(phone)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)
//            LogUtils.shortToast(this@ForgotPasswordActivity, getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
//            LogUtils.shortToast(this@ForgotPasswordActivity, getString(R.string.mob_num_length_valid))
        }


        else {
            forgotPassword()
        }
    }

    private fun forgotPassword() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("mobile", "fcm_token", "device_type", "lang"),
            arrayOf(phone.trim({ it <= ' ' }),  SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.forgotPassword(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
                            val data = jsonObject.getJSONObject("data")
//                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            /*val bundle=Bundle()
                            bundle.putString("ref", "2")
                            bundle.putString("user_id", data.getInt("user_id").toString())
                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_otpVerificationFragment, bundle)*/
                            startActivity(
                                Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java).putExtra("ref", "2")
                                .putExtra("user_id", data.getInt("user_id").toString()))

                        }
                        else if (jsonObject.getInt("response") == 2){
                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
                            onBackPressed()

                        }
                        else {
                            LogUtils.shortToast(this@ForgotPasswordActivity, jsonObject.getString("message"))
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
                LogUtils.shortToast(this@ForgotPasswordActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
}