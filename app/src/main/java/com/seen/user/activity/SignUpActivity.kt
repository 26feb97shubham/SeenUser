package com.seen.user.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seen.user.BuildConfig
import com.seen.user.R
import com.seen.user.custom.FetchPath
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_sign_up.*
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

class SignUpActivity : AppCompatActivity() {
    var name: String = ""
    var phone: String = ""
    var email: String = ""
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
    var cCodeList= arrayListOf<String>()
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setUpViews()
        getCountires()
    }
    private fun setUpViews() {
 /*      frag_other_backImg.setOnClickListener {
           frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
//            onBackPressed()
           finish()
        }*/

        btnNext.setOnClickListener {
            btnNext.startAnimation(AlphaAnimation(1f, 0.5f))
           SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnNext)
            validateAndSignUp()
        }

        editProfile.setOnClickListener {
            editProfile.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

        scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                edtName.clearFocus()
                edtEmail.clearFocus()
                edtPhone.clearFocus()
                return false
            }

        })
        txtCountryCode.setOnClickListener {
            if(cCodeList.size != 0){
                showCountryCodeList()
            }
        }

        mtv_log_in.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun getCountires() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
       progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val call = apiInterface.getCountries()
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
               progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries = jsonObject.getJSONArray("countries")
                        countryCodes.clear()
                        for (i in 0 until countries.length()) {
                            val jsonObj = countries.getJSONObject(i)
                            countryCodes.add(jsonObj.getString("country_code"))
                            cCodeList.add(jsonObj.getString("country_name") + " ("+jsonObj.getString("country_code")+")")
                        }
                        txtCountryCode.text=countryCodes[0]

                        Log.d("countries", countryCodes.toString())

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
                LogUtils.shortToast(this@SignUpActivity, getString(R.string.check_internet))
               progressBar.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun validateAndSignUp() {
        name = edtName.text.toString()
        phone = edtPhone.text.toString()
        email = edtEmail.text.toString()
        selectCountryCode= txtCountryCode.text.toString()

        if (TextUtils.isEmpty(name)) {
            scrollView.scrollTo(0, 150)
            edtName.requestFocus()
            edtName.error=getString(R.string.please_enter_your_full_name)
        } else if (!isCharacterAllowed(name)) {
            scrollView.scrollTo(0, 150)
            edtName.requestFocus()
           edtName.error=getString(R.string.emojis_are_not_allowed)
        } else if (TextUtils.isEmpty(email)) {
            scrollView.scrollTo(0, 210)
            edtEmail.requestFocus()
            edtEmail.error=getString(R.string.please_enter_your_email)
        } else if (!SharedPreferenceUtility.getInstance().isEmailValid(email)){
            scrollView.scrollTo(0, 210)
            edtEmail.requestFocus()
            edtEmail.error=getString(R.string.please_enter_valid_email)
        } else if (TextUtils.isEmpty(selectCountryCode)) {
            scrollView.scrollTo(0, 210)
            txtCountryCode.requestFocus()
            txtCountryCode.error=getString(R.string.please_select_your_country_code)
        } else if (TextUtils.isEmpty(phone)) {
            scrollView.scrollTo(0, 180)
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.please_enter_your_phone_number)
        } else if ((phone.length < 7 || phone.length > 15)) {
            scrollView.scrollTo(0, 180)
            edtPhone.requestFocus()
            edtPhone.error=getString(R.string.mob_num_length_valid)
        }
        else {
            val bundle = Bundle()
            bundle.putString("userName", name)
            bundle.putString("email", email)
            bundle.putString("country_code", selectCountryCode)
            bundle.putString("mobile", phone)
            bundle.putString("imagePath", imagePath)
            startActivity(Intent(this, SignUp2Activity::class.java).putExtras(bundle))
            finish()
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

        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setItems(cCodeList.toArray(arrayOfNulls<String>(cCodeList.size))) { dialogInterface, i ->
            txtCountryCode.text=countryCodes[i]
        }


        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight: Int = displayMetrics.heightPixels
        val layoutParams= WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.6f).toInt()
        val dialogWindowHeight = (displayHeight * 0.6f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        dialog.window!!.attributes = layoutParams
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
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(this, *PERMISSIONS)) {
            openCameraDialog()
        }
    }

    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel))
        val builder = AlertDialog.Builder(this)
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
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
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
                    LogUtils.shortToast(this, "Please grant both Camera and Storage permissions")

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(this, "Please grant both Camera and Storage permissions")
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
                    LogUtils.shortToast(this, "something went wrong! please try again")
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null) {
                imagePath = ""
                val uri = data.data
                imagePath = if (uri.toString().startsWith("content")) {
                    FetchPath.getPath(this, uri!!)!!
                } else {
                    uri!!.path!!
                }
                Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imagePath").into(img)
            }
        }

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