package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.HistoryAdapter
import com.seen.user.adapter.UpComingAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Categories
import com.seen.user.model.CategoryName
import com.seen.user.model.MyOrders
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_orders.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyOrdersFragment : Fragment() {

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
    var direction = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            notType = it.getString("type").toString()
            direction = it.getInt("direction")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_my_orders, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        if(notType.equals("reject", true)){
            setHistoryTab()
        }
        else{
            setUpcomingTab()
        }

        return mView
    }
    private fun setUpViews() {

        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            if (direction==1){
                findNavController().navigate(R.id.homeFragment)
            }else{
                findNavController().popBackStack()
            }
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
                                m.supplier_id = obj.getInt("supplier_id")
                                m.order_data = obj.getJSONArray("order_data")
                                m.subtotal = obj.getString("subtotal")
                                m.shipping_fee = obj.getString("shipping_fee")
                                m.taxes = obj.getString("taxes")
                                m.total_price = obj.getString("total_price")
                                m.delivery_date = obj.getString("delivery_date")
                                m.file = obj.getString("file")
                                m.AWBNumber = obj.getString("AWBNumber")

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
                bundle.putString("file", historyList[pos].file.toString())
                bundle.putString("delivery_date", historyList[pos].delivery_date)
                bundle.putString("shipping_fee", historyList[pos].shipping_fee)
                bundle.putString("subtotal", historyList[pos].subtotal)
                bundle.putString("taxes", historyList[pos].taxes)
                bundle.putString("total_price", historyList[pos].total_price)
                bundle.putString("order_id", historyList[pos].order_id)
                bundle.putInt("id", historyList[pos].id)
                bundle.putInt("supplier_id", historyList[pos].supplier_id)
                bundle.putString("AWBNumber", historyList[pos].AWBNumber)
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
                bundle.putString("AWBNumber", upcomingList[pos].AWBNumber)
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
        var order_status = 0
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