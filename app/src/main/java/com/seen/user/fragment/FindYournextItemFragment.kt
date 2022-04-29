package com.seen.user.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.CategoryWiseListAdapter
import com.seen.user.adapter.ProductWiseListAdapter
import com.seen.user.model.*
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_find_yournext_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindYournextItemFragment : Fragment() {
    private var mView : View?= null
    private var productItem:ArrayList<ProductsItemXX?>?=null
    private var dataList : ArrayList<DataItem?>?=null
    private var productWiseListAdapter : ProductWiseListAdapter?=null
    private var categoryWiseListAdapter : CategoryWiseListAdapter?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_find_yournext_item, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_toolbar.frag_other_backImg.visibility = View.VISIBLE
        requireActivity().frag_other_toolbar.frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_toolbar.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_toolbar.frag_other_backImg)
            findNavController().popBackStack()
        }

        requireActivity().home_frag_categories.visibility = View.GONE

//        getGlobalSearchList("")

        mView!!.textSearch.doOnTextChanged { text, start, before, count ->
            if (count == 0 && text!!.isEmpty()){
                mView!!.mcv_search.visibility = View.GONE
            }else{
                mView!!.mcv_search.visibility = View.VISIBLE
                getGlobalSearchList(text)
            }
        }
    }

    private fun getGlobalSearchList(text: CharSequence?) {
        productItem?.clear()
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id","device_id", "lang", "search"),
            arrayOf( SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString(),
            text.toString()))
        val call = apiInterface.globalSearch(builder.build())
        call?.enqueue(object : Callback<GlobalSrchResponse?>{
            override fun onResponse(
                call: Call<GlobalSrchResponse?>,
                response: Response<GlobalSrchResponse?>
            ) {
                if (response.body()==null){
//                    LogUtils.shortToast(requireContext(), "No Data Found")
                    LogUtils.shortToast(requireContext(), requireContext().getString(R.string.no_results_found))
                    mView!!.mcv_search.visibility = View.GONE
                }else{
                    mView!!.mcv_search.visibility = View.VISIBLE
                    productItem = response.body()!!.products as ArrayList<ProductsItemXX?>?
                    dataList = response.body()!!.data as ArrayList<DataItem?>?

                    mView!!.rv_all_searches.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    mView!!.rv_category_wise_searches.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    productWiseListAdapter = ProductWiseListAdapter(requireContext(), productItem, findNavController())
                    categoryWiseListAdapter = CategoryWiseListAdapter(requireContext(), dataList, findNavController())

                    mView!!.rv_all_searches.adapter = productWiseListAdapter
                    mView!!.rv_category_wise_searches.adapter = categoryWiseListAdapter

                    productWiseListAdapter?.notifyDataSetChanged()
                    categoryWiseListAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GlobalSrchResponse?>, t: Throwable) {
                LogUtils.shortToast(requireContext(), requireContext().getString(R.string.no_results_found))
            }

        })
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