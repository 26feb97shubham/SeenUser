package com.seen.user.fragment

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
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seen.user.BuildConfig
import com.seen.user.R
import com.seen.user.custom.FetchPath
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.frag_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.profile_toolbar_layout.view.*
import kotlinx.android.synthetic.main.side_top_view.*
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

class ProfileFragment : Fragment() {
    var mView:View?=null
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val IMAGE_DIRECTORY_NAME = "Seen"
    private var imagePath = ""
    private var email = ""
    private var name = ""
    private var selectCountryCode = ""
    private var mobile = ""
    private var country_code=ArrayList<String>()
    var cCodeList= arrayListOf<String>()
    var profile_picture:String=""
    var registered_number=""
    var registered_country_code=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requireContext() fragment
//        if(mView==null) {
            mView = inflater.inflate(R.layout.frag_profile, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
            setUpViews()
            getCountires()
//        }
        return mView
    }
    private fun setUpViews() {
/*        requireActivity().backImg.visibility=View.VISIBLE


        requireActivity().backImg.setOnClickListener {
            requireActivity().backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().backImg)
            findNavController().navigate(R.id.homeFragment)
        }*/

        requireActivity().profile_fragment_toolbar.profileFragment_backimg.setOnClickListener {
           requireActivity().profile_fragment_toolbar.profileFragment_backimg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()

        }



   /*     underlineForChangePass()
        underlineForAddLocation()
        underlineForAddCard()*/

        mView!!.txtChangePass.setOnClickListener {
            mView!!.txtChangePass.startAnimation(AlphaAnimation(1f, 0.5f))
            val args= Bundle()
            args.putString("profile_picture", profile_picture)
            findNavController().navigate(R.id.action_profileFragment_to_changePassFragment, args)
        }

      /*  mView!!.editProfile.setOnClickListener {
            mView!!.editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }*/

      /*  mView!!.btnSubmit.setOnClickListener {
            mView!!.btnSubmit.startAnimation(AlphaAnimation(1f, .5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(),   mView!!.btnSubmit)
            validateAndEdit()
        }*/

       /* mView!!.txtCountryCode.setOnClickListener {
            if(cCodeList.size != 0){
                showCountryCodeList()
            }

        }*/

        mView!!.txtAddLocationLayout.setOnClickListener {
            mView!!.txtAddLocationLayout.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_profileFragment_to_addLocationFragment)
        }
        mView!!.txtAddCardLayout.setOnClickListener {
            mView!!.txtAddCardLayout.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_profileFragment_to_addCardFragment)
        }

        mView!!.btneditprofile.setOnClickListener {
            mView!!.btneditprofile.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }



        /*  requireActivity().notificationImg.setOnClickListener {
              requireActivity().notificationImg.startAnimation(AlphaAnimation(1f, 0.5f))
  //            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
              findNavController().navigate(R.id.notificationsFragment)
          }
          requireActivity().llContactUs.setOnClickListener {
              requireActivity().llContactUs.startAnimation(AlphaAnimation(1f, 0.5f))
              requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
              findNavController().navigate(R.id.contactUSFragment)
          }*/


    }

  /*  private fun underlineForChangePass() {
        val underline = SpannableString(requireContext().getString(R.string.change_password_caps))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView!!.txtChangePass.text=underline
    }

    private fun underlineForAddLocation() {
        val underline = SpannableString(requireContext().getString(R.string.add_location))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView!!.txtAddLocation.text=underline
    }
    private fun underlineForAddCard() {
        val underline = SpannableString(requireContext().getString(R.string.add_card_details))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView!!.txtAddCard.text=underline
    }*/


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
                        country_code.clear()
                        cCodeList.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            country_code.add(jsonObj.getString("country_code"))
                            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
                                cCodeList.add(jsonObj.getString("country_name_ar") + " ("+jsonObj.getString("country_code")+")")
                            }else{
                                cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                            }
                        }
