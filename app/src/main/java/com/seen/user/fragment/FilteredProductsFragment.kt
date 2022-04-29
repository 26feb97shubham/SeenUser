package com.seen.user.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.seen.user.R
import com.seen.user.adapter.GridFilteredProductsListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Products
import com.seen.user.model.ProductsItem
import com.seen.user.model.SearchFilterResponse
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import com.seen.user.utils.Utility.Companion.apiInterface
import com.seen.user.utils.Utility.Companion.price_category
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.iv_filter
import kotlinx.android.synthetic.main.fragment_home2.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilteredProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView:View?=null
    lateinit var gridFilteredProductsListAdapter: GridFilteredProductsListAdapter
    private var productsList : ArrayList<Products>?=null
    private var queryMap = HashMap<String, String>()
    private var search_keyword : String = ""
    var country_id = ""
    var price_cat = ""

    var product_item_id:String=""
    var already_added:Boolean=false


    var add_cart_type:String=""


    var productPrice:String=""
    lateinit var attrArrayData: JSONArray
    var attrData: JSONArray = JSONArray()
    var attributeObj=JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           if (it!=null){
               country_id = it.getInt("country_id").toString()
               productsList = it.getSerializable("productsItemList") as? ArrayList<Products>
               price_cat = it.getString("price_cat").toString()
           }else{
               country_id = ""
               price_cat = ""
               productsList = ArrayList<Products>()
           }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filtered_products, container, false)
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

        if(productsList!=null){
            if(productsList?.size==0){
                mView!!.tv_no_product.visibility = View.VISIBLE
                mView!!.scroll_view_filtered_products_frag.visibility = View.GONE
            }else{
                mView!!.tv_no_product.visibility = View.GONE
                mView!!.scroll_view_filtered_products_frag.visibility = View.VISIBLE
                setProducts(productsList!!)
            }
        }else{
            mView!!.tv_no_product.visibility = View.VISIBLE
            mView!!.scroll_view_filtered_products_frag.visibility = View.GONE
        }



        if (!price_cat.equals("")){
            mView!!.tv_filterSearch.text = price_cat
            price_cat = ""
            Utility.price_category = ""
        }else{
            mView!!.tv_filterSearch.text = ""
        }

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
                        productsList = response.body()!!.products as ArrayList<Products>
                        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.searchItem, "")
                        if (!price_cat.equals("")){
                            mView!!.tv_filterSearch.text = price_cat
                            price_cat = ""
                            price_category = ""
                        }else{
                            mView!!.tv_filterSearch.text = ""
                        }

                        if(productsList?.size==0){
                            mView!!.tv_no_product.visibility = View.VISIBLE
                            mView!!.scroll_view_filtered_products_frag.visibility = View.GONE
                        }else{
                            mView!!.tv_no_product.visibility = View.GONE
                            mView!!.scroll_view_filtered_products_frag.visibility = View.VISIBLE
                            setProducts(productsList!!)
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

    private fun setProducts(productsList: ArrayList<Products>) {
        Log.e("Products_list", productsList.toString())
        mView!!.rv_filtered_products.layoutManager = GridLayoutManager(requireContext(), 3)
        gridFilteredProductsListAdapter = GridFilteredProductsListAdapter(requireContext(),
                productsList,findNavController(), object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int,type: String) {
                Log.e("Position_1", pos.toString())

/*                Log.e("Position_1", pos.toString())
                if(type=="Cart"){
                    val attrbts = productsList[pos].attributes
                    val jsonArray = JSONArray(productsList[pos].attributes)
                    val price  = productsList[pos].price
                    for (i in 0 until jsonArray.length()){
                       productPrice = JSONObject(jsonArray[i].toString()).getString("price")
                        attrArrayData = JSONObject(jsonArray[i].toString()).getJSONArray("data")
                        if (productPrice.equals(price)){
                            for (i in 0 until attrArrayData.length()){
                                val attribute_Obj = attrArrayData.getJSONObject(i)
                                val obj1=JSONObject()
                                obj1.put("id", attribute_Obj.getInt("id"))
                                obj1.put("name", attribute_Obj.getString("name"))
                                obj1.put("type", attribute_Obj.getString("type"))
//                                obj1.put("value",JSONArray(attribute_Obj.getString("value")) )
                                obj1.put("primary", 1)
                                attrData.put(0, obj1)
                                attributeObj.put("data", attrData)
                                attributeObj.put("itemAdd", true)
                            }
                            break
                        }else{
                            Log.e("err", "err")
                        }

                        if(attrArrayData.length()==2){
                            checkProductPrice(productsList[pos].id!!, productsList[pos].supplier_id)
                        }
                    }
                }*/



                if(type=="Cart"){
                    val attrbts = productsList[pos].attributes
                    val jsonArray = JSONArray(productsList[pos].attributes)
                    val price  = productsList[pos].price
                    for (i in 0 until jsonArray.length()){
                        productPrice = JSONObject(jsonArray[i].toString()).getString("price")
                        attrArrayData = JSONObject(jsonArray[i].toString()).getJSONArray("data")
                        if (productPrice.equals(price)){
                            for (i in 0 until attrArrayData.length()){
                                val attribute_Obj = attrArrayData.getJSONObject(i)
                                val obj1=JSONObject()
                                obj1.put("id", attribute_Obj.getInt("id"))
                                obj1.put("name", attribute_Obj.getString("name"))
                                obj1.put("type", attribute_Obj.getString("type"))
//                                obj1.put("value",JSONArray(attribute_Obj.getString("value")) )
                                obj1.put("primary", 1)
                                attrData.put(0, obj1)
                                attributeObj.put("data", attrData)
                                attributeObj.put("itemAdd", true)
                            }
                            break
                        }else{
                            Log.e("err", "err")
                        }

                        if(attrArrayData.length()==2){
                            checkProductPrice(productsList[pos].id!!, productsList[pos].supplierId!!)
                        }
                    }
//                    JSONObject((JSONArray(productsList[pos].attributes)[0] as JSONObject).toString()).getString("price")
//                    val attrArray = JSONArray(attrbts)
//                   productDetailPage(productsList[pos].id)
                }else if(type == "Supplier"){
                    val bundle = Bundle()
                    bundle.putInt("supplier_user_id", productsList[pos].supplierId!!)
                    findNavController().navigate(R.id.supplierDetailsFragment, bundle)
                }







            }

        })
        mView!!.rv_filtered_products.adapter = gridFilteredProductsListAdapter
    }



    private fun checkProductPrice(productId: Int, supplierId: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "device_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                productId.toString(),
                attrArrayData.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.checkProductPrice(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){

                            product_item_id=jsonObject.getString("product_item_id")
                            if(TextUtils.isEmpty(product_item_id)){
                                LogUtils.shortToast(requireContext(), getString(R.string.this_item_is_currently_out_of_stock))
                            }
                            already_added=jsonObject.getBoolean("already_added")
                            if(already_added){
                                LogUtils.shortToast(requireContext(), getString(R.string.go_to_cart))
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setTitle(getString(R.string.alert_i))
                                builder.setMessage(getString(R.string.item_already_added))
                                builder.setPositiveButton(R.string.yes) { dialog, which ->
                                    dialog.cancel()
                                    add_cart_type="1"
                                    validateAndCartAdd(product_item_id, productId, supplierId)
                                }
                                builder.setNegativeButton(R.string.no) { dialog, which ->
                                    dialog.cancel()
                                }
                                builder.show()
                            }else{
                                validateAndCartAdd(product_item_id, productId, supplierId)
                            }
                        }

                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    private fun validateAndCartAdd(product_item_id: String, productId: Int, supplierId: Int) {
        if(TextUtils.isEmpty(product_item_id)){
            LogUtils.shortToast(requireContext(), getString(R.string.this_item_is_currently_out_of_stock))
        }
        else{
            cartAdd(product_item_id, productId, supplierId)
        }
    }


    private fun cartAdd(product_item_id: String, productId: Int, supplierId:Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("product_id", "product_item_id", "type", "quantity", "product_type", "cart_id", "device_id", "user_id", "add_cart_type", "supplier_id", "lang"),
            arrayOf(productId.toString(),
                product_item_id, "1", "1", "" , ""
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                ,add_cart_type, supplierId.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.cartAdd(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            add_cart_type=""
                            if(jsonObject.getInt("carts_count")!=0){
                                requireActivity().cartWedgeCount.visibility=View.VISIBLE
                                requireActivity().frag_other_cartWedgeCount.visibility=View.VISIBLE
                                requireActivity().cartWedgeCount.text=jsonObject.getInt("carts_count").toString()
                                requireActivity().frag_other_cartWedgeCount.text=jsonObject.getInt("carts_count").toString()
                            }
                            else{
                                requireActivity().cartWedgeCount.visibility=View.GONE
                                requireActivity().frag_other_cartWedgeCount.visibility=View.GONE
                            }
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            already_added=true
                        }
                        else if (jsonObject.getInt("response") == 2) {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle(getString(R.string.alert_i))
                            builder.setMessage(jsonObject.getString("message"))
                            builder.setPositiveButton(R.string.yes) { dialog, which ->
                                dialog.cancel()
                                add_cart_type="1"
                                validateAndCartAdd(product_item_id, productId, supplierId)
                            }
                            builder.setNegativeButton(R.string.no) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        }

                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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