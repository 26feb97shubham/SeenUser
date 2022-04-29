package com.seen.user.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.OrdersProductAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_order_details.view.btnsubmitrating
import kotlinx.android.synthetic.main.fragment_order_details.view.deliveryDate
import kotlinx.android.synthetic.main.fragment_order_details.view.orderNum
import kotlinx.android.synthetic.main.fragment_order_details.view.rvList
import org.json.JSONArray

class OrderDetailsFragment : Fragment() {
    var mView: View?=null
    var productList=ArrayList<ProductList>()
    lateinit var ordersProductAdapter: OrdersProductAdapter
    var order_data:String=""
    var delivery_date:String=""
    var product_item_id:String=""
    var shipping_fee:String=""
    var subtotal:String=""
    var taxes:String=""
    var total_price:String=""
    var order_id:String=""
    var file:String=""
    var my_id:Int=0
    var supplier_id:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            order_data = it.getString("order_data").toString()
            delivery_date = it.getString("delivery_date").toString()
            shipping_fee = it.getString("shipping_fee").toString()
            file = it.getString("file").toString()
            subtotal = it.getString("subtotal").toString()
            taxes = it.getString("taxes").toString()
            total_price = it.getString("total_price").toString()
            order_id = it.getString("order_id").toString()
            my_id = it.getInt("id")
            supplier_id = it.getInt("supplier_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_order_details, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }
    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView!!.orderNum.text=getString(R.string.order_hess)+order_id
        mView!!.deliveryDate.text=getString(R.string.delivery_date)+" "+delivery_date
        if(!TextUtils.isEmpty(order_data)){
            productList.clear()
            val jsonArray= JSONArray(order_data)
            for(i in 0 until jsonArray.length()){
                val obj1 = jsonArray.getJSONObject(i)
                val p = ProductList()
                p.quantity = obj1.getInt("quantity")
                p.id = obj1.getInt("product_id")
                p.price = obj1.getString("price")
                p.discount = obj1.getString("discount")
                p.product_item_id = obj1.getString("product_item_id")
                p.category_name = obj1.getString("category_name")
                p.name = obj1.getString("product_name")
                p.supplier_name = obj1.getString("supplier_name")
                p.supplier_profile_picture =file
                p.files = obj1.getString("files")
                productList.add(p)
            }
        }

        ordersProductAdapter= OrdersProductAdapter(requireContext(), productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {

            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(context)
        mView!!.rvList.adapter=ordersProductAdapter


        mView!!.btnsubmitrating.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("product_name", productList[0].name)
            bundle.putString("supplier_name", productList[0].supplier_name)
            bundle.putString("supplier_profile_picture", productList[0].supplier_profile_picture)
            bundle.putString("files", productList[0].files)
            bundle.putString("order_id", order_id)
            bundle.putInt("product_id",productList[0].id)
            bundle.putString("product_item_id", productList[0].product_item_id)
            bundle.putInt("supplier_id",supplier_id)
            findNavController().navigate(R.id.action_rating_to_ratingProductsFragment, bundle)
           /* val bundle = Bundle()
            bundle.putString("product_name", productList[0].name)
            bundle.putString("supplier_name", productList[0].supplier_name)
            bundle.putString("files", productList[0].files)
            bundle.putDouble("rating", productList[0].rating)
            val rateYourServicePopUpDialog = RateYourServicePopUpDialog.newInstance(requireContext(), bundle)

            rateYourServicePopUpDialog.show(requireActivity().supportFragmentManager, RateYourServicePopUpDialog.TAG)*/
        }

    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
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
}