package com.seen.user.fragment

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_supplier_details.view.*
import kotlinx.android.synthetic.main.supplier_profile_fragment_toolbar.view.*
import org.json.JSONArray
import org.json.JSONException
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
 * Use the [SupplierDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SupplierDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var mView: View
    var supplier_user_id:Int=0
    lateinit var productAdapter: ProductAdapter
    lateinit var productCategoryAdapter: ProductCategoryAdapter
    lateinit var supplierDetailsProductsAdapter: SupplierDetailsProductsAdapter
   /* lateinit var serveCountriesAdapter: ServeCountriesAdapter*/
    var productList=ArrayList<ProductsItem>()
    var catList=ArrayList<Categories>()
    var isLike:Boolean=false
    var categories=JSONArray()
    var profile = Profile()
    var catNameList=ArrayList<String>()
    var categoryList=ArrayList<CategoriesItem>()
    lateinit var categoryListAdapter: CategoryNameListAdapter
    lateinit var categoryAdapter: CategoriesAdapter
    lateinit var supplierCategoryAdapter: SupplierCategoriesAdapter
    var allCatList=ArrayList<CategoriesItem>()
    /*var serveCountriesList=ArrayList<ServeCountries>()*/

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
                val bundle=Bundle()
                bundle.putInt("supplier_user_id", supplier_user_id)
                bundle.putInt("category_id", catList[pos].id)
                findNavController().navigate(R.id.action_supplierDetailsFragment_to_productsFragment, bundle)
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
                mView.mainView.visibility=View.VISIBLE
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        if(response.body()!!.response==1){
                            profile = response.body()!!.profile!!
                            categoryList = response.body()!!.categories as ArrayList<CategoriesItem>
                            mView.txtBrand.text = profile.account_name
                            mView.txtBioContent.text = profile.bio
                            Glide.with(requireContext()).load(profile.profile_picture).placeholder(R.drawable.user).into(mView.supplier_details_img)
                            mView.categories_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            supplierCategoryAdapter= SupplierCategoriesAdapter(requireContext(), categoryList)
                            mView.categories_rvlist.adapter=supplierCategoryAdapter
                            supplierCategoryAdapter.notifyDataSetChanged()
                            mView.txtSupplier.text = profile.name
                            mView.address.text = profile.country_served_name
                            mView.address.text = profile.country_served_name
                            mView.txtBrand.text = profile.account_name
                            when(profile.account_name){
                                "Blogger" -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(R.drawable.blogger_bg)
                                }

                                "Brand" -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(R.drawable.brand_bg)
                                }

                                "Homemade supplier" -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(R.drawable.homemade_supplier_bg)
                                }

                                "Health and Beauty" -> {
                                    requireActivity().supplier_fragment_toolbar.backdrop.setImageResource(R.drawable.health_and_beauty_bg)
                                }
                            }
                            if (profile.rating!!.equals("") || profile.rating!!.equals(null)){
                                mView.txtRating.text = "0"
                                mView.ratingBar.rating = 0F
                            }else{
                                mView.txtRating.text = profile.rating.toString()
                                mView.ratingBar.rating = profile.rating!!.toFloat()
                            }

                            if (response.body()!!.products!!.isEmpty()){
                                LogUtils.shortCenterToast(requireContext(), getString(R.string.no_results_found))
                            }else{
                                productList = response.body()!!.products as ArrayList<ProductsItem>
                                mView.rvProducts.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                                supplierDetailsProductsAdapter= SupplierDetailsProductsAdapter(requireContext(), productList,profile,findNavController(), object : ClickInterface.ClickPosInterface{
                                    override fun clickPostion(pos: Int, type : String) {
                                        val bundle=Bundle()
                                        productList[pos].id?.let { bundle.putInt("product_id", it) }
                                        findNavController().navigate(R.id.action_supplierDetailsFragment_to_productDetailsFragment, bundle)
                                    }
                                })
                                mView.rvProducts.adapter=supplierDetailsProductsAdapter
                                supplierDetailsProductsAdapter.notifyDataSetChanged()
                            }
                        }else{
                            LogUtils.shortCenterToast(requireContext(), getString(R.string.no_results_found))
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SupplierDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SupplierDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}