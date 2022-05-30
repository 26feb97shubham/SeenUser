package com.seen.user.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.activity.LoginActivity
import com.seen.user.adapter.AttributesAdapter
import com.seen.user.adapter.ProductImageAdapter
import com.seen.user.adapter.SimilarItemsAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Attributes
import com.seen.user.model.GetProductsResponse
import com.seen.user.model.ProductsItemX
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_product_details.view.*
import kotlinx.android.synthetic.main.fragment_product_details.view.mainView
import kotlinx.android.synthetic.main.fragment_product_details.view.pageIndicator
import kotlinx.android.synthetic.main.fragment_product_details.view.shimmerLayout
import kotlinx.android.synthetic.main.supplier_profile_fragment_toolbar.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ProductDetailsFragment : Fragment() {
    lateinit var mView: View
    var attributeObj=JSONObject()
    var attrList=ArrayList<Attributes>()
    lateinit var attributesAdapter: AttributesAdapter
    var product_id:Int=0
    var product_item_id:String=""
    var my_product_item_id:String=""
    var productFiles=ArrayList<String>()
    var pagerAdapter: ScreenSlidePagerAdapter?= null
    lateinit var productImageAdapter: ProductImageAdapter
    var similarItemsList = ArrayList<ProductsItemX>()
    lateinit var similarItemsAdapter: SimilarItemsAdapter
    var data=JSONArray()
    var type:String="1"
    var qty:Int=1
    var already_added:Boolean=false
    var add_cart_type:String="1"
    var supplier_id:Int=0
    var category_id:Int=0
    private var myAttributeDataJSONObject:JSONObject?=null

    var productPrice:String=""
    lateinit var attrArrayData: JSONArray
    var attrData: JSONArray = JSONArray()

    private var isDataLoaded = false

    private var myLength = ""
    private var myWidth = ""
    private var myHeight = ""
    private var myWeight = ""

    lateinit var mContext: Context
    private var queryMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product_id = it.getInt("product_id", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mView = inflater.inflate(R.layout.fragment_product_details, container, false)
        mContext = requireContext()
        Utility.changeLanguage(
            mContext,
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        requireActivity().home_frag_categories.visibility=View.GONE
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        productDetailPage()
    }

    private fun getSimilaritems(category_id: Int) {
        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("category_id", "")
        queryMap.put("account_type", "")
        defaultProductList(queryMap)
    }

    private fun defaultProductList(queryMap: HashMap<String, String>) {
        val builder = ApiClient.createBuilder(arrayOf("user_id", "category_id", "account_type"),
            arrayOf(queryMap.get("user_id").toString(),
                queryMap.get("category_id").toString(),
                queryMap.get("account_type").toString()))
        val call = Utility.apiInterface.getProductsCategories(builder.build())
        var navController : NavController?=null
        var abc = lifecycleScope.launchWhenResumed {
            navController = findNavController()
        }
        call?.enqueue(object : Callback<GetProductsResponse?> {
            override fun onResponse(call: Call<GetProductsResponse?>, response: Response<GetProductsResponse?>) {
                if (response.isSuccessful){
                    if (response.body()!=null){
                        if (response.body()!!.products==null){
                            LogUtils.shortToast(mContext, getString(R.string.no_results_found))
                        }else{
                            similarItemsList = response.body()!!.products as ArrayList<ProductsItemX>
                            Log.e("Products_list", similarItemsList.toString())
                            mView.rv_similarItems.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                            similarItemsAdapter = SimilarItemsAdapter(mContext,
                                similarItemsList,
                                navController!!,
                                object : ClickInterface.ClickPosInterface{
                                    override fun clickPostion(pos: Int, type: String) {
                                        Log.e("Position_1", pos.toString())
                                        if (type == "Cart") {
                                            val jsonArray =
                                                JSONArray(similarItemsList[pos].attributes)
                                            val price = similarItemsList[pos].price
                                            for (i in 0 until jsonArray.length()) {
                                                productPrice =
                                                    JSONObject(jsonArray[i].toString()).getString(
                                                        "price"
                                                    )
                                                attrArrayData =
                                                    JSONObject(jsonArray[i].toString()).getJSONArray(
                                                        "data"
                                                    )
                                                if (productPrice.equals(price)) {
                                                    for (i in 0 until attrArrayData.length()) {
                                                        val attribute_Obj =
                                                            attrArrayData.getJSONObject(i)
                                                        val obj1 = JSONObject()
                                                        obj1.put(
                                                            "id",
                                                            attribute_Obj.getInt("id")
                                                        )
                                                        obj1.put(
                                                            "name",
                                                            attribute_Obj.getString("name")
                                                        )
                                                        obj1.put(
                                                            "name_ar",
                                                            attribute_Obj.getString("name_ar")
                                                        )
                                                        obj1.put(
                                                            "type",
                                                            attribute_Obj.getString("type")
                                                        )
                                                        obj1.put("primary", 1)
                                                        attrData.put(0, obj1)
                                                        attributeObj.put("data", attrData)
                                                        attributeObj.put("itemAdd", true)
                                                    }
                                                    break
                                                } else {
                                                    Log.e("err", "err")
                                                }
                                            }
                                            //checkProductPrice()
                                        } else if (type == "Supplier") {
                                            val bundle = Bundle()
                                            bundle.putInt(
                                                "supplier_user_id",
                                                similarItemsList[pos].user_id!!
                                            )
                                            findNavController().navigate(
                                                R.id.supplierDetailsFragment,
                                                bundle
                                            )
                                        }
                                    }

                                })
                            mView.rv_similarItems.adapter = similarItemsAdapter
                            similarItemsAdapter.notifyDataSetChanged()
                        }
                    }
                }else {
                    LogUtils.shortCenterToast(mContext, getString(R.string.no_results_found))
                }
            }

            override fun onFailure(call: Call<GetProductsResponse?>, t: Throwable) {
                LogUtils.shortToast(mContext, t.message)
            }

        })
    }


    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(mContext, requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }


        productImageAdapter = ProductImageAdapter(mContext,productFiles)
        mView.rvProductImageList.adapter = productImageAdapter
        mView.pageIndicator.attachTo(mView.rvProductImageList)
        productImageAdapter.notifyDataSetChanged()


        mView.btnAddToCart.setOnClickListener {
            mView.btnAddToCart.startAnimation(AlphaAnimation(1f, .5f))
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                requireContext().startActivity(Intent(requireContext(), LoginActivity::class.java).putExtras(args))
            }else{
                if(already_added){
                    goToCart()
                }
                else{
                    cartAdd()
                }
            }
        }
        setProductDetails()
    }

    private fun setProductDetails() {
        val linearLayoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mView!!.rvProductImageList.layoutManager= linearLayoutManager
        productImageAdapter = ProductImageAdapter(mContext, productFiles)

        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(mView!!.rvProductImageList)

        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition()<(productImageAdapter.itemCount) - 1){
                    linearLayoutManager.smoothScrollToPosition(mView!!.rvProductImageList, RecyclerView.State(),
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1)
                }else {
                    linearLayoutManager.smoothScrollToPosition(mView!!.rvProductImageList, RecyclerView.State(), 0)
                }
            }

        }, 0, 5000)
    }

    private fun goToCart() {
        setBottomView()
        findNavController().navigate(R.id.action_productDetailsFragment_to_cartFragment)
    }
    private fun setBottomView() {
        requireActivity().itemDiscount.setImageResource(R.drawable.offers_and_discounts_icon)
        requireActivity().itemHome.setImageResource(R.drawable.home_icon)

    }
    private fun validateAndCartAdd() {
        if(TextUtils.isEmpty(product_item_id)){
            LogUtils.shortToast(mContext, getString(R.string.this_item_is_currently_out_of_stock))
        }
        else{
            cartAdd()
        }
    }

    private fun productDetailPage() {
        mView.mainView.visibility=View.GONE
        mView.shimmerLayout.visibility=View.VISIBLE
        mView.shimmerLayout.startShimmer()

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id",  "device_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), product_id.toString()
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.productDetailPage(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmer()
                try {
                    if (response.body() != null) {
                        isDataLoaded = true
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            val product=jsonObject.getJSONObject("product")
                            mView.btnAddToCart.text=getString(R.string.add_to_cart)
                            supplier_id=product.getInt("user_id")
                            mView.productName.text=product.getString("name")
                            category_id = product.getInt("category_id")
                            if (product.getString("price").equals("")){
                                mView.productPrice.text="AED "+0
                            }else{
                                mView.productPrice.text="AED "+product.getString("price")
                            }

                            myLength = product.getString("length")
                            myWidth = product.getString("width")
                            myHeight = product.getString("height")
                            myWeight = product.getString("weight")

                            mView.txtProductRating.text=product.getDouble("product_rating").toString()
                            mView.productRatingBar.rating=product.getDouble("product_rating").toFloat()
                            mView.productCategory.text=product.getString("category_name")
                            //mView.supplierName.text=product.getString("supplier_name")
                            mView.supplierName2.text=product.getString("supplier_name")
                            if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                                mView.category.text=product.getString("categories_ar")
                            }else{
                                mView.category.text=product.getString("categories")
                            }


                            mView.txtSupplierRating.text=product.getDouble("supplier_rating").toString()
                            mView.supplierRatingBar.rating=product.getDouble("supplier_rating").toFloat()
                            Glide.with(mContext).load(product.getString("supplier_profile_picture")).placeholder(R.drawable.default_icon).into(mView.supplierImg)

                            mView.view.setOnClickListener(object : View.OnClickListener{
                                override fun onClick(p0: View?) {
                                    val bundle = Bundle()
                                    bundle.putInt("supplier_user_id", product.getInt("supplier_id"))
                                    findNavController().navigate(R.id.supplierDetailsFragment, bundle)
                                }

                            })
                            mView.productDesc.text=product.getString("description")
                            val all_files=product.getJSONArray("all_files")
                            productFiles.clear()
                            for(i in 0 until all_files.length()){
                                productFiles.add(all_files.getString(i))
                            }
                            pagerAdapter = ScreenSlidePagerAdapter(requireParentFragment())
                            pagerAdapter!!.notifyDataSetChanged()

                            val attributes=product.getJSONArray("attributes")
                            attrList.clear()
                            for (m in 0 until attributes.length()) {
                                val obj = attributes.getJSONObject(m)
                                val a = Attributes()
                                a.id=obj.getInt("id")
                                a.name = obj.getString("name")
                                a.name_ar = obj.getString("name_ar")
                                a.type =obj.getString("type")
                                a.value = obj.getJSONArray("value")
                                attrList.add(a)
                            }

                            Log.e("Size : ", ""+attrList.size)
                            mView.rvList_attributes.layoutManager=LinearLayoutManager(mContext)
                            attributesAdapter= AttributesAdapter(mContext, attrList, object : ClickInterface.ClickJSonObjInterface{
                                override fun clickJSonObj(obj: JSONObject) {
                                    attributeObj=obj
                                    checkProductAvailable(attributeObj.getJSONArray("data"), attributeObj)
                                }
                            })
                            mView.rvList_attributes.adapter=attributesAdapter
                            attributesAdapter.notifyDataSetChanged()

                            getSimilaritems(category_id)
                        }

                        else {
                            LogUtils.shortToast(mContext, jsonObject.getString("message"))
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
                isDataLoaded = true
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(mContext, getString(R.string.check_internet))
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmer()
            }
        })


    }
    private fun checkProductAvailable(dataJsonArray: JSONArray, attributeObj: JSONObject) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                product_id.toString(), dataJsonArray.toString(),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.checkProductAvailable(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            mView.productPrice.text="AED "+jsonObject.getString("price")
                            product_item_id= jsonObject.getString("product_item_id").toString()
                            if (product_item_id.isEmpty()){
                                // LogUtils.shortToast(mContext, getString(R.string.this_item_is_currently_out_of_stock))
                                mView.yes.isSelected=false
                                mView.no.isSelected=true
                                mView.btnAddToCart.isEnabled = false
                            }else{
                                mView.yes.isSelected=true
                                mView.no.isSelected=false
                                mView.btnAddToCart.isEnabled = true
                                checkProductPrice(attributeObj, product_item_id)
                            }
                        }

                        else {
                            LogUtils.shortToast(mContext, jsonObject.getString("message"))
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
                LogUtils.shortToast(mContext, getString(R.string.check_internet))
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })


    }

    private fun checkProductPrice(attributeDataJsonObject: JSONObject, product_item_id: String) {
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "device_id", "lang", "product_item_id"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                product_id.toString(),
                attributeDataJsonObject.getJSONArray("data1").toString(),
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString(),
                product_item_id
            ))


        val call = apiInterface.checkProductPrice(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            if(jsonObject.getString("price").equals("")){
                                mView.productPrice.text="AED "+0
                            }else{
                                mView.productPrice.text="AED "+jsonObject.getString("price")
                            }

                            already_added=jsonObject.getBoolean("already_added")
                            myAttributeDataJSONObject = attributeDataJsonObject
                            my_product_item_id = jsonObject.getString("product_item_id")
                            if(already_added){
                                mView.btnAddToCart.text=getString(R.string.go_to_cart)
                            }
                            else{
                                mView.btnAddToCart.text=getString(R.string.add_to_cart)
                            }
                        }
                        else {
                            LogUtils.shortToast(mContext, jsonObject.getString("message"))
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
                LogUtils.shortToast(mContext, getString(R.string.check_internet))
                mView.progressBar.visibility= View.GONE
            }
        })


    }

    inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int{
            return productFiles.size
        }

        override fun createFragment(position: Int): Fragment {

            val fragment= ProductViewPagerFragment(productFiles[position])
            return fragment
        }
    }
    private fun cartAdd() {
        mView.progressBar.visibility= View.VISIBLE
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("product_id", "product_item_id", "type", "quantity", "product_type", "cart_id", "device_id", "user_id", "add_cart_type", "supplier_id", "lang"),
            arrayOf(product_id.toString(), my_product_item_id, "1", "1", "1" , ""
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                ,add_cart_type, supplier_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.cartAdd(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
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
                            LogUtils.shortToast(mContext, jsonObject.getString("message"))
                            already_added=true
                            mView.btnAddToCart.text=getString(R.string.go_to_cart)
                        }
                        else if (jsonObject.getInt("response") == 2) {
                            val builder = AlertDialog.Builder(mContext)
                            builder.setTitle(getString(R.string.alert_i))
                            builder.setMessage(jsonObject.getString("message"))
                            builder.setPositiveButton(R.string.yes) { dialog, which ->
                                dialog.cancel()
                                add_cart_type="1"
                                cartAdd()
                            }
                            builder.setNegativeButton(R.string.no) { dialog, which ->
                                dialog.cancel()
                            }
                            builder.show()
                        }

                        else {
                            LogUtils.shortToast(mContext, jsonObject.getString("message"))
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
                LogUtils.shortToast(mContext, getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
            }
        })

    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            mContext,
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