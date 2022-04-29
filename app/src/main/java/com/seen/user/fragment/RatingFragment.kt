package com.seen.user.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.dialog.RateYourServicePopUpDialog
import com.seen.user.model.MyOrders
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_rating.view.*
import org.json.JSONArray
import org.json.JSONObject

class RatingFragment : Fragment() {
    var mView:View?=null
    var product_name = ""
    var supplier_name = ""
    var supplier_profile_picture = ""
    var files = ""
    var order_id = ""
    var product_item_id = ""
    var rating = 0.0
    var pro_id = 0
    var supplier_id = 0
    var productings_ratings = JSONArray()
    var received_product_List=ArrayList<MyOrders>()
    var products_JSONOBJ = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           product_name = it.getString("product_name")!!
            supplier_name = it.getString("supplier_name")!!
            supplier_profile_picture = it.getString("supplier_profile_picture")!!
            files = it.getString("files")!!
            order_id = it.getString("order_id")!!
            product_item_id = it.getString("product_item_id")!!
            pro_id = it.getInt("product_id")
            supplier_id = it.getInt("supplier_id")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_rating, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_toolbar.frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_toolbar.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_toolbar.frag_other_backImg)
            findNavController().popBackStack()
        }


        Glide.with(requireContext()).load(files).into(mView!!.productimg)
        Glide.with(requireContext()).load(supplier_profile_picture).into(mView!!.supplierimg)
        mView!!.prod_name.text = product_name
        mView!!.supplier_name.text = supplier_name
        mView!!.productRatingBar.rating = rating.toFloat()



        products_JSONOBJ.put("order_id", order_id)
        products_JSONOBJ.put("product_id", pro_id)
        products_JSONOBJ.put("product_item_id", product_item_id)
        products_JSONOBJ.put("rating", mView!!.productRatingBar.rating)
        productings_ratings.put(products_JSONOBJ)

        mView!!.tv_add_rating.setOnClickListener {
             val bundle = Bundle()
            bundle.putString("product_name", product_name)
            bundle.putString("files", files)
            bundle.putString("order_id", order_id)
            bundle.putString("productings_ratings", productings_ratings.toString())
            bundle.putInt("product_id", pro_id)
            bundle.putInt("supplier_id", supplier_id)
            bundle.putFloat("supp_rating", mView!!.productRatingBar.rating)
            val rateYourServicePopUpDialog = RateYourServicePopUpDialog.newInstance(requireContext(), bundle)
            rateYourServicePopUpDialog.show(requireActivity().supportFragmentManager, RateYourServicePopUpDialog.TAG)
        }
    }

}