//                        txtCountryCode.text=country_code[0]


                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                getProfile()
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun getProfile() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.getProfile(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val data=jsonObject.getJSONObject("data")
                            Glide.with(requireContext()).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(mView!!.img)
                            mView!!.tv_profileName.setText(data.getString("name"))
                            mView!!.tv_email.setText(data.getString("email"))
                            registered_number=data.getString("mobile")
                            mView!!.tv_phone.setText(registered_number)
                            registered_country_code=data.getString("country_code")
                        /*    for(i in 0 until country_code.size){
                                if(country_code[i]==registered_country_code){
                                    mView!!.txtCountryCode.text=country_code[i]
                                }
                            }*/
                            requireActivity().name.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                          /*  requireActivity().name.text= data.getString("name")
                            requireActivity().email.text= data.getString("email")*/
                            profile_picture=data.getString("profile_picture")
                            Glide.with(requireContext()).load(profile_picture).placeholder(R.drawable.user).into(requireActivity().userIcon)
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

   

    private fun validateAndEdit() {
        name=mView!!.edtName.text.toString()
        email=mView!!.edtEmail.text.toString()
        mobile=mView!!.edtPhone.text.toString()
        selectCountryCode=mView!!.txtCountryCode.text.toString()
        if (TextUtils.isEmpty(name)) {
            mView!!.edtName.requestFocus()
            mView!!.edtName.error=getString(R.string.please_enter_your_full_name)
//            LogUtils.shortToast(this, getString(R.string.please_eRkjRR

        }
        else if (!SharedPreferenceUtility.getInstance().isCharacterAllowed(name)) {
            mView!!.edtName.requestFocus()
            mView!!.edtName.error=getString(R.string.emojis_are_not_allowed)
//              LogUtils.shortToast(this, getString(R.string.emojis_are_not_allowed))
        }
        else if (!TextUtils.isEmpty(email) && !SharedPreferenceUtility.getInstance().isEmailValid(email)) {
            mView!!.edtEmail.requestFocus()
            mView!!.edtEmail.error=getString(R.string.please_enter_valid_email)
//            LogUtils.shortToast(this, getString(R.string.please_enter_valid_email))
        }
      /*  else if (TextUtils.isEmpty(selectCountryCode)) {
//            edtPhone.error=getString(R.string.please_select_your_country_code)
            LogUtils.shortToast(requireContext(), getString(R.string.please_select_your_country_code))

        }*/
        else if (TextUtils.isEmpty(mobile)) {
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.please_enter_your_phone_number)
//             LogUtils.shortToast(this, getString(R.string.please_enter_your_mob_number))

        }
        else if ((mobile.length < 7 || mobile.length > 15)) {
            mView!!.edtPhone.requestFocus()
            mView!!.edtPhone.error=getString(R.string.mob_num_length_valid)
//             LogUtils.shortToast(this, getString(R.string.mob_num_length_valid))
        }


        else {
            if(registered_number!=mobile){
                val builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle(requireContext().getString(R.string.alert_i))
                builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_update_your_phone_number))
                builder.setPositiveButton(R.string.ok) { dialog, which ->
                    dialog.cancel()
                    editProfile()
                }
                builder.setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.cancel()
                    mobile=registered_number
                    selectCountryCode=registered_country_code
                    editProfile()
                }
                builder.show()
            }
            else {
                editProfile()
            }
        }
    }

    private fun editProfile() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("email", "name", "mobile", "country_code", "user_id",  "lang"),
            arrayOf(email.trim({ it <= ' ' }), name.trim({ it <= ' ' }), mobile.trim({ it <= ' ' }), selectCountryCode
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        if (imagePath != "") {
            val file = File(imagePath)
            val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            builder!!.addFormDataPart("profile_picture", file.name, requestBody)
        }


        val call = apiInterface.editProfile(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            getProfile()
                        }
                        else if (jsonObject.getInt("response") == 2) {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                            SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                            val args=Bundle()
                            args.putString("reference", "Logout")
                            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    private fun showCountryCodeList() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select country code")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            if( mView!!.txtCountryCode.text.toString()!=country_code[i]){
                mView!!.txtCountryCode.text=country_code[i]
                mView!!.edtPhone.setText("")
            }

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
    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
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
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.please_grant_both_camera_and_storage_permissions))

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(requireContext(),  requireContext().getString(R.string.please_grant_both_camera_and_storage_permissions))
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
                    Glide.with(requireContext()).load("file:///$imagePath").placeholder(R.drawable.user).into(mView!!.img)
                } else {
                    LogUtils.shortToast(requireContext(),  requireContext().getString(R.string.something_went_wrong_please_try_again))
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
                Glide.with(requireContext()).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imagePath").into(mView!!.img)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        getProfile()
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.VISIBLE
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
}