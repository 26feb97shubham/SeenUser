package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.rest.ApiUtils
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_reset_password.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResetPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResetPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var mView: View?=null
    lateinit var user_id: String
    var password: String = ""
    var confirmPassword: String = ""
    lateinit var reference: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user_id = it.getString("user_id").toString()
            reference = it.getString("reference").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_reset_password, container, false)
            setUpViews()
        }
        return mView
    }

    private fun setUpViews() {
       mView!!.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass = mView!!.edtPassword.text.toString()

                if(!TextUtils.isEmpty(pass)){
                    if(!pass.equals(charSeq.toString(), false)){
                        mView!!.edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
                    }
                    else{
                        SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.edtConfirmPassword)
                    }
                }
                else{
                    mView!!.edtPassword.error=getString(R.string.please_first_enter_your_password)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        mView!!.btnSubmit.setOnClickListener {
            mView!!.btnSubmit.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndReset()
            /* val navOptions = NavOptions.Builder().setPopUpTo(R.id.my_nav_graph, true).build()
             findNavController().navigate(R.id.action_resetPasswordFragment_to_homeFragment, null, navOptions)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 requireActivity().window.setDecorFitsSystemWindows(true)
             } else {
 //                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                 requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
             }*/
        }
    }

    private fun validateAndReset() {
        password= mView!!.edtPassword.text.toString()
        confirmPassword= mView!!.edtConfirmPassword.text.toString()

        if (TextUtils.isEmpty(password)) {
            mView!!.edtPassword.requestFocus()
            mView!!.edtPassword.error=getString(R.string.please_enter_your_new_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            mView!!.edtPassword.requestFocus()
            mView!!.edtPassword.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.please_verify_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_verify_your_password))
        }
        /* else if (confirmPassword.length < 6) {
              mView!!.edtConfirmPassword.error=getString(R.string.verify_password_length_valid)
 //            LogUtils.shortToast(requireContext(), getString(R.string.verify_password_length_valid))

         }*/
        else if (!confirmPassword.equals(password)) {
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_verify_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_doesnt_match_with_verify_password))
        }
        else{
            resetPassword()
        }

    }


    private fun resetPassword() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "password", "confirm_password", "fcm_token", "device_type", "lang"),
                arrayOf(user_id, password, confirmPassword, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""]
                        , ApiUtils.DeviceType, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.resetPassword(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        if(jsonObject.getInt("response")==1){
                            /*SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, user_id)
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsLogin, true)
                            startActivity(Intent(requireContext(), HomeActivity::class.java))*/
                            val args=Bundle()
                            args.putString("reference", reference)
//                            val navOptions = NavOptions.Builder().setPopUpTo(R.id.home_nav_graph, true).build()
                            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment, args)
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

    override fun onResume() {
        super.onResume()
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResetPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResetPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}