package com.seen.user.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_change_password.view.*
import kotlinx.android.synthetic.main.profile_toolbar_layout.view.*
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
 * Use the [ChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePasswordFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var oldPassword: String = ""
    var newPassword: String = ""
    var confirmPassword: String = ""
    var profile_picture: String = ""
    var oldPassVis=false
    var newPassVis=false
    var confmPassVis=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            profile_picture = it.getString("profile_picture").toString()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_change_password, container, false)
            setUpViews()
        }
        return mView
    }
    private fun setUpViews() {
    /*    requireActivity().backImg.visibility = View.VISIBLE
        requireActivity().notificationImg.visibility = View.GONE
        requireActivity().menuImg.visibility = View.GONE*/

        Glide.with(requireContext()).load(profile_picture).placeholder(R.drawable.user).into(mView!!.img)

       /* requireActivity().backImg.setOnClickListener {
            requireActivity().backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().backImg)
            findNavController().popBackStack()
        }*/

        requireActivity().profile_fragment_toolbar.profileFragment_backimg.setOnClickListener {
            requireActivity().profile_fragment_toolbar.profileFragment_backimg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.profileFragment)
        }

        mView!!.btnChangePass.setOnClickListener {
            mView!!.btnChangePass.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndChangePassword()
        }
        mView!!.edtNewPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(SharedPreferenceUtility.getInstance().isPasswordValid(charSeq.toString())){
                    mView!!.imgPassVerify.visibility=View.VISIBLE

                }
                else{
                    mView!!.imgPassVerify.visibility=View.GONE

                }


            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        mView!!.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass = mView!!.edtNewPass.text.toString()

                if(!TextUtils.isEmpty(pass)){
                    if(!pass.equals(charSeq.toString(), false)){
                        mView!!.imgConfPassVerify.visibility=View.GONE
                        mView!!.txtPassMatch.visibility=View.GONE
                    }
                    else{
                        mView!!.imgConfPassVerify.visibility=View.VISIBLE
                        mView!!.txtPassMatch.visibility=View.VISIBLE
                        SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.edtConfirmPassword)
                    }
                }
                else{
                    mView!!.edtNewPass.error=getString(R.string.please_first_enter_your_password)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })


        mView!!.imgEyeOldPass.setOnClickListener {
            if(oldPassVis){
                oldPassVis=false
                val start=mView!!.edtOldPass.selectionStart
                val end=mView!!.edtOldPass.selectionEnd
                mView!!.edtOldPass.transformationMethod = PasswordTransformationMethod()
                mView!!.edtOldPass.setSelection(start, end)
                mView!!.imgEyeOldPass.setImageResource(R.drawable.visible)
            }
            else{
                oldPassVis=true
                val start=mView!!.edtOldPass.selectionStart
                val end=mView!!.edtOldPass.selectionEnd
                mView!!.edtOldPass.transformationMethod = null
                mView!!.edtOldPass.setSelection(start, end)
                mView!!.imgEyeOldPass.setImageResource(R.drawable.invisible)
            }
        }

        mView!!.imgEyeNewPass.setOnClickListener {
            if(newPassVis){
                newPassVis=false
                val start=mView!!.edtNewPass.selectionStart
                val end=mView!!.edtNewPass.selectionEnd
                mView!!.edtNewPass.transformationMethod = PasswordTransformationMethod()
                mView!!.edtNewPass.setSelection(start, end)
                mView!!.imgEyeNewPass.setImageResource(R.drawable.visible)
            }
            else{
                newPassVis=true
                val start=mView!!.edtNewPass.selectionStart
                val end=mView!!.edtNewPass.selectionEnd
                mView!!.edtNewPass.transformationMethod = null
                mView!!.edtNewPass.setSelection(start, end)
                mView!!.imgEyeNewPass.setImageResource(R.drawable.invisible)
            }
        }
        mView!!.imgEyeConfPass.setOnClickListener {
            if(confmPassVis){
                confmPassVis=false
                val start=mView!!.edtConfirmPassword.selectionStart
                val end=mView!!.edtConfirmPassword.selectionEnd
                mView!!.edtConfirmPassword.transformationMethod = PasswordTransformationMethod()
                mView!!.edtConfirmPassword.setSelection(start, end)
                mView!!.imgEyeConfPass.setImageResource(R.drawable.visible)
            }
            else{
                confmPassVis=true
                val start=mView!!.edtConfirmPassword.selectionStart
                val end=mView!!.edtConfirmPassword.selectionEnd
                mView!!.edtConfirmPassword.transformationMethod = null
                mView!!.edtConfirmPassword.setSelection(start, end)
                mView!!.imgEyeConfPass.setImageResource(R.drawable.invisible)
            }
        }
    }

    private fun validateAndChangePassword() {
        oldPassword= mView!!.edtOldPass.text.toString()
        newPassword= mView!!.edtNewPass.text.toString()
        confirmPassword= mView!!.edtConfirmPassword.text.toString()

        if (TextUtils.isEmpty(oldPassword)) {
            mView!!.edtOldPass.requestFocus()
            mView!!.edtOldPass.error=getString(R.string.please_enter_your_old_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(oldPassword)) {
            mView!!.edtOldPass.requestFocus()
            mView!!.edtOldPass.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(newPassword)) {
            mView!!.edtNewPass.requestFocus()
            mView!!.edtNewPass.error=getString(R.string.please_enter_your_new_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(newPassword)) {
            mView!!.edtNewPass.requestFocus()
            mView!!.edtNewPass.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.please_verify_your_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_verify_your_password))
        }
        /* else if (confirmPassword.length < 6) {
              edtConfirmPassword.error=getString(R.string.verify_password_length_valid)
 //            LogUtils.shortToast(requireContext(), getString(R.string.verify_password_length_valid))

         }*/
        else if (!confirmPassword.equals(newPassword)) {
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_confirm_password)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_doesnt_match_with_verify_password))
        }
        else{
            changePassword()
        }

    }
    private fun changePassword() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "new_password", "old_password", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                        , newPassword, oldPassword, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.changePassword(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        if(jsonObject.getInt("response")==1){
                            findNavController().popBackStack()
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChangePasswordFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }
}
