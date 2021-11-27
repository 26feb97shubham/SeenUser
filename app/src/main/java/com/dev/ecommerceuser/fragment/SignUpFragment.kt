package com.dev.ecommerceuser.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.ecommerceuser.BuildConfig
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.activity.TermsAndConditionsActivity
import com.dev.ecommerceuser.custom.FetchPath
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.rest.ApiUtils
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import okhttp3.MediaType
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of requireContext() fragment.
 */
class SignUpFragment : Fragment() {
    var mView: View?=null
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val IMAGE_DIRECTORY_NAME = "Seen"
    private var imagePath = ""
    private var selectCountryCode = ""
    private var countryCodes=ArrayList<String>()
    lateinit var adp: ArrayAdapter<String>
    var isChecked: Boolean=false
    var cCodeList= arrayListOf<String>()
    var reference:String=""
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
        // Inflate the layout for requireContext() fragment
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_sign_up, container, false)
            setUpViews()
            getCountires()
        }
        return mView
    }

    private fun setUpViews() {
        mView!!.frag_other_backImg.setOnClickListener {
            mView!!.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            findNavController().popBackStack()
        }

        mView!!.btnSignUp.setOnClickListener {
            mView!!.btnSignUp.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnSignUp)
            validateAndSignUp()
        }

        mView!!.editProfile.setOnClickListener {
            mView!!.editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
            mView!!.edtPassword.gravity=Gravity.START
            mView!!.edtConfirmPassword.gravity=Gravity.START
        }
        else{
            mView!!.edtPassword.gravity=Gravity.END
            mView!!.edtConfirmPassword.gravity=Gravity.END
        }

        /*  mView!!.imgChk.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
              override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                  tcAccepted = isChecked
  
              }
  
          })*/

        mView!!.imgChk.setOnClickListener {
            mView!!.imgChk.startAnimation(AlphaAnimation(1f, 0.5f))
            if(isChecked){
                isChecked=false
                mView!!.imgChk.setImageResource(R.drawable.un_check)
            }
            else{
                isChecked=true
                mView!!.imgChk.setImageResource(R.drawable.check)
            }
        }

        mView!!.scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mView!!.edtName.clearFocus()
                mView!!.edtEmail.clearFocus()
                mView!!.edtPhone.clearFocus()
                mView!!.edtPassword.clearFocus()
                mView!!.edtConfirmPassword.clearFocus()
                return false
            }

        })


        /*val w1 = SpannableString(resources.getString(R.string.please_accept) + " ")
        w1.setSpan(StyleSpan(Typeface.NORMAL), 0, w1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       mView!!.mView!!.txtTermsConditions.text = w1

        val w2 = SpannableString(resources.getString(R.string.terms_amp_conditions))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w2.setSpan(StyleSpan(Typeface.BOLD), 0, w2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
       mView!!.mView!!.txtTermsConditions.append(w2)*/
       mView!!.txtTermsConditions.setOnClickListener {
            mView!!.txtTermsConditions.startAnimation(AlphaAnimation(1f, 0.5f))
           startActivity(Intent(requireContext(), TermsAndConditionsActivity::class.java).putExtra("title", getString(R.string.terms_amp_conditions)))
        }
        mView!!.txtCountryCode.setOnClickListener {
            if(cCodeList.size != 0){
                showCountryCodeList()
            }

        }
        /*  adp = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, countryCodes)
          adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         spinner.adapter = adp
  
         spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
              override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                  selectCountryCode=spinner.selectedItem.toString()
  
              }
  
              override fun onNothingSelected(p0: AdapterView<*>?) {
                  selectCountryCode=""
              }
  
  
          }*/
        mView!!.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass =mView!!.edtPassword.text.toString()

                if(!TextUtils.isEmpty(pass)){
                    if(!pass.equals(charSeq.toString(), false)){
                        mView!!.edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_verify_password)
                    }
                }
                else{
                    mView!!.edtPassword.error=getString(R.string.please_first_enter_your_password)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun getCountires() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val call = apiInterface.getCountries()
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries = jsonObject.getJSONArray("countries")
                        countryCodes.clear()
                        cCodeList.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            countryCodes.add(jsonObj.getString("country_code"))
                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }
