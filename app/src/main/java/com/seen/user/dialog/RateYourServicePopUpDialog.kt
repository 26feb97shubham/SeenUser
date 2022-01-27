package com.seen.user.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.model.PostRatingResponse
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_rate_your_service_pop_up_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateYourServicePopUpDialog : BottomSheetDialogFragment() {
    private var btnOk: TextView? = null
    private var btnCancel: TextView? = null
    override fun getTheme(): Int {
        return R.style.RateYourServicePopUpDialogTheme
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rate_your_service_pop_up_dialog, container, false)
        btnOk = view.findViewById(R.id.btnOk)
        btnCancel = view.findViewById(R.id.btnCancel)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnCancel!!.setOnClickListener {
            dismiss()
        }

        product_ratings = productRatingBar.rating.toDouble()

        btnOk!!.setOnClickListener {
            val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
            val builder = ApiClient.createBuilder(arrayOf("user_id", "order_id", "supplier_id","rating", "product_ratings"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), order_id,
                    supplier_id.toString(), supp_rating.toString(), productings_ratings))

            val call = apiInterface.postRating(builder.build())
            call!!.enqueue(object : Callback<PostRatingResponse?>{
                override fun onResponse(
                    call: Call<PostRatingResponse?>,
                    response: Response<PostRatingResponse?>
                ) {
                    if (response.body()!=null){
                        if (response.body()!!.response==1){
                            LogUtils.shortToast(requireContext(), ""+response.body()!!)
                            findNavController().navigate(R.id.myOrdersFragment)
                            dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<PostRatingResponse?>, t: Throwable) {
                    LogUtils.shortToast(requireContext(), t.localizedMessage)
                }

            })
        }

        Glide.with(requireContext()).load(files).into(productimg)
        prod_name.text= product_name
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val TAG = "RateYourServicePopUpDialog"
        var product_name = ""
        var supplier_name = ""
        var files = ""
        var productings_ratings = ""
        var order_id = ""
        var pro_id = 0
        var supplier_id = 0
        var product_ratings = 0.0
        var supp_rating : Float= 0.0F
        fun newInstance(context: Context?, bundle: Bundle): RateYourServicePopUpDialog {
            //this.context = context;
            product_name = bundle.getString("product_name")!!
            files = bundle.getString("files")!!
            order_id = bundle.getString("order_id")!!
            productings_ratings = bundle.getString("productings_ratings")!!
            pro_id = bundle.getInt("product_id")
            supplier_id = bundle.getInt("supplier_id")
            supp_rating = bundle.getFloat("supp_rating")
            return RateYourServicePopUpDialog()
        }
    }
    interface OnOkClick{
        fun okay()
    }
}