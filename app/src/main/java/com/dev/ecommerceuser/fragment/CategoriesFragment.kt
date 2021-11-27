package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.AccountTypeAdapter
import com.dev.ecommerceuser.adapter.CategoriesGridFilteredProductsAdapter
import com.dev.ecommerceuser.adapter.CategoryListAdapter
import com.dev.ecommerceuser.adapter.GridFilteredProductsListAdapter
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.*
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.dev.ecommerceuser.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.filter_bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_bloggers.view.*
import kotlinx.android.synthetic.main.fragment_categories.view.*
import kotlinx.android.synthetic.main.fragment_categories.view.progressBar
import kotlinx.android.synthetic.main.fragment_categories.view.rvList
import kotlinx.android.synthetic.main.fragment_categories.view.txtNoDataFound
import kotlinx.android.synthetic.main.fragment_categories_details.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.tv_search
import okhttp3.ResponseBody
import org.json.JSONArray
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
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var allCatList=ArrayList<Categories>()
    var category:String=""
    var supplier_user_id:Int=0
    var category_id = ""
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
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_categories_details, container, false)
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

        Log.e("category_id",""+category_id)

        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("category_id", category_id)
        queryMap.put("account_type", "")
        defaultProductList(queryMap)

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
        call?.enqueue(object : Callback<GetProductsResponse?>{
            override fun onResponse(call: Call<GetProductsResponse?>, response: Response<GetProductsResponse?>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
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
        call?.enqueue(object : Callback<AccountTypeResponse?>{
            override fun onResponse(call: Call<AccountTypeResponse?>, response: Response<AccountTypeResponse?>) {
                try {
                    if (response.body() != null) {
                        accTypeList = response.body()!!.account_types as ArrayList<AccountTypesItem>
//                        txtCountryCode.text=country_code[0]
                        if (accTypeList.size!=0){
                            mView!!.rv_categories_acc_type.visibility = View.VISIBLE
                            mView!!.divider.visibility = View.VISIBLE
                            mView!!.rv_categories_acc_type.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            accountTypeAdapter= AccountTypeAdapter(requireContext(), accTypeList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    acc_type_id = accTypeList[pos].id.toString()
                                    mView!!.cl_dropdown_layout.visibility = View.GONE
                                    mView!!.tv_dropdown_items_names.text = accTypeList[pos].name
                                    queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                                    queryMap.put("category_id", category_id)
                                    queryMap.put("account_type", acc_type_id)
                                    defaultProductList(queryMap)
                                }

                            })
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoriesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CategoriesFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
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
}