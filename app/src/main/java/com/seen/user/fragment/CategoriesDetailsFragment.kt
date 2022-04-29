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
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.AccountTypeAdapter
import com.seen.user.adapter.CategoriesGridFilteredProductsAdapter
import com.seen.user.adapter.CategoryListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.*
import com.seen.user.rest.ApiClient
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_categories_details.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CategoriesDetailsFragment : Fragment() {

    var mView: View?=null
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var allCatList=ArrayList<Categories>()
    var category:String=""
    var supplier_user_id:Int=0
    var category_id = ""
    var category_name = ""
    var category_name_ar = ""
    lateinit var categoriesgridFilteredProductsListAdapter: CategoriesGridFilteredProductsAdapter
    private var productsList = ArrayList<ProductsItemX>()
    private var queryMap = HashMap<String, String>()
    private var search_keyword : String = ""
    var accTypeList = ArrayList<AccountTypesItem>()
    lateinit var accountTypeAdapter: AccountTypeAdapter
    private var acc_type_id:String = ""
    private var filter_clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*    category = it.getString("categories").toString()
                supplier_user_id = it.getInt("supplier_user_id", 0)*/
            category_id = it.getString("category_id").toString()
            category_name = it.getString("category_name").toString()
            category_name_ar = it.getString("category_name_ar").toString()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_categories_details, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }

    private fun setUpViews() {

        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().home_frag_categories.visibility=View.GONE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.homeFragment)
        }

        if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
            mView!!.tv_category.text = category_name_ar
        }else{
            mView!!.tv_category.text = category_name
        }


        Log.e("category_id",""+category_id)

        getAccType()

        mView!!.tv_dropdown_items_names.setOnClickListener {
            if (!filter_clicked){
                filter_clicked = true
                mView!!.cl_dropdown_layout.visibility = View.VISIBLE
            }else{
                filter_clicked = false
                mView!!.cl_dropdown_layout.visibility = View.GONE
            }
        }

        mView!!.tv_all.setOnClickListener {
            mView!!.cl_dropdown_layout.visibility = View.GONE
            mView!!.tv_dropdown_items_names.text = mView!!.tv_all.text.toString()
            queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
            queryMap.put("category_id", category_id)
            queryMap.put("account_type", "")
            defaultProductList(queryMap)
        }
    }

    private fun defaultProductList(queryMap: HashMap<String, String>) {
        val builder = ApiClient.createBuilder(arrayOf("user_id", "category_id", "account_type"),
                arrayOf(queryMap.get("user_id").toString(),
                        queryMap.get("category_id").toString(),
                        queryMap.get("account_type").toString()))
        val call = Utility.apiInterface.getProductsCategories(builder.build())
        call?.enqueue(object : Callback<GetProductsResponse?> {
            override fun onResponse(call: Call<GetProductsResponse?>, response: Response<GetProductsResponse?>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        if (response.body()!!.products==null){
                            mView!!.tv_no_of_results.text = "0"
                            mView!!.rv_filtered_products_categories_home.visibility = View.GONE
                            mView!!.txtNoDataFound.visibility = View.VISIBLE
                            LogUtils.shortToast(requireContext(), getString(R.string.no_results_found))
                        }else{
                            mView!!.rv_filtered_products_categories_home.visibility = View.VISIBLE
                            mView!!.txtNoDataFound.visibility = View.GONE
                            productsList.clear()
                            productsList = response.body()!!.products as ArrayList<ProductsItemX>
                            mView!!.tv_no_of_results.text = productsList.size.toString()
                            Log.e("Products_list", productsList.toString())
                            mView!!.rv_filtered_products_categories_home.layoutManager = GridLayoutManager(requireContext(), 3)
                            categoriesgridFilteredProductsListAdapter = CategoriesGridFilteredProductsAdapter(requireContext(),
                                    productsList,findNavController(), object : ClickInterface.ClickPosInterface{
                                override fun clickPostion(pos: Int,type: String) {
                                    Log.e("Position_1", pos.toString())
                                }

                            })
                            mView!!.rv_filtered_products_categories_home.adapter = categoriesgridFilteredProductsListAdapter
                            categoriesgridFilteredProductsListAdapter.notifyDataSetChanged()
                        }
//                        setProducts(productsList)
                    }
                }else {
                    LogUtils.shortCenterToast(requireContext(), getString(R.string.no_results_found))
                }
            }

            override fun onFailure(call: Call<GetProductsResponse?>, t: Throwable) {
                LogUtils.shortToast(requireContext(), t.message)
            }

        })
    }

    private fun setProducts(productsList: ArrayList<ProductsItemX>) {

    }

    private fun getAccType() {
        val call = Utility.apiInterface.getAccType()
        call?.enqueue(object : Callback<AccountTypeResponse?> {
            override fun onResponse(call: Call<AccountTypeResponse?>, response: Response<AccountTypeResponse?>) {
                try {
                    if (response.body() != null) {
                        accTypeList = response.body()!!.account_types as ArrayList<AccountTypesItem>
//                        txtCountryCode.text=country_code[0]
                        if (accTypeList.size!=0){
                            mView!!.rv_categories_acc_type.visibility = View.VISIBLE
                            mView!!.divider.visibility = View.VISIBLE
                            if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
                                mView!!.tv_dropdown_items_names.text = accTypeList[0].name_ar
                            }else{
                                mView!!.tv_dropdown_items_names.text = accTypeList[0].name
                            }
                            mView!!.rv_categories_acc_type.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            accountTypeAdapter= AccountTypeAdapter(requireContext(), accTypeList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    acc_type_id = accTypeList[pos].id.toString()
                                    mView!!.cl_dropdown_layout.visibility = View.GONE
                                    if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals("ar")){
                                        mView!!.tv_dropdown_items_names.text = accTypeList[pos].name_ar
                                    }else{
                                        mView!!.tv_dropdown_items_names.text = accTypeList[pos].name
                                    }
                                    queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                                    queryMap.put("category_id", category_id)
                                    queryMap.put("account_type", acc_type_id)
                                    defaultProductList(queryMap)
                                }

                            })

                            queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                            queryMap.put("category_id", category_id)
                            queryMap.put("account_type",accTypeList[0].id.toString())
                            defaultProductList(queryMap)
                            mView!!.rv_categories_acc_type.adapter=accountTypeAdapter
                            accountTypeAdapter.notifyDataSetChanged()
                        }else{
                            mView!!.rv_categories_acc_type.visibility = View.GONE
                            mView!!.divider.visibility = View.GONE
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

            override fun onFailure(call: Call<AccountTypeResponse?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
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
}