package com.seen.user.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.rest.ApiUtils
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_otp_verification.view.*
import kotlinx.android.synthetic.main.side_menu_layout.*
import kotlinx.android.synthetic.main.side_top_view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OtpVerificationFragment : Fragment() {
    var mView: View?=null
    lateinit var ref: String
    lateinit var reference: String
    lateinit var pin: String
    lateinit var user_id: String
    lateinit var name: String
    lateinit var email: String
    lateinit var profile_picture: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ref = it.getString("ref").toString()
            user_id = it.getString("user_id").toString()
            reference = it.getString("reference", "")
            if(ref=="2"){
                name= it.getString("name", "")
                email= it.getString("email", "")
                profile_picture= it.getString("profile_picture", "")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if(mView == null){
            mView = inflater.inflate(R.layout.fragment_otp_verification, container, false)
            Utility.changeLanguage(
                requireContext(),
                SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
            )
            setUpViews()
        }
        
        return mView
    }

    private fun setUpViews() {
        mView!!.btnVerify.isEnabled=false
       mView!!.btnVerify.alpha=0.5f

        mView!!.firstPinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) =
                    if(charSeq!!.length==4){
                       mView!!.btnVerify.isEnabled=true
                       mView!!.btnVerify.alpha=1f
                        SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.firstPinView)

                    }
                    else{
                       mView!!.btnVerify.isEnabled=false
                       mView!!.btnVerify.alpha=0.5f
                    }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
       mView!!.btnVerify.setOnClickListener {
           mView!!.btnVerify.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndVerification()

        }

        mView!!.resend.setOnClickListener {
            mView!!.resend.startAnimation(AlphaAnimation(1f, 0.5f))
            mView!!.firstPinView.setText("")
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), it)
           resendOtp()


        }


    }
    private fun validateAndVerification() {
        pin = mView!!.firstPinView.text.toString()
        verifyAccount()
        /*  if (TextUtils.isEmpty(pin)) {
              mView!!.firstPinView.error=getString(R.string.please_enter_your_otp)
  //            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_otp))
  
          }
          else if ((pin.length < 4)) {
              mView!!.firstPinView.error=getString(R.string.otp_length_valid)
  //            LogUtils.shortToast(requireContext(), getString(R.string.otp_length_valid))
          }
  
          else {
              verifyAccount()
          }*/

    }

    private fun verifyAccount() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "otp", "fcm_token", "device_type", "lang"),
                arrayOf(user_id, pin.trim({ it <= ' ' }),  SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                        , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.verifyAccount(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            if(ref=="1"){
                                findNavController().navigate(R.id.action_otpVerificationFragment_to_loginFragment)

                            }
                            else if(ref=="2"){
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id.toInt())
                                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                                setBottomView()
                                requireActivity().llLogout.visibility=View.VISIBLE
                                requireActivity().logoutView.visibility=View.VISIBLE
                                requireActivity().name.setTextColor(
                                        ContextCompat.getColor(
                                                requireContext(),
                                                R.color.black
                                        )
                                )
                                requireActivity().name.text = name
                                requireActivity().email.text = email
                                Glide.with(requireContext()).load(profile_picture).placeholder(R.drawable.user).into(requireActivity().userIcon)
//                                startActivity(Intent(requireContext(), HomeActivity::class.java))
                                if(reference=="Profile"){
                                    findNavController().navigate(R.id.profileFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    //requireActivity().itemProfile.setImageResource(R.drawable.user_profile_active)
                                }

                                else if(reference=="CheckOut"){
                                    findNavController().navigate(R.id.checkOutFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    //requireActivity().itemCart.setImageResource(R.drawable.shopping_cart_active)
                                }
                                else if(reference=="OffersDiscount"){
                                    findNavController().navigate(R.id.discountFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    requireActivity().itemDiscount.setImageResource(R.drawable.discount_active)
                                }
                                else if(reference=="Bloggers"){
                                    findNavController().navigate(R.id.bloggersFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    requireActivity().itemHome.setImageResource(R.drawable.home_active)
                                }
                                else if(reference=="Brands"){
                                    findNavController().navigate(R.id.brandsFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    requireActivity().itemHome.setImageResource(R.drawable.home_active)
                                }
                                else if(reference=="HomeMadeSuppliers"){
                                    findNavController().navigate(R.id.homeMadeSuppliersFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    requireActivity().itemHome.setImageResource(R.drawable.home_active)
                                }
                                else{
                                    findNavController().navigate(R.id.homeFragment)
                                    requireActivity().toolbar.visibility=View.VISIBLE
                                    requireActivity().bottomNavigationView.visibility=View.VISIBLE
                                    requireActivity().itemHome.setImageResource(R.drawable.home_active)
                                }

                            }

                            else{
                                /*startActivity(Intent(requireContext(), ResetPasswordActivity::class.java)
                                        .putExtra("user_id", user_id))*/
                                 val bundle=Bundle()
                                 bundle.putString("user_id", user_id)
                                 bundle.putString("reference", reference)
                                 findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment, bundle)
                                //requireActivity().itemProfile.setImageResource(R.drawable.home_active)
                            }

                        }
                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    private fun resendOtp() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "fcm_token", "device_type", "lang"),
                arrayOf(user_id, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                        , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.resendOtp(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(requireContext(), jsonObject.getString("message"))

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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    private fun setBottomView() {
        requireActivity().itemDiscount.setImageResource(R.drawable.discount)
        //requireActivity().itemCart.setImageResource(R.drawable.shopping_cart)
        requireActivity().itemHome.setImageResource(R.drawable.home1)
        //requireActivity().itemProfile.setImageResource(R.drawable.profile_5)

    }
}