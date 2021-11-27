package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.HistoryAdapter
import com.dev.ecommerceuser.adapter.UpComingAdapter
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Categories
import com.dev.ecommerceuser.model.CategoryName
import com.dev.ecommerceuser.model.MyOrders
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
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
 * Use the [MyOrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyOrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    lateinit var upcomingAdapter: UpComingAdapter
    var upcomingList=ArrayList<MyOrders>()
    var historyList=ArrayList<MyOrders>()
    lateinit var historyAdapter: HistoryAdapter
    var account_types_id:String="1"
    var allCatNameList=ArrayList<CategoryName>()
    var allCatList=ArrayList<Categories>()
    var orderstatus : String = ""
    var type:String=""
    var notType:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
             notType = it.getString("type").toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*  if(mView==null) {*/
        mView = inflater.inflate(R.layout.fragment_my_orders, container, false)
        setUpViews()
        if(notType.equals("reject", true)){
            setHistoryTab()
        }
        else{
            setUpcomingTab()
        }


//        }

        return mView
    }
    private fun setUpViews() {

        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView!!.swipeRefresh.setOnRefreshListener {
            if(mView!!.upcomingView.isSelected) {
                type=""
                myOrder()
            }
            else{
                type="3"
                myOrder()
            }
        }

        mView!!.upcomingLayout.setOnClickListener {
            mView!!.upcomingLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setUpcomingTab()
        }
        mView!!.historyLayout.setOnClickListener {
            mView!!.historyLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setHistoryTab()
        }

    }
    private fun myOrder() {
        mView!!.progressBar.visibility= View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "type", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), type, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.myOrder(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val carts=jsonObject.getJSONArray("carts")
                            if(type=="3"){
                                historyList.clear()
                            }
                            else{
                                upcomingList.clear()
                            }

                            mView!!.txtNoDataFound.visibility=View.GONE
                            mView!!.rvList.visibility=View.VISIBLE
                            for(i in 0  until carts.length()){
                                val obj = carts.getJSONObject(i)
                                val m = MyOrders()
                                m.id = obj.getInt("id")
                                m.order_id = obj.getString("order_id")
                                m.order_data = obj.getJSONArray("order_data")
                                m.subtotal = obj.getString("subtotal")
                                m.shipping_fee = obj.getString("shipping_fee")
                                m.taxes = obj.getString("taxes")
                                m.total_price = obj.getString("total_price")
                                m.delivery_date = obj.getString("delivery_date")
                                m.file = obj.getString("file")

                                if(type=="3"){
                                    historyList.add(m)
                                }
                                else{
                                    upcomingList.add(m)
                                }
                            }

                        }

                        else {
                            if(type=="3"){
                                historyList.clear()
                            }
                            else{
                                upcomingList.clear()
                            }
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                        if(type=="3"){
                            historyAdapter.notifyDataSetChanged()
                        }
                        else{
                            upcomingAdapter.notifyDataSetChanged()
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
                 if(mView!!.swipeRefresh.isRefreshing){
                     mView!!.swipeRefresh.isRefreshing=false
                 }
            }
        })


    }

    private fun setHistoryTab() {
        mView!!.upcomingView.isSelected=false
        mView!!.historyView.isSelected=true
        historyAdapter= HistoryAdapter(requireContext(), historyList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
                val bundle=Bundle()
                bundle.putString("order_data", historyList[pos].order_data.toString())
                bundle.putString("delivery_date", historyList[pos].delivery_date)
                bundle.putString("shipping_fee", historyList[pos].shipping_fee)
                bundle.putString("subtotal", historyList[pos].subtotal)
                bundle.putString("taxes", historyList[pos].taxes)
                bundle.putString("total_price", historyList[pos].total_price)
                bundle.putString("order_id", historyList[pos].order_id)
                bundle.putInt("id", historyList[pos].id)
                findNavController().navigate(R.id.action_myOrdersFragment_to_orderDetailsFragment, bundle)
            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=historyAdapter

        type="3"

        myOrder()

//        getCategories(false)

    }

    private fun setUpcomingTab() {
        mView!!.upcomingView.isSelected=true
        mView!!.historyView.isSelected=false
        upcomingAdapter= UpComingAdapter(requireContext(), upcomingList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
                val bundle=Bundle()
                bundle.putString("order_data", upcomingList[pos].order_data.toString())
                bundle.putString("delivery_date", upcomingList[pos].delivery_date)
                bundle.putString("shipping_fee", upcomingList[pos].shipping_fee)
                bundle.putString("subtotal", upcomingList[pos].subtotal)
                bundle.putString("taxes", upcomingList[pos].taxes)
                bundle.putString("total_price", upcomingList[pos].total_price)
                bundle.putString("order_id", upcomingList[pos].order_id)
                bundle.putInt("id", upcomingList[pos].id)
                order_status = upcomingList[pos].status
                bundle.putInt("order_status", upcomingList[pos].status)
                findNavController().navigate(R.id.action_myOrdersFragment_to_orderStatusFragment, bundle)
            }

        })

        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=upcomingAdapter

        type=""

        myOrder()

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMadeSuppliersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeMadeSuppliersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var order_status = 0
    }

    override fun onResume() {
        super.onResume()
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