//                        mView!!.txtCountryCode.text=countryCodes[0]

                        Log.d("countries", countryCodes.toString())
                        adp.notifyDataSetChanged()

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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun validateAndSignUp() {
        name = mView!!.edtName.text.toString()
        phone = mView!!.edtPhone.text.toString()
        email = mView!!.edtEmail.text.toString()
        password= mView!!.edtPassword.text.toString()
        confirmPassword= mView!!.edtConfirmPassword.text.toString()
        selectCountryCode= mView!!.txtCountryCode.text.toString()

        if (TextUtils.isEmpty(name)) {
            mView!!.scrollView.scrollTo(0, 150)
            mView!!.edtName.requestFocus()
            mView!!.edtName.error=getString(R.string.please_enter_your_full_name)
//            LogUtils.shortToast(this, getString(R.string.please_eRkjRR

        }
        else if (!isCharacterAllowed(name)) {
            mView!!.scrollView.scrollTo(0, 150)
            mView!!.edtName.requestFocus()
            mView!!.edtName.error=getString(R.string.emojis_are_not_allowed)
//              LogUtils.shortToast(this, getString(R.string.emojis_are_not_allowed))
        }
        else if (TextUtils.isEmpty(selectCountryCode)) {
//            mView!!.edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_your_country_code))

        }
        else if (TextUtils.isEmpty(phone)) {
            mView!!.scrollView.scrollTo(0, 180)
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.please_enter_your_phone_number)
//             LogUtils.shortToast(this, getString(R.string.please_enter_your_mob_number))

        }
        else if ((phone.length < 7 || phone.length > 15)) {
            mView!!.scrollView.scrollTo(0, 180)
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.mob_num_length_valid)
//             LogUtils.shortToast(this, getString(R.string.mob_num_length_valid))
        }
        /* else if (TextUtils.isEmpty(email)) {
             LogUtils.shortToast(this, getString(R.string.please_enter_your_email))
 
         }*/
        else if (!TextUtils.isEmpty(email) && !SharedPreferenceUtility.getInstance().isEmailValid(email)) {
            mView!!.scrollView.scrollTo(0, 210)
            mView!!.edtEmail.requestFocus()
            mView!!.edtEmail.error=getString(R.string.please_enter_valid_email)
//            LogUtils.shortToast(this, getString(R.string.please_enter_valid_email))
        }

        else if (TextUtils.isEmpty(password)) {
            mView!!.edtPassword.requestFocus()
            mView!!.scrollView.scrollTo(0, 240)
            mView!!.edtPassword.error=getString(R.string.please_enter_your_password)
//            LogUtils.shortToast(this, getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isPasswordValid(password)) {
            mView!!.edtPassword.requestFocus()
            mView!!.scrollView.scrollTo(0, 240)
            mView!!.edtPassword.error=getString(R.string.password_length_valid)
//            LogUtils.shortToast(this, getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            mView!!.scrollView.scrollTo(0, 270)
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.please_verify_your_password)
//            LogUtils.shortToast(this, getString(R.string.please_verify_your_password))
        }
        /* else if (confirmPassword.length < 6) {
             mView!!.edtConfirmPassword.error=getString(R.string.verify_password_length_valid)
 //            LogUtils.shortToast(this, getString(R.string.verify_password_length_valid))
 
         }*/
        else if (!confirmPassword.equals(password)) {
            mView!!.scrollView.scrollTo(0, 270)
            mView!!.edtConfirmPassword.requestFocus()
            mView!!.edtConfirmPassword.error=getString(R.string.password_doesnt_match_with_verify_password)
//            LogUtils.shortToast(this, getString(R.string.password_doesnt_match_with_verify_password))
        }


        else if(!isChecked){
            LogUtils.shortToast(requireContext(), getString(R.string.please_accept_terms_conditions))
        }

        else {
            getSignUp()
        }
    }
    private fun isCharacterAllowed(validateString: String): Boolean {
        var containsInvalidChar = false
        for (i in 0 until validateString.length) {
            val type = Character.getType(validateString[i])
            containsInvalidChar = !(type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt())
        }
        return containsInvalidChar
    }
    private fun showCountryCodeList() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select country code")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            mView!!.txtCountryCode.text=countryCodes[i]
        }


        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams= WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.8f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog.window!!.attributes = layoutParams
    }
    private fun getSignUp() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("email", "password", "fcm_token", "device_type", "name", "mobile", "country_code", "lang"),
                arrayOf(email.trim({ it <= ' ' }), password.trim({ it <= ' ' }),
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.FCMTOKEN, ""], ApiUtils.DeviceType, name.trim { it <= ' ' }, phone.trim({ it <= ' ' }), selectCountryCode, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            builder!!.addFormDataPart("profile_picture", file.name, requestBody)
        }


        val call = apiInterface.signUp(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
//                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("user_id"))
                             val bundle = Bundle()
                             bundle.putString("ref", "1")
                             bundle.putString("reference", reference)
                             bundle.putString("user_id", data.getInt("user_id").toString())
                             findNavController().navigate(R.id.action_signUpFragment_to_otpVerificationFragment, bundle)
                           /* startActivity(Intent(requireContext(), OtpVerificationActivity::class.java).putExtra("ref", "1")
                                    .putExtra("user_id", data.getInt("user_id").toString()))*/

                        }
                        /*else if (jsonObject.getInt("response") == 2){
                            *//*val data = jsonObject.getJSONObject("data")
                            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.UserId, data.getInt("id"))*//*
                            val bundle=Bundle()
                            bundle.putString("ref", "1")
                            findNavController().navigate(R.id.action_signUpFragment_to_otpVerificationFragment, bundle)

                        } */
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    fun requestToUploadProfilePhoto() {
        if (!hasPermissions(requireContext(), *PERMISSIONS)) {
           requestPermissions(PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(requireContext(), *PERMISSIONS)) {
            openCameraDialog()
        }
    }

    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel))
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.camera)) {
                captureImage()
            } else if (items[i] == getString(R.string.gallery)) {
                chooseImage()
            } else if (items[i] == getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }


    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
    }


    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }


    fun getOutputMediaFileUri(type: Int): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
        } else {
            Uri.fromFile(getOutputMediaFile(type))
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".png")
        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }
        return mediaFile
    }


    /*private fun getRealPath(ur: Uri?): String {
        var realpath = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        // Get the cursor
        val cursor = contentResolver.query(ur!!,
                filePathColumn, null, null, null)!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        //Log.e("columnIndex", String.valueOf(MediaStore.Images.Media.DATA));
        realpath = cursor.getString(columnIndex)
        cursor.close()
        return realpath
    }*/


    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE) {
            if (grantResults.size > 0) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                    openCameraDialog()
                } else {
                    LogUtils.shortToast(requireContext(), "Please grant both Camera and Storage permissions")

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(requireContext(), "Please grant both Camera and Storage permissions")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) { //previewCapturedImage();
                if (uri != null) {
                    imagePath = ""
                    Log.e("uri", uri.toString())
                    imagePath = uri!!.path!!
                    Glide.with(this).load("file:///$imagePath").placeholder(R.drawable.user).into(img)
                } else {
                    LogUtils.shortToast(requireContext(), "something went wrong! please try again")
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                imagePath = ""
                val uri = data.data
                imagePath = if (uri.toString().startsWith("content")) {
                    FetchPath.getPath(requireContext(), uri!!)!!
                } else {
                    uri!!.path!!
                }
                Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imagePath").into(img)
            }
        }

    }
    companion object {
        /**
         * Use requireContext() factory method to create a new instance of
         * requireContext() fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}