package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.OrdersProductAdapter
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.ProductList
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_order_details.view.*
import kotlinx.android.synthetic.main.fragment_order_details.view.btnsubmitrating
import kotlinx.android.synthetic.main.fragment_order_details.view.deliveryDate
import kotlinx.android.synthetic.main.fragment_order_details.view.orderNum
import kotlinx.android.synthetic.main.fragment_order_details.view.rvList
import kotlinx.android.synthetic.main.fragment_order_status.view.*
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var productList=ArrayList<ProductList>()
    lateinit var ordersProductAdapter: OrdersProductAdapter
    var order_data:String=""
    var delivery_date:String=""
    var shipping_fee:String=""
    var subtotal:String=""
    var taxes:String=""
    var total_price:String=""
    var order_id:String=""
    var my_id:Int=0
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
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_order_details, container, false)
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
            val jsonArray= JSONArray(order_data)
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

        ordersProductAdapter= OrdersProductAdapter(requireContext(), productList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {

            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(context)
        mView!!.rvList.adapter=ordersProductAdapter


        mView!!.btnsubmitrating.setOnClickListener {
            findNavController().navigate(R.id.action_rating_to_ratingProductsFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.VISIBLE
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
         * @return A new instance of fragment OrderDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                OrderDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}