package com.seen.user.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seen.user.R
import com.seen.user.adapter.FilteredProductsListAdapter
import com.seen.user.adapter.GridFilteredProductsListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductsItem
import com.seen.user.model.SearchFilterResponse
import com.seen.user.rest.ApiClient
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility.Companion.apiInterface
import com.seen.user.utils.Utility.Companion.price_category
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.iv_filter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilteredProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView:View?=null
    lateinit var filteredProductsListAdapter: FilteredProductsListAdapter
    lateinit var gridFilteredProductsListAdapter: GridFilteredProductsListAdapter
    private var productsList = ArrayList<ProductsItem>()
    private var queryMap = HashMap<String, String>()
    private var search_keyword : String = ""
    var country_id = ""
    var price_cat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           if (it!=null){
               country_id = it.getInt("country_id").toString()
           }else{
               country_id = ""
           }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filtered_products, container, false)
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

        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("device_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.DeviceId, ""))
        queryMap.put("search", SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.searchItem, ""])
        queryMap.put("category_id", "")
        queryMap.put("account_type", "")
        queryMap.put("country_id", country_id)
        queryMap.put("price", "")
        queryMap.put("price_from", "")
        queryMap.put("price_to", "")
        defaultProductList(queryMap)

        mView!!.iv_filter.setOnClickListener {
            val filterBottomSheetDialogFragment = FilterBottomSheetDialogFragment.newInstance(requireContext())
            filterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, FilterBottomSheetDialogFragment.TAG)
            filterBottomSheetDialogFragment.setFilterClickListenerCallback(object : FilterBottomSheetDialogFragment.OnFilterClick{
              override fun onFilter(queryMap: HashMap<String, String>, price_category: String) {
                 this@FilteredProductsFragment.queryMap = queryMap
                  price_cat = price_category
                  defaultProductList(queryMap)
              }
          })
        }

        mView!!.tv_search.setOnClickListener {
            findNavController().navigate(R.id.findYourNextItemFragment)
        }

        mView!!.loc_view_filtered_products_frag.setOnClickListener {
            findNavController().navigate(R.id.locationFragment)
        }

   /*     mView!!.tv_search.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    search_keyword = mView!!.tv_search.text.toString().trim()
                    SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(),  mView!!.tv_search)
                    //  getAllServicesListing(latitude!!, longitude!!, 0,"", search_keyword, 1)
                    queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                    queryMap.put("device_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.DeviceId, ""))
                    queryMap.put("search", search_keyword)
                    queryMap.put("category_id", "")
                    queryMap.put("account_type", "")
                    queryMap.put("country_id", "")
                    queryMap.put("price", "")
                    queryMap.put("price_from", "")
                    queryMap.put("price_to", "")
                    defaultProductList(queryMap)
                    if (TextUtils.isEmpty(search_keyword)){
                        LogUtils.shortToast(requireContext(), getString(R.string.please_enter_search_keyword_for_searching))
                    }else{
                        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                        queryMap.put("device_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.DeviceId, ""))
                        queryMap.put("search", search_keyword)
                        queryMap.put("category_id", "")
                        queryMap.put("account_type", "")
                        queryMap.put("country_id", "")
                        queryMap.put("price", "")
                        queryMap.put("price_from", "")
                        queryMap.put("price_to", "")
                        defaultProductList(queryMap)
                    }
                    return true
                }
                return false
            }

        })*/

        mView!!.selector_layout.setOnClickListener {
            mView!!.selector_categories_layout.visibility = View.VISIBLE
        }

        mView!!.all_tv.setOnClickListener {
            mView!!.selector_tv.text = mView!!.all_tv.text.toString()
            mView!!.selector_categories_layout.visibility = View.GONE
        }

        mView!!.bloggers_tv.setOnClickListener {
            mView!!.selector_tv.text = mView!!.bloggers_tv.text.toString()
            mView!!.selector_categories_layout.visibility = View.GONE
        }

        mView!!.homemadesuppliers_tv.setOnClickListener {
            mView!!.selector_tv.text = mView!!.homemadesuppliers_tv.text.toString()
            mView!!.selector_categories_layout.visibility = View.GONE
        }

        mView!!.brands_tv.setOnClickListener {
            mView!!.selector_tv.text = mView!!.brands_tv.text.toString()
            mView!!.selector_categories_layout.visibility = View.GONE
        }
    }

    private fun defaultProductList(queryMap: HashMap<String, String>) {
        val builder = ApiClient.createBuilder(arrayOf("user_id", "device_id", "search", "category_id", "account_type","country_id", "price", "price_from", "price_to"),
                arrayOf(queryMap.get("user_id").toString(),
                        queryMap.get("device_id").toString(),
                        queryMap.get("search").toString(),
                        queryMap.get("category_id").toString(),
                        queryMap.get("account_type").toString(),
                        queryMap.get("country_id").toString(),
                        queryMap.get("price").toString(),
                        queryMap.get("price_from").toString(),
                        queryMap.get("price_to").toString()))
        val call = apiInterface.searchFilter(builder.build())
        call?.enqueue(object : Callback<SearchFilterResponse?>{
            override fun onResponse(call: Call<SearchFilterResponse?>, response: Response<SearchFilterResponse?>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        productsList = response.body()!!.products as ArrayList<ProductsItem>
                        if (!price_cat.equals("")){
                            mView!!.tv_filterSearch.text = price_cat
                            price_cat = ""
                            price_category = ""
                        }else{
                            mView!!.tv_filterSearch.text = ""
                        }

                        if(productsList.size==0){
                            mView!!.tv_no_product.visibility = View.VISIBLE
                            mView!!.scroll_view_filtered_products_frag.visibility = View.GONE
                        }else{
                            mView!!.tv_no_product.visibility = View.GONE
                            mView!!.scroll_view_filtered_products_frag.visibility = View.VISIBLE
                            setProducts(productsList)
                        }
                    }
                }else {
                    LogUtils.shortCenterToast(requireContext(), getString(R.string.no_results_found))
                }
            }

            override fun onFailure(call: Call<SearchFilterResponse?>, t: Throwable) {
                LogUtils.shortToast(requireContext(), t.message)
            }

        })
    }

    private fun setProducts(productsList: ArrayList<ProductsItem>) {
        Log.e("Products_list", productsList.toString())
        mView!!.rv_filtered_products.layoutManager = GridLayoutManager(requireContext(), 3)
        gridFilteredProductsListAdapter = GridFilteredProductsListAdapter(requireContext(),
                productsList,findNavController(), object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int,type: String) {
                Log.e("Position_1", pos.toString())
            }

        })
        mView!!.rv_filtered_products.adapter = gridFilteredProductsListAdapter
        gridFilteredProductsListAdapter.notifyDataSetChanged()
        /*mView!!.rv_products.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recentProductsAdapter = RecentProductsAdapter(requireContext(),productsList,findNavController(), object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int,type: String) {
                Log.e("Position_1", pos.toString())
                *//* if(type.equals("Like")){
                     likeUnlikeProduct(pos, productsList)
                 } else if(type.equals("Cart")) {
                     LogUtils.shortCenterToast(context, "Item added to cart successfully!!!!. '\n' Thanks alot for purchasing")
                 } else{
                         val bundle = Bundle()
                     bundle.putInt("product_id", productsList[pos].id)
                     findNavController().navigate(R.id.productDetailsFragment, bundle)
                 }*//*
            }

        })
        mView!!.rv_products.adapter = recentProductsAdapter
        recentProductsAdapter.notifyDataSetChanged()*/
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