package com.seen.user.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.AttributesAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Attributes
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_product_details.view.*
import kotlinx.android.synthetic.main.fragment_product_details.view.progressBar
import kotlinx.android.synthetic.main.fragment_select_attribute_pop_up_dialog_box.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SelectAttributePopUpDialogBoxFragment(private val attrArrayDataList: ArrayList<Attributes>,
                                            private val productId : String) : DialogFragment() {
    private var completionCallback : SelectAttributeInterface?=null
    private var myAttrArrayDataList = ArrayList<Attributes>()
    lateinit var attributesAdapter: AttributesAdapter
    var attributeObj:JSONObject?=null
    private var already_added : Boolean = false
    private var product_item_id = ""
    private var myProductId = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_select_attribute_pop_up_dialog_box, container, false)
        setUpViews(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setDataCompletionCallback(completionCallback: SelectAttributeInterface?) {
        this.completionCallback = completionCallback
    }



    private fun setUpViews(view: View) {
        myAttrArrayDataList = attrArrayDataList
        myProductId = productId

       /* view.rvListAttributesSelectPopUp.layoutManager= LinearLayoutManager(requireContext())
        attributesAdapter= AttributesAdapter(requireContext(), myAttrArrayDataList, object : ClickInterface.ClickJSonObjInterface{
            override fun clickJSonObj(obj: JSONObject) {
                attributeObj=obj
                Log.e("attributeObj", attributeObj.toString())
                if(attributeObj!!.getJSONArray("data").length()==2){
                    checkProductPrice(view)
                }
            }


        })
        view.rvListAttributesSelectPopUp.adapter=attributesAdapter
        attributesAdapter.notifyDataSetChanged()*/

        view.selectAttributeButton.setOnClickListener {
            completionCallback?.attributesSelected(already_added, product_item_id)
            dismiss()
        }
    }


    private fun checkProductPrice(myView: View) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        myView.selectedAttributePopUpDialogProgressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "device_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), myProductId, attributeObj!!.getJSONArray("data").toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.checkProductPrice(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                myView.selectedAttributePopUpDialogProgressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){

                            product_item_id=jsonObject.getString("product_item_id")
                            if(TextUtils.isEmpty(product_item_id)){
                                LogUtils.shortToast(requireContext(), getString(R.string.this_item_is_currently_out_of_stock))
                                myView.selectAttributeButton.isEnabled = false
                            }else{
                                myView.selectAttributeButton.isEnabled = true
                                already_added=jsonObject.getBoolean("already_added")
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
                myView.selectedAttributePopUpDialogProgressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })


    }

    interface SelectAttributeInterface{
        fun attributesSelected(alreadyAdded:Boolean, productItemId: String)
    }
}