package com.seen.user.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.CouponCodeAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Coupon
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility.Companion.total_discount
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_apply_coupon.view.*
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
 * Use the [ApplyCouponFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplyCouponFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView:View?=null
    lateinit var couponCodeAdapter: CouponCodeAdapter
    var couponList=ArrayList<Coupon>()
    var supplier_id:Int=0
    var coupon_code=""
    var amount=""
    var minPrice=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            supplier_id = it.getInt("supplier_id", 0)
            amount = it.getString("amount").toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_apply_coupon, container, false)
        Log.e("amount", ""+amount)
        setUpViews()
        getCoupons(false)
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
            mView!!.swipeRefresh.startAnimation(AlphaAnimation(1f, 0.5f))
            getCoupons(true)
        }

        mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
        couponCodeAdapter= CouponCodeAdapter(requireContext(), couponList, object :ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="apply") {
                    coupon_code = couponList[pos].code
                    minPrice = couponList[pos].min_price
                    /*if (minPrice<amount){
                        LogUtils.shortToast(requireContext(), "Min Price for Coupans is not matching")
                    }else{
                        applyCoupons()
                    }*/
                    applyCoupons()
                }
                else{
                    val args=Bundle()
                    args.putString("title", getString(R.string.terms_amp_conditions))
                    findNavController().navigate(R.id.action_applyCouponFragment_to_webViewFragment, args)
                }
            }


        })
        mView!!.rvList.adapter=couponCodeAdapter

        mView!!.txtApply.setOnClickListener {
            mView!!.txtApply.startAnimation(AlphaAnimation(1f, .5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.txtApply)
            checkCoupon()
        }

        mView!!.edtSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.edtSearch)
                checkCoupon()
                    true
            } else {
                false
            }
        }

    }

    private fun checkCoupon() {
        coupon_code=mView!!.edtSearch.text.toString()
        if(!TextUtils.isEmpty(coupon_code)){
           /* if (minPrice<amount){
                LogUtils.shortToast(requireContext(), "Min Price for Coupans is not matching")
            }else{
                applyCoupons()
            }*/
            applyCoupons()
        }
        else{
            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_coupon_code))
        }
    }

    private fun getCoupons(isRefresh: Boolean) {
        if(!isRefresh){
            mView!!.progressBar.visibility= View.VISIBLE
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("supplier_id", "lang"),
            arrayOf(supplier_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.getCoupons(builder.build())
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
                            val coupons=jsonObject.getJSONArray("coupons")
                            couponList.clear()
                            mView!!.txtNoDataFound.visibility=View.GONE
                            mView!!.rvList.visibility=View.VISIBLE
                            for(i in 0  until coupons.length()){
                                val obj = coupons.getJSONObject(i)
                                val c = Coupon()
                                c.id = obj.getInt("id")
                                c.code = obj.getString("code")
                                c.title = obj.getString("title")
                                c.description = obj.getString("description")
                                c.percentage = obj.getString("percentage")
                                c.from_date = obj.getString("from_date")
                                c.to_date = obj.getString("to_date")
                                c.picture = obj.getString("picture")
                                c.min_price = obj.getString("min_price")
                                c.max_discount_price = obj.getString("max_discount_price")
                                couponList.add(c)
                            }
                        }

                        else {
                            couponList.clear()
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                        couponCodeAdapter.notifyDataSetChanged()
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
    private fun applyCoupons() {
        mView!!.progressBar.visibility= View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "supplier_id", "code", "amount", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), supplier_id.toString(), coupon_code, amount, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.applyCoupons(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        total_discount = jsonObject.getDouble("total_discount")
                        findNavController().popBackStack()
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

    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ApplyCouponFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ApplyCouponFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}