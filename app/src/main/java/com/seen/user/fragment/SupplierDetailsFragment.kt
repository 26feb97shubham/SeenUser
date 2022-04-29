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
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.adapter.*
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.*
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home2.view.*
import kotlinx.android.synthetic.main.fragment_supplier_details.view.*
import kotlinx.android.synthetic.main.supplier_profile_fragment_toolbar.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class SupplierDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var mView: View
    var supplier_user_id:Int=0
    lateinit var productAdapter: ProductAdapter
    lateinit var productCategoryAdapter: ProductCategoryAdapter
    lateinit var supplierDetailsProductsAdapter: SupplierDetailsProductsAdapter
   /* lateinit var serveCountriesAdapter: ServeCountriesAdapter*/
    var productList=ArrayList<ProductsItemZ>()
    var catList=ArrayList<Categories>()
    var isLike:Boolean=false
    var categories=JSONArray()
    var profile = ProfileItemX()
    var catNameList=ArrayList<String>()
    var categoryList=ArrayList<CategoriesItemX>()
    lateinit var categoryListAdapter: CategoryNameListAdapter
    lateinit var categoryAdapter: CategoriesAdapter
    lateinit var supplierCategoryAdapter: SupplierCategoriesAdapter
    var allCatList=ArrayList<CategoriesItem>()
    /*var serveCountriesList=ArrayList<ServeCountries>()*/

    var productPrice:String=""
    lateinit var attrArrayData: JSONArray
    var attrData: JSONArray = JSONArray()
    var attributeObj=JSONObject()


    var product_item_id:String=""
    var already_added:Boolean=false
    var add_cart_type:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            supplier_user_id = it.getInt("supplier_user_id", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_supplier_details, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        supplierDetails(false)
        //clickOnDrawer()
        return mView
    }

    private fun openCloseDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility= View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        requireActivity().frag_other_toolbar.visibility = View.GONE

        requireActivity().supplier_fragment_toolbar.frag_profile_backImg.setOnClickListener {
            requireActivity().supplier_fragment_toolbar.frag_profile_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().supplier_fragment_toolbar.frag_profile_backImg)
            findNavController().popBackStack()
        }


        mView.rvCategories.layoutManager= GridLayoutManager(requireContext(), 4)
        productCategoryAdapter= ProductCategoryAdapter(requireContext(), catList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {

            }

        })

        mView.swipeRefresh.setOnRefreshListener {
            supplierDetails(true)
        }

        mView.rvCategories.adapter=productCategoryAdapter

        mView.viewAllProducts.setOnClickListener {
            mView.viewAllProducts.startAnimation(AlphaAnimation(1f, .5f))
            val bundle=Bundle()
            bundle.putInt("supplier_user_id", supplier_user_id)
//            bundle.putString("categories", "")
            findNavController().navigate(R.id.action_supplierDetailsFragment_to_productsFragment, bundle)
        }

        mView.viewAllCat.setOnClickListener {
            mView.viewAllCat.startAnimation(AlphaAnimation(1f, .5f))
            val bundle=Bundle()
            bundle.putString("categories", categories.toString())
            bundle.putInt("supplier_user_id", supplier_user_id)
            findNavController().navigate(R.id.action_supplierDetailsFragment_to_categoriesFragment, bundle)

        }

