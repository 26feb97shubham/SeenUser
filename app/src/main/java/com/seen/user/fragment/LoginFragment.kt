package com.seen.user.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.*
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

class   LoginFragment : Fragment() {
    var mView: View?=null
    var remembered:Boolean=false
    var phone: String = ""
    var password: String = ""
    var reference:String=""
    var spannableString : SpannableString?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reference = it.getString("reference", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_login, container, false)
            Utility.changeLanguage(
                requireContext(),
                SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
            )
            spannableString = SpannableString("SIGNUP")
            spannableString!!.setSpan(UnderlineSpan(), 0, spannableString!!.length, 0)
            mView!!.tv_signup.setText(spannableString)
            setUpViews()
        }
        return mView
    }
    private fun setUpViews() {

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsRemembered, false]){
            remembered=true
            mView!!.chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
//            chkRememberMe.isChecked=true
            phone=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Phone, ""]
            password=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.Password, ""]
            mView!!.edtPhone.setText(phone)
            mView!!.edtPass.setText(password)
        }

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
            mView!!.edtPass.gravity=Gravity.START
        }
        else{
            mView!!.edtPass.gravity=Gravity.END
        }

        /*  chkRememberMe.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
               override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                   remembered = isChecked
   
               }
   
           })*/
        mView!!.chkRememberMe.setOnClickListener {
            if(remembered){
                remembered=false
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
                    mView!!.chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.un_check, 0, 0, 0)
                }
                else{
                    mView!!.chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.un_check, 0)
                }

            }
            else{
                remembered=true
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
                    mView!!.chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0)
                }
                else{
                    mView!!.chkRememberMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0)
                }
            }
        }

       /* mView!!.backImg.setOnClickListener {
            mView!!.backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.backImg)
//            findNavController().popBackStack()
            findNavController().navigate(R.id.chooseLoginSingUpFragment)
        }*/


        mView!!.txtForgotPass.setOnClickListener {
            mView!!.txtForgotPass.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.txtForgotPass)
            val bundle=Bundle()
            bundle.putString("reference", reference)
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment, bundle)

        }
        mView!!.btnLogin.setOnClickListener {
            mView!!.btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnLogin)
//            startActivity(Intent(requireContext(), HomeActivity::class.java))
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
    }


    private fun validateAndLogin() {
        phone = mView!!.edtPhone.text.toString()
        password= mView!!.edtPass.text.toString()


        if (TextUtils.isEmpty(phone)) {
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.please_enter_your_phone_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.mob_num_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.mob_num_length_valid))
        }

        else if (TextUtils.isEmpty(password)) {
            mView!!.edtPass.requestFocus()
            mView!!.edtPass.error=getString(R.string.please_enter_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            mView!!.edtPass.requestFocus()
            mView!!.edtPass.error=getString(R.string.invalid_password)
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
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("password", "fcm_token", "device_type", "device_id", "mobile", "lang"),
                arrayOf(password.trim({ it <= ' ' }),
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                        , ApiUtils.DeviceType,  SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], phone.trim({ it <= ' ' }), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.login(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                            val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            if(jsonObject.getInt("carts_count")!=0){
                                requireActivity().cartWedgeCount.visibility=View.VISIBLE
                                requireActivity().cartWedgeCount.text=jsonObject.getInt("carts_count").toString()
                            }
                            else{
                                requireActivity().cartWedgeCount.visibility=View.GONE
                            }
                            setBottomView()
                            requireActivity().llLogout.visibility=View.VISIBLE
                            requireActivity().logoutView.visibility=View.VISIBLE
                            requireActivity().name.setTextColor(
                                    ContextCompat.getColor(
                                            requireContext(),
                                            R.color.black
                                    )
                            )
                            requireActivity().name.text = data.getString("name")
                            requireActivity().email.text = data.getString("email")
                            Glide.with(requireContext()).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(requireActivity().userIcon)

                            if(reference=="Profile"){
                                findNavController().navigate(R.id.profileFragment)
                                requireActivity().toolbar.visibility=View.VISIBLE
                                requireActivity().bottomNavigationView.visibility=View.VISIBLE
                              //  requireActivity().itemProfile.setImageResource(R.drawable.user_profile_active)
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
//                            startActivity(Intent(requireContext(), HomeActivity::class.java))

                        }
                        else if (jsonObject.getInt("response") == 2){
                            val data = jsonObject.getJSONObject("data")
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                              val bundle=Bundle()
                              bundle.putString("ref", "2")
                              bundle.putString("reference", reference)
                              bundle.putString("user_id", data.getInt("user_id").toString())
                              bundle.putString("name", data.getString("name"))
                              bundle.putString("email", data.getString("email"))
                              bundle.putString("profile_picture", data.getString("profile_picture"))
                              findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment, bundle)
                           /* startActivity(Intent(requireContext(), OtpVerificationActivity::class.java).putExtra("ref", "1")
                                    .putExtra("user_id", data.getInt("user_id").toString()))*/

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
    private fun setBottomView() {
        requireActivity().itemDiscount.setImageResource(R.drawable.discount)
        //requireActivity().itemCart.setImageResource(R.drawable.shopping_cart)
        requireActivity().itemHome.setImageResource(R.drawable.home1)
       // requireActivity().itemProfile.setImageResource(R.drawable.profile_5)

    }

}