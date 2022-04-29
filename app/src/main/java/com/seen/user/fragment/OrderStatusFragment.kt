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
import com.seen.user.adapter.Orders_Products_Adapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_order_status.view.*
import org.json.JSONArray

class OrderStatusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var mView: View?=null
    var productList=ArrayList<ProductList>()
   /* lateinit var ordersProductAdapter: OrdersProductAdapter*/
    lateinit var ordersProductsAdapter: Orders_Products_Adapter
    var order_data:String=""
    var delivery_date:String=""
    var shipping_fee:String=""
    var subtotal:String=""
    var taxes:String=""
    var total_price:String=""
    var order_id:String=""
    var my_id:Int=0
    var status : String = ""
    var order_status : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            order_data = it.getString("order_data").toString()
            delivery_date = it.getString("delivery_date").toString()
            shipping_fee = it.getString("shipping_fee").toString()
            subtotal = it.getString("subtotal").toString()
            taxes = it.getString("taxes").toString()
            total_price = it.getString("total_price").toString()
            order_id = it.getString("order_id").toString()
            my_id = it.getInt("id")
            order_status = it.getInt("order_status")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_order_status, container, false)
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
            findNavController().navigate(R.id.myOrdersFragment)
        }

        mView!!.orderNum.text=getString(R.string.order_hess)+order_id
        mView!!.deliveryDate.text=getString(R.string.delivery_date)+" "+delivery_date
        if(!TextUtils.isEmpty(order_data)){
            val jsonArray=JSONArray(order_data)
            for(i in 0 until jsonArray.length()){
                val obj1 = jsonArray.getJSONObject(i)
                val p = ProductList()
                p.quantity = obj1.getInt("quantity")
                p.id = obj1.getInt("product_id")
                p.price = obj1.getString("price")
                p.discount = obj1.getString("discount")
                /*  p.product_item_id = obj1.getInt("product_item_id")*/
                p.category_name = obj1.getString("category_name")
                p.name = obj1.getString("product_name")
                p.supplier_name = obj1.getString("supplier_name")
                p.files = obj1.getString("files")
                productList.add(p)
            }
        }

        if (MyOrdersFragment.order_status == 0){
            mView!!.pickedUpImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
            mView!!.onWayImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
            mView!!.deliveredImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
        }else if(MyOrdersFragment.order_status == 1){
            mView!!.pickedUpImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
            mView!!.onWayImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
            mView!!.deliveredImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
        }else if(MyOrdersFragment.order_status == 2){
            mView!!.pickedUpImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
            mView!!.onWayImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
            mView!!.deliveredImg.setBackgroundResource(R.drawable.black_ring_gray_bg)
        }else{
            mView!!.pickedUpImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
            mView!!.onWayImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
            mView!!.deliveredImg.setBackgroundResource(R.drawable.black_ring_gold_bg)
        }



/*        ordersProductAdapter= OrdersProductAdapter(requireContext(), productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {

            }

        })*/

        ordersProductsAdapter = Orders_Products_Adapter(requireContext(), productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {

            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(context)
        mView!!.rvList.adapter=ordersProductsAdapter

        mView!!.btnsubmitrating.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("order_data", order_data)
            bundle.putString("delivery_date", delivery_date)
            bundle.putString("shipping_fee", shipping_fee)
            bundle.putString("subtotal",subtotal)
            bundle.putString("taxes",taxes)
            bundle.putString("total_price",total_price)
            bundle.putString("order_id",order_id)
            bundle.putInt("id",my_id)
            findNavController().navigate(R.id.action_rating_to_ratingProductsFragment, bundle)
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