/*        mView.imgLike.setOnClickListener {
            mView.imgLike.startAnimation(AlphaAnimation(1f, .5f))
            likeUnlike()
        }*/



    }

  /*  private fun clickOnDrawer() {
        requireActivity().userIcon.setOnClickListener {
            requireActivity().userIcon.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else{
                findNavController().navigate(R.id.homeFragment)
            }
        }
        requireActivity().name.setOnClickListener {
            requireActivity().name.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else{
                findNavController().navigate(R.id.homeFragment)
            }

        }

        requireActivity().llLanguage.setOnClickListener {
            requireActivity().llLanguage.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.selectLangFragment)
        }

        requireActivity().llContactUs.setOnClickListener {
            requireActivity().llContactUs.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.contactUSFragment)
        }

        requireActivity().llGallery.setOnClickListener {
            requireActivity().llGallery.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.galleryFragment)
        }
        requireActivity().llCategory.setOnClickListener {
            requireActivity().llCategory.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.categoriesFragment)
        }
        requireActivity().llMyCards.setOnClickListener {
            requireActivity().llMyCards.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else {
                findNavController().navigate(R.id.myCardsFragment)
            }
        }
        requireActivity().llMyLocations.setOnClickListener {
            requireActivity().llMyLocations.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else {
                findNavController().navigate(R.id.myLocationFragment)
            }
        }
        requireActivity().llMyOrders.setOnClickListener {
            requireActivity().llMyOrders.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else {
                findNavController().navigate(R.id.myOrdersFragment)
            }
        }
        requireActivity().llFavourites.setOnClickListener {
            requireActivity().llFavourites.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)

            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else{
                findNavController().navigate(R.id.favouritesFragment)
            }
        }
        requireActivity().llNotificationSettings.setOnClickListener {
            requireActivity().llNotificationSettings.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
                val args=Bundle()
                args.putString("reference", "Home")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            else {
                findNavController().navigate(R.id.notificationSettingsFragment)
            }
        }
        requireActivity().llOffersDiscounts.setOnClickListener {
            requireActivity().llOffersDiscounts.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(findNavController().currentDestination?.id != R.id.discountFragment){
                requireActivity().itemDiscount.setImageResource(R.drawable.selected_offers_and_discounts)
                findNavController().navigate(R.id.discountFragment)
            }
        }
        requireActivity().llAboutUs.setOnClickListener {
            requireActivity().llAboutUs.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            val args=Bundle()
            args.putString("title", getString(R.string.about_us))
            findNavController().navigate(R.id.cmsFragment, args)
        }
        requireActivity().llFAQ.setOnClickListener {
            requireActivity().llFAQ.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            *//* val args=Bundle()
             args.putString("title", getString(R.string.faq))
             findNavController().navigate(R.id.webViewFragment, args)*//*
            findNavController().navigate(R.id.faqFragment)
        }
        requireActivity().llPrivacyPolicy.setOnClickListener {
            requireActivity().llPrivacyPolicy.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            val args=Bundle()
            args.putString("title", getString(R.string.privacy_and_policy))
            findNavController().navigate(R.id.cmsFragment, args)
        }
        requireActivity().llTermsConditions.setOnClickListener {
            requireActivity().llTermsConditions.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            val args=Bundle()
            args.putString("title", getString(R.string.terms_amp_conditions))
            findNavController().navigate(R.id.cmsFragment, args)
        }
        requireActivity().llAccount.setOnClickListener {
            requireActivity().llAccount.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0] == 0){
                LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
                )
//                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
                val args=Bundle()
                args.putString("reference", "Profile")
                findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            }
            *//* else {
                 if(findNavController().currentDestination?.id != R.id.profileFragment) {
                     setBottomView()
                     requireActivity().itemProfile.setImageResource(R.drawable.user_profile_active)
                     findNavController().navigate(R.id.profileFragment)
                 }
             }*//*
        }
        requireActivity().llLogout.setOnClickListener {
            requireActivity().llLogout.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)

            val logoutDialog = LogoutDialog()
            logoutDialog.isCancelable = false
            logoutDialog.setDataCompletionCallback(object : LogoutDialog.LogoutInterface {
                override fun complete() {
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.UserId)
                    SharedPreferenceUtility.getInstance().delete(SharedPreferenceUtility.IsLogin)
                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isLoggedIn, false)
                    val args = Bundle()
                    args.putString("reference", "Logout")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finishAffinity()
                }
            })
            logoutDialog.show(requireActivity().supportFragmentManager, "HomeFragment")

            *//* startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
             requireActivity().finishAffinity()*//*
        }
    }*/



    private fun supplierDetails(isRefresh: Boolean) {
        mView.mainView.visibility=View.GONE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "supplier_user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), supplier_user_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.supplierDetails(builder.build())
        call!!.enqueue(object : Callback<SupplierDetailsResponse?> {
            override fun onResponse(call: Call<SupplierDetailsResponse?>, response: Response<SupplierDetailsResponse?>) {
                mView.mainView.visibility = View.VISIBLE
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (mView.swipeRefresh.isRefreshing) {
                    mView.swipeRefresh.isRefreshing = false
                }

                try {
                    if (response.body() != null) {
                        if (response.body()!!.response == 1) {
                            profile = response.body()!!.profile!!
                            categoryList.clear()
                            categoryList =
                                response.body()!!.categories as ArrayList<CategoriesItemX>
                            if (profile.bio!!.isEmpty()) {
                                mView.txtBioContent.visibility = View.GONE
                                mView.txtNoBioFound.visibility = View.VISIBLE
                            } else {
                                mView.txtBioContent.visibility = View.VISIBLE
                                mView.txtNoBioFound.visibility = View.GONE
                                mView.txtBioContent.text = profile.bio
                            }
                            Glide.with(requireContext()).load(profile.profilePicture)
                                .placeholder(R.drawable.user)
                                .into(requireActivity().supplier_fragment_toolbar.img)
                            mView.categories_rvlist.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            supplierCategoryAdapter =
                                SupplierCategoriesAdapter(requireContext(), categoryList)
                            mView.categories_rvlist.adapter = supplierCategoryAdapter
                            supplierCategoryAdapter.notifyDataSetChanged()
                            mView.txtSupplier.text = profile.name
                            if (SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].equals(
                                    "ar"
                                )
                            ) {
                                mView.address.text = profile.countryServedNameAr
                                mView.txtBrand.text = profile.accountNameAr
                            } else {
                                mView.address.text = profile.countryServedName
                                mView.txtBrand.text = profile.accountName
                            }

                            when (profile.accountType) {
                                1 -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(
                                        R.drawable.blogger_bg
                                    )
                                }

                                2 -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(
                                        R.drawable.brand_bg
                                    )
                                }

                                3 -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(
                                        R.drawable.homemade_supplier_bg
                                    )
                                }

                                4 -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(
                                        R.drawable.health_and_beauty_bg
                                    )
                                }
                            }
                            if (profile.rating!!.equals("") || profile.rating!!.equals(null)) {
                                mView.txtRating.text = "0"
                                mView.ratingBar.rating = 0F
                            } else {
                                mView.txtRating.text = profile.rating.toString()
                                mView.ratingBar.rating = profile.rating!!.toFloat()
                            }

                            if (response.body()!!.products!!.isEmpty() || response.body()!!.products==null) {
                                mView!!.txtNoProduct.visibility = View.VISIBLE
                                mView!!.rvProducts.visibility = View.GONE
                            } else {
                                mView!!.txtNoProduct.visibility = View.GONE
                                mView!!.rvProducts.visibility = View.VISIBLE
                                productList.clear()
                                productList = response.body()!!.products as ArrayList<ProductsItemZ>
                                mView.rvProducts.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                supplierDetailsProductsAdapter = SupplierDetailsProductsAdapter(
                                    requireContext(),
                                    productList,
                                    profile,
                                    findNavController(),
                                    object : ClickInterface.ClickPosInterface {
                                        override fun clickPostion(pos: Int, type: String) {
                                            if (type == "Cart") {
                                                val attrbts = productList[pos].attributes
                                                val jsonArray =
                                                    JSONArray(productList[pos].attributes)
                                                val price = productList[pos].price
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
                                                    if (attrArrayData.length() == 2) {
                                                        checkProductPrice(
                                                            productList[pos].productId!!,
                                                            productList[pos].supplierUserId!!
                                                        )
                                                    }
                                                }
                                            } else if (type == "Supplier") {
                                                val bundle = Bundle()
                                                bundle.putInt(
                                                    "supplier_user_id",
                                                    productList[pos].supplierUserId!!
                                                )
                                                findNavController().navigate(
                                                    R.id.supplierDetailsFragment,
                                                    bundle
                                                )
                                            }
                                        }
                                    })
                                mView!!.rvProducts.adapter = supplierDetailsProductsAdapter
                            }

                        } else {
                            mView!!.txtNoProduct.visibility = View.VISIBLE
                            mView!!.rvProducts.visibility = View.GONE

                        }
                    } else {
                        LogUtils.shortToast(requireContext(), getString(R.string.no_results_found))
                        mView!!.txtNoProduct.visibility = View.VISIBLE
                        mView!!.rvProducts.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<SupplierDetailsResponse?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                mView.mainView.visibility=View.GONE
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }

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
        requireActivity().supplier_fragment_toolbar.visibility=View.VISIBLE
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

    private fun checkProductPrice(productId: Int, supplierId: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "data", "device_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                productId.toString(),
                attrArrayData.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.checkProductPrice(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE

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
                mView!!.progressBar.visibility= View.GONE
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
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("product_id", "product_item_id", "type", "quantity", "product_type", "cart_id", "device_id", "user_id", "add_cart_type", "supplier_id", "lang"),
            arrayOf(productId.toString(),
                product_item_id, "1", "1", "" , ""
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""], SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                ,add_cart_type, supplierId.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.cartAdd(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
}