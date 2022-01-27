package com.seen.user.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.seen.user.R
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductDetailsFragment : Fragment() {
    lateinit var mView: View
    var attributeObj=JSONObject()
    var attrList=ArrayList<Attributes>()
    lateinit var attributesAdapter: AttributesAdapter
    var product_id:Int=0
    var product_item_id:String=""
    var productFiles=ArrayList<String>()
    var pagerAdapter: ScreenSlidePagerAdapter?= null
    lateinit var productImageAdapter: ProductImageAdapter
    var similarItemsList = ArrayList<ProductsItemX>()
    lateinit var similarItemsAdapter: SimilarItemsAdapter
    var data=JSONArray()
    var type:String="1"
    var qty:Int=1
    var already_added:Boolean=false
    var add_cart_type:String=""
    var supplier_id:Int=0
    var category_id:Int=0
    private var queryMap = HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product_id = it.getInt("product_id", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_product_details, container, false)
        requireActivity().home_frag_categories.visibility=View.GONE
        setUpViews()
        productDetailPage()
        return mView
    }

    private fun getSimilaritems(category_id: Int) {
        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("category_id", category_id.toString())
        queryMap.put("account_type", "")
        defaultProductList(queryMap)
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
                            LogUtils.shortToast(requireContext(), getString(R.string.no_results_found))
                        }else{
                            similarItemsList = response.body()!!.products as ArrayList<ProductsItemX>
                            Log.e("Products_list", similarItemsList.toString())
                            mView.rv_similarItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            similarItemsAdapter = SimilarItemsAdapter(requireContext(),
                                similarItemsList,findNavController(), object : ClickInterface.ClickPosInterface{
                                    override fun clickPostion(pos: Int,type: String) {
                                        Log.e("Position_1", pos.toString())
                                    }

                                })
                            mView.rv_similarItems.adapter = similarItemsAdapter
                            similarItemsAdapter.notifyDataSetChanged()
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

    private fun setUpViews() {

        similarItemsAdapter = SimilarItemsAdapter(
            requireContext(),
            similarItemsList,
            findNavController(),
            object : ClickInterface.ClickPosInterface{
                override fun clickPostion(pos: Int,type: String) {
                    Log.e("Position_1", pos.toString())
                }

            })
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

     /*   pagerAdapter = ScreenSlidePagerAdapter(this)
        mView.viewPager2.adapter = pagerAdapter
        TabLayoutMediator(mView.tabLayout,   mView.viewPager2){ tab, position ->

        }.attach()*/

        productImageAdapter = ProductImageAdapter(requireContext(),productFiles)
        mView.rvProductImageList.adapter = productImageAdapter
        mView.pageIndicator.attachTo(mView.rvProductImageList)
        productImageAdapter.notifyDataSetChanged()


        mView.btnAddToCart.setOnClickListener {
            mView.btnAddToCart.startAnimation(AlphaAnimation(1f, .5f))
            if(already_added){
                goToCart()
            }
            else{
                validateAndCartAdd()
            }

        }

        /*SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.PrimaryAdapterPos)
        SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.SecondaryAdapterPos)
        SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.TertiaryAdapterPos)*/

        mView.rvList_attributes.layoutManager=LinearLayoutManager(requireContext())
        attributesAdapter= AttributesAdapter(requireContext(), attrList, object : ClickInterface.ClickJSonObjInterface{
            override fun clickJSonObj(obj: JSONObject) {
                attributeObj=obj
                Log.e("attributeObj", attributeObj.toString())
                if(attributeObj.getJSONArray("data").length()==2){
                    checkProductPrice()
                }

                /*if(attributeObj.getBoolean("itemAdd")){
                    checkProductPrice()
                }*/
               /* else{
                    checkProductPrice()
                }*/

            }


        })
        mView.rvList_attributes.adapter=attributesAdapter

        AttributesAdapter.attrData= JSONArray()

        mView.rv_similarItems.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mView.rv_similarItems.adapter =similarItemsAdapter
        similarItemsAdapter.notifyDataSetChanged()

        setProductDetails()
    }

    private fun setProductDetails() {
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mView!!.rvProductImageList.layoutManager= linearLayoutManager
       productImageAdapter = ProductImageAdapter(requireContext(), productFiles)

       /* mView!!.rvProductImageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mView!!.swipeRefresh.isEnabled =
                    true
                if (newState == RecyclerView.SCROLL_STATE_IDLE) mView!!.swipeRefresh.isEnabled =
                    false
            }
        })*/

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
        //requireActivity().itemCart.setImageResource(R.drawable.shopping_cart_active)
        findNavController().navigate(R.id.action_productDetailsFragment_to_cartFragment)
    }
    private fun setBottomView() {
        requireActivity().itemDiscount.setImageResource(R.drawable.offers_and_discounts_icon)
        //requireActivity().itemCart.setImageResource(R.drawable.add_to_basket_icon)
        requireActivity().itemHome.setImageResource(R.drawable.home_icon)
//        requireActivity().itemSearch.setImageResource(R.drawable.search)
       /* requireActivity().itemProfile.setImageResource(R.drawable.profile)
        requireActivity().itemHotDeals.setImageResource(R.drawable.hot_deals)*/

//        setHostFragment()

    }
    private fun validateAndCartAdd() {
        if(TextUtils.isEmpty(product_item_id)){
            LogUtils.shortToast(requireContext(), getString(R.string.this_item_is_currently_out_of_stock))
        }
        else{
            cartAdd()
        }
    }

    private fun productDetailPage() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//        mView.progressBar.visibility= View.VISIBLE
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
//                mView.progressBar.visibility= View.GONE
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmer()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
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

                            mView.txtProductRating.text=product.getDouble("product_rating").toString()
                            mView.productRatingBar.rating=product.getDouble("product_rating").toFloat()
                            mView.productCategory.text=product.getString("category_name")
                            //mView.supplierName.text=product.getString("supplier_name")
                            mView.supplierName2.text=product.getString("supplier_name")
                            mView.category.text=product.getString("categories")
                            mView.txtSupplierRating.text=product.getDouble("supplier_rating").toString()
                            mView.supplierRatingBar.rating=product.getDouble("supplier_rating").toFloat()
                            Glide.with(requireContext()).load(product.getString("supplier_profile_picture")).placeholder(R.drawable.default_icon).into(mView.supplierImg)
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
                                a.type =obj.getString("type")
                                a.value = obj.getJSONArray("value")
                                attrList.add(a)
                            }

                            Log.e("Size : ", ""+attrList.size)

                            mView.rvList_attributes.layoutManager=LinearLayoutManager(requireContext())
                            attributesAdapter= AttributesAdapter(requireContext(), attrList, object : ClickInterface.ClickJSonObjInterface{
                                override fun clickJSonObj(obj: JSONObject) {
                                    attributeObj=obj
                                    Log.e("attributeObj", attributeObj.toString())
                                    if(attributeObj.getJSONArray("data").length()==2){
                                        checkProductPrice()
                                    }

                            /*        if(attributeObj.getBoolean("itemAdd")){
                                        checkProductPrice()
                                    }
                                     else{
                                         checkProductPrice()
                                     }*/

                                }


                            })
                            getSimilaritems(category_id)
                            mView.rvList_attributes.adapter=attributesAdapter
                            attributesAdapter.notifyDataSetChanged()
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
//                mView.progressBar.visibility= View.GONE
                mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmer()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })


    }
    private fun checkProductAvailable() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), product_id.toString(), attributeObj.getJSONArray("data").toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


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
                            product_item_id= jsonObject.getInt("product_item_id").toString()
                            val result=jsonObject.getJSONArray("result")
                            attrList.clear()
                            AttributesAdapter.attrData= JSONArray()
                            for (m in 0 until result.length()) {
                                val obj = result.getJSONObject(m)
                                val a = Attributes()
                                a.id=obj.getInt("id")
                                a.name = obj.getString("name")
                                a.type =obj.getString("type")
                                a.value = obj.getJSONArray("value")
                                attrList.add(a)
                            }
                            attributesAdapter.notifyDataSetChanged()


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
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })


    }

    private fun checkProductPrice() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE
       /* mView.mainView.visibility=View.GONE
        mView.shimmerLayout.visibility=View.VISIBLE
        mView.shimmerLayout.startShimmerAnimation()*/

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "device_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), product_id.toString(), attributeObj.getJSONArray("data").toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.checkProductPrice(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
               /* mView.mainView.visibility=View.VISIBLE
                mView.shimmerLayout.visibility=View.GONE
                mView.shimmerLayout.stopShimmerAnimation()*/

                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            if(jsonObject.getString("price").equals("")){
                                mView.productPrice.text="AED "+0
                            }else{
                                mView.productPrice.text="AED "+jsonObject.getString("price")
                            }

                            product_item_id=jsonObject.getString("product_item_id")
                            if(TextUtils.isEmpty(product_item_id)){
                                LogUtils.shortToast(requireContext(), getString(R.string.this_item_is_currently_out_of_stock))
                                mView.yes.isSelected=false
                                mView.no.isSelected=true
                            }
                            else{
                                mView.yes.isSelected=true
                                mView.no.isSelected=false
                            }
                            already_added=jsonObject.getBoolean("already_added")
                            if(already_added){
                                mView.btnAddToCart.text=getString(R.string.go_to_cart)
                            }
                            else{
                                mView.btnAddToCart.text=getString(R.string.add_to_cart)
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
                mView.progressBar.visibility= View.GONE
//                mView.mainView.visibility=View.VISIBLE
//                mView.shimmerLayout.visibility=View.GONE
//                mView.shimmerLayout.stopShimmerAnimation()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("product_id", "product_item_id", "type", "quantity", "product_type", "cart_id", "device_id", "user_id", "add_cart_type", "supplier_id", "lang"),
            arrayOf(product_id.toString(), product_item_id, type, qty.toString(), "" , ""
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                   ,add_cart_type, supplier_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.cartAdd(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
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
                            mView.btnAddToCart.text=getString(R.string.go_to_cart)
                        }
                        else if (jsonObject.getInt("response") == 2) {
                            val builder = AlertDialog.Builder(requireContext())
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
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onResume() {
        super.onResume()
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProductDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}