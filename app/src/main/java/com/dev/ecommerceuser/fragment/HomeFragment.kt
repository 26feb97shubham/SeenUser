package com.dev.ecommerceuser.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.activity.HomeActivity
import com.dev.ecommerceuser.activity.LoginActivity
import com.dev.ecommerceuser.adapter.*
import com.dev.ecommerceuser.dialog.LogoutDialog
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Categories
import com.dev.ecommerceuser.model.HomeCategories
import com.dev.ecommerceuser.model.Products
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.about_us_more_info_frag_toolbar.view.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_introduction.view.*
import kotlinx.android.synthetic.main.categories_side_menu_layout.*
import kotlinx.android.synthetic.main.categories_side_menu_layout.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.iv_filter
import kotlinx.android.synthetic.main.fragment_home.view.loc_view
import kotlinx.android.synthetic.main.fragment_home.view.rvListCat
import kotlinx.android.synthetic.main.fragment_home2.*
import kotlinx.android.synthetic.main.fragment_home2.view.*
import kotlinx.android.synthetic.main.fragment_products.view.*
import kotlinx.android.synthetic.main.home_frag_categories_layout.view.*
import kotlinx.android.synthetic.main.item_recent_products.*
import kotlinx.android.synthetic.main.profile_toolbar_layout.view.*
import kotlinx.android.synthetic.main.side_menu_layout.*
import kotlinx.android.synthetic.main.side_top_view.*
import kotlinx.android.synthetic.main.supplier_profile_fragment_toolbar.view.*
import okhttp3.ResponseBody
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
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private val TAG: String="HomeFragment"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var homeCatList=ArrayList<HomeCategories>()
    lateinit var homeCategoriesAdapter: HomeCategoriesAdapter
    var catList=ArrayList<Categories>()
    lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var autoScrollViewPagerAdapter: AutoScrollViewPagerAdapter
    private  val MIN_SCALE = 0.85f
    private  val MIN_ALPHA = 0.5f
    private val LOCATION_PERMISSION_CODE = 500
    private val REQUEST_CHECK_SETTINGS = 900
    private val RequestPermissionsSettings = 500
    var latitude:String=""
    var longitude:String=""
    var address:String=""
    var isRefresh:Boolean=false
    var reference:String=""
    var profile_picture : String = ""
    var currentItem = 0
    var NUM_PAGES = 0
    lateinit var popuplayoutInflater: LayoutInflater
    lateinit var popupview : View
    lateinit var recentProductsAdapter: RecentProductsAdapter
    var isLoggedIn : Boolean = false

    private var runnable: Runnable? = null
    private val handler = Handler()

    lateinit var bannerAdapter: BannerAdapter
    var bannersList=ArrayList<Categories>()
    var productsList = ArrayList<Products>()

    var catNameList=ArrayList<String>()
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryNameListAdapter
    var allCatList=ArrayList<Categories>()
    private var category_id:String = ""
    private var category_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
            mView = inflater.inflate(R.layout.fragment_home2, container, false)

       /* val toast =Toast.makeText(requireContext(), "Hello User!!", Toast.LENGTH_LONG)
        toast.show()*/
        isLoggedIn = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isLoggedIn, false)
        if (!isLoggedIn){
            isLoggedIn = true
            SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isLoggedIn, isLoggedIn)
            val toast =Toast.makeText(requireContext(), "Hello User!!", Toast.LENGTH_LONG)
            toast.show()
        }

        setUpViews()
        if(TextUtils.isEmpty(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsDefaultLoc, ""])){
            requestLocation()
            getHomes()
        }
        else{
        /*    address=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsDefaultLoc, ""]
            mView!!.txtLoc.text = address
            latitude = SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SavedLat, ""]
            longitude= SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SavedLng, ""]*/
            getHomes()
            /*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*/
        }

        return mView
    }

    private fun setUpViews() {

        if(!TextUtils.isEmpty(HomeActivity.type)){
            manageNotificationRedirection()
        }

        mView!!.mainView2.visibility=View.GONE
        isRefresh=false
//        requireActivity().backImg.visibility=View.GONE

        getHomes()
        getCategories()
        setLogoutView()

        setBottomView()
        requireActivity().itemHome.setImageResource(R.drawable.selected_home)

        setOnClickBottomItemView()

        clickOnDrawer()

        /*clickOnCategoriesDrawer()*/

        setBanners()

        clickOnHomeItems()

        setHomeCategoryAdapter()
        setHomeCategoryData()

//        setCategoriesAdapter()

        mView!!.swipeRefresh2.setOnRefreshListener {
            isRefresh=true
            if(TextUtils.isEmpty(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsDefaultLoc, ""])){
      /*          requestLocation()*/
            }
            else{
     /*           address=SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.IsDefaultLoc, ""]
                mView!!.txtLoc.text = address
                latitude = SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SavedLat, ""]
                longitude= SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SavedLng, ""]*/
                getHomes()
                /*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*/
            }
        }

        mView!!.iv_filter.setOnClickListener {
            /*val filterBottomSheetDialogFragment = FilterBottomSheetDialogFragment.newInstance(requireContext())
            filterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, FilterBottomSheetDialogFragment.TAG)
            filterBottomSheetDialogFragment.setFilterClickListenerCallback(object : FilterBottomSheetDialogFragment.OnFilterClick{
                override fun onFilter() {
                    findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)
                }
            })*/

            findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)
        }

        mView!!.loc_view.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myLocationFragment)
        }

      /*  mView!!.search_view2.setOnClickListener {
            findNavController().navigate(R.id.action_mainHomeFragment_to_filteredproductsfragment)
        }
*/


    }

    fun setProducts(){
        Log.e("Products_list", productsList.toString())
        mView!!.rv_products.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recentProductsAdapter = RecentProductsAdapter(requireContext(),productsList,findNavController(), object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int,type: String) {
                Log.e("Position_1", pos.toString())
               /* if(type.equals("Like")){
                    likeUnlikeProduct(pos, productsList)
                } else if(type.equals("Cart")) {
                    LogUtils.shortCenterToast(context, "Item added to cart successfully!!!!. '\n' Thanks alot for purchasing")
                } else{
                        val bundle = Bundle()
                    bundle.putInt("product_id", productsList[pos].id)
                    findNavController().navigate(R.id.productDetailsFragment, bundle)
                }*/
            }

        })
        mView!!.rv_products.adapter = recentProductsAdapter
        recentProductsAdapter.notifyDataSetChanged()
    }

    private fun likeUnlikeProduct(pos: Int, productsList: ArrayList<Products>) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
            val args=Bundle()
            args.putString("reference", "OffersDiscount")
            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            return
        }

        Log.e("product_id", productsList[pos].id.toString())
        Log.e("pos", pos.toString())

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), productsList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.likeUnlikeProduct(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            this@HomeFragment.productsList[pos].like = !this@HomeFragment.productsList[pos].like
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            recentProductsAdapter.notifyDataSetChanged()

                        }
                        else{
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
                LogUtils.shortToast(requireContext(),throwable.message)
            }
        })
    }

    private fun getCategories() {
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : retrofit2.Callback<ResponseBody?> {
            override fun onResponse(call: retrofit2.Call<ResponseBody?>, response: retrofit2.Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val categories = jsonObject.getJSONArray("categories")
                        categoryList.clear()
                        catNameList.clear()
                        if(categories.length() != 0) {
                            requireActivity().navSideMenuhome.txtNoDataFound_categories_home.visibility = View.GONE
                            requireActivity().navSideMenuhome.rv_categories_home.visibility = View.VISIBLE
                            for (i in 0 until categories.length()) {
                                val jsonObj = categories.getJSONObject(i)
                                val c = Categories()
                                c.name = jsonObj.getString("name")
                                c.id = jsonObj.getInt("id")
                                /* c.image = jsonObj.getString("image")
                                 c.desc = jsonObj.getString("desc")
                                 c.icon = jsonObj.getInt("icon")*/
                                categoryList.add(c)
                            }

                            /*   for (i in 0 until categoryList.size){
                                   catNameList.add(categoryList[i].name)
                               }*/
                            requireActivity().navSideMenuhome.rv_categories_home.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            categoryListAdapter= CategoryNameListAdapter(requireContext(), categoryList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    category_id = categoryList[pos].id.toString()
                                    category_name = categoryList[pos].name.toString()
                                    opencloseDrawer()
                                    val bundle = Bundle()
                                    bundle.putString("category_id", category_id)
                                    bundle.putString("category_name", category_name)
                                    findNavController().navigate(R.id.categoriesDetailsFragment, bundle)
                                }
                            })
                            requireActivity().navSideMenuhome.rv_categories_home.adapter=categoryListAdapter
                            categoryListAdapter.notifyDataSetChanged()
                        }
                        else{
                            requireActivity().navSideMenuhome.txtNoDataFound_categories_home.visibility = View.VISIBLE
                            requireActivity().navSideMenuhome.rv_categories_home.visibility = View.GONE
                        }
                        allCatList.clear()
                        allCatList.addAll(categoryList)
                        categoryListAdapter.notifyDataSetChanged()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), throwable.message)
            }
        })
    }

    private fun clickOnCategoriesDrawer() {
        findNavController().navigate(R.id.action_mainHomeFragment_to_categoriesDetailsFragment)
    }

    private fun manageNotificationRedirection() {
        if(HomeActivity.type.equals("accept", true) || HomeActivity.type.equals("reject", true)){
            val bundle=Bundle()
            bundle.putString("type", HomeActivity.type)
            findNavController().navigate(R.id.myOrdersFragment, bundle)
        }
        HomeActivity.type=""
       /* else if(HomeActivity.type.equals("reject", true)){
            findNavController().navigate(R.id.myOrdersFragment)
        }*/
    }

   /* private fun setCategoriesAdapter() {
        mView!!.rvList2.layoutManager = GridLayoutManager(activity, 3).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position== 0)
                        3
                    else
                        1
                }
            }
        }

        categoriesAdapter= CategoriesAdapter(
                requireContext(),
                catList,
                object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int) {
                        val bundle = Bundle()
                        bundle.putInt("category_id", catList[pos].id)
                        findNavController().navigate(
                                R.id.action_homeFragment_to_productsFragment,
                                bundle
                        )
                    }

                })
        mView!!.rvList2.adapter=categoriesAdapter
    }*/

    private fun setHomeCategoryAdapter() {
        requireActivity().home_frag_categories.rv_categories.layoutManager= LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        )
        homeCategoriesAdapter= HomeCategoriesAdapter(
                requireContext(),
                homeCatList,
                object : ClickInterface.ClickPosInterface {
                    override fun clickPostion(pos: Int, status : String) {
                        if (homeCatList[pos].id == 0){
//                            findNavController().popBackStack()
                            opencloseDrawer()
                        } else if (homeCatList[pos].id == 1) {
                            /*findNavController().navigate(R.id.action_homeFragment_to_homeMadeSuppliersFragment)*/
                                if(findNavController().currentDestination?.id!=R.id.homeMadeSuppliersFragment){
                                    findNavController().navigate(R.id.homeMadeSuppliersFragment)
                                }
                        } else if (homeCatList[pos].id == 2) {
                            /*findNavController().navigate(R.id.action_mainHomeFragment_to_brandsFragment)*/
                            if(findNavController().currentDestination?.id!=R.id.brandsFragment){
                                findNavController().navigate(R.id.brandsFragment)
                            }
                        } else if (homeCatList[pos].id == 3) {
                           /* findNavController().navigate(R.id.action_homeFragment_to_bloggersFragment)*/
                            if(findNavController().currentDestination?.id!=R.id.bloggersFragment){
                                findNavController().navigate(R.id.bloggersFragment)
                            }
                        } else if (homeCatList[pos].id == 4) {
                            /*findNavController().navigate(R.id.action_homeFragment_to_healthandBeautyFragment)*/
                            if(findNavController().currentDestination?.id!=R.id.healthandBeautyFragment){
                                findNavController().navigate(R.id.healthandBeautyFragment)
                            }
                        } else if (homeCatList[pos].id == 5) {
                            /*findNavController().navigate(R.id.action_homeFragment_to_globalMarketFragment)*/
                            if(findNavController().currentDestination?.id!=R.id.globalMarketFragment){
                                findNavController().navigate(R.id.globalMarketFragment)
                            }
                        }
                    }
                })
        requireActivity().home_frag_categories.rv_categories.adapter=homeCategoriesAdapter
        homeCategoriesAdapter.notifyDataSetChanged()
    }

    private fun opencloseDrawer() {
        if(requireActivity().drawerLayout.isDrawerOpen(GravityCompat.END)){
            requireActivity().drawerLayout.closeDrawer(GravityCompat.END)
        }
        else{
            requireActivity().drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun clickOnHomeItems() {
        mView!!.loc_view.setOnClickListener {
            mView!!.loc_view.startAnimation(AlphaAnimation(1f, 0.5f))
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

        /*requireActivity().notificationImg.setOnClickListener {
            requireActivity().notificationImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openNotificationFrag()
        }

        requireActivity().frag_other_notificationImg.setOnClickListener {
            requireActivity().frag_other_notificationImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openNotificationFrag()
        }

        requireActivity().profile_fragment_toolbar.profileFragment_notificationImg.setOnClickListener {
            requireActivity().profile_fragment_toolbar.profileFragment_notificationImg.startAnimation(
                    AlphaAnimation(
                            1f,
                            0.5f
                    )
            )
            openNotificationFrag()
        }

        requireActivity().about_us_fragment_toolbar.aboutUsFragment_notificationImg.setOnClickListener {
            requireActivity().about_us_fragment_toolbar.aboutUsFragment_notificationImg.startAnimation(
                    AlphaAnimation(
                            1f,
                            0.5f
                    )
            )
            openNotificationFrag()
        }*/

        requireActivity().addToBasketImg.setOnClickListener {
            requireActivity().addToBasketImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openAddToBasketFragment()
        }

        requireActivity().frag_other_addToBasketImg.setOnClickListener {
            requireActivity().frag_other_addToBasketImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openAddToBasketFragment()
        }

        requireActivity().profile_fragment_toolbar.profileFragment_addToBasketImg.setOnClickListener {
            requireActivity().profile_fragment_toolbar.profileFragment_addToBasketImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openAddToBasketFragment()
        }

        requireActivity().about_us_fragment_toolbar.aboutUsFragment_addToBasketImg.setOnClickListener {
            requireActivity().about_us_fragment_toolbar.aboutUsFragment_addToBasketImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openAddToBasketFragment()
        }

        requireActivity().supplier_fragment_toolbar.frag_profile_cartImg.setOnClickListener {
            requireActivity().supplier_fragment_toolbar.frag_profile_cartImg.startAnimation(AlphaAnimation(1f, 0.5f))
            openAddToBasketFragment()
        }

    }

    private fun openAddToBasketFragment(){
        findNavController().navigate(R.id.cartFragment)
    }

    private fun openNotificationFrag() {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0] == 0){
            LogUtils.shortToast(
                    requireContext(),
                    getString(R.string.please_login_signup_to_access_this_functionality)
            )
//                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
            val args=Bundle()
            args.putString("reference", "Home")
            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
        }
        else {
            findNavController().navigate(R.id.notificationsFragment)
        }
    }

    private fun setBanners() {
        val linearLayoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        )
        mView!!.rv_banners.layoutManager= linearLayoutManager
        bannerAdapter= BannerAdapter(requireContext(), bannersList)
        mView!!.rv_banners.adapter=bannerAdapter

        mView!!.rv_banners.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mView!!.swipeRefresh2.isEnabled =
                        true
                if (newState == RecyclerView.SCROLL_STATE_IDLE) mView!!.swipeRefresh2.isEnabled =
                        false
            }
        })

        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(mView!!.rvListCat)

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (bannerAdapter.itemCount) - 1) {
                    linearLayoutManager.smoothScrollToPosition(mView!!.rv_banners, RecyclerView.State(),
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1)
                } else {
                    linearLayoutManager.smoothScrollToPosition(mView!!.rv_banners, RecyclerView.State(), 0)
                }
            }

        }, 0, 5000)

        /*for (i in 1 .. mView!!.rvListCat.adapter!!.itemCount){
            handler.postDelayed(object : Runnable {
                override fun run() {
                    mView!!.rvListCat.smoothScrollToPosition(i)
                }
            }, 5000)
        }*/

      /*  mView!!.rvListCat.post { mView!!.rvListCat.smoothScrollToPosition(mView!!.rvListCat.adapter!!.itemCount) }
        var i = 1
        if(i<mView!!.rvListCat.adapter!!.itemCount){
            handler.postDelayed(object : Runnable {
                override fun run() {
                    mView!!.rvListCat.smoothScrollToPosition(i)
                }
            }, 5000)
        }else{
            i=1
        }

        var pos = 0
        if (pos>=mView!!.rvListCat.adapter!!.itemCount){
            pos = 0
        }else{
            handler.postDelayed(object : Runnable{
                override fun run() {
                    mView!!.rvListCat.smoothScrollToPosition(pos)
                }
            }, 5000)
        }
*/
   /*     startAutoSlider(mView!!.rvListCat.adapter!!.itemCount)*/




        /*Thread(Runnable {
            //some method here
            Thread.sleep(5000)
            var pos = BannerAdapter.currentpos
            pos = pos + 1
            if (pos>=mView!!.rvListCat.adapter!!.itemCount) pos = 0
            mView!!.rvListCat.smoothScrollToPosition(i)
            if(i<mView!!.rvListCat.adapter!!.itemCount){
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        mView!!.rvListCat.smoothScrollToPosition(i)
                    }
                }, 5000)
                mView!!.rvListCat.smoothScrollToPosition(i)
                i++
            }else{
                i=1
            }
//            Thread.sleep(5000)
        }).start()*/

    }

    private fun startAutoSlider(itemCount: Int) {
        runnable = object : Runnable{
            override fun run() {
                currentItem = currentItem + 1
                if (currentItem >= itemCount){
                    currentItem = 0
                }else{
//                    mView!!.rvListCat.smoothScrollToPosition(currentItem)
                    handler.postDelayed(runnable!!, 5000)
                }
            }
        }
        currentItem+=1
        handler.postDelayed(runnable!!, 5000)
    }

    private fun setOnClickBottomItemView() {
        requireActivity().itemDiscount.setOnClickListener {
            if(findNavController().currentDestination?.id!=R.id.discountFragment){
                requireActivity().itemDiscount.startAnimation(AlphaAnimation(1f, 0.5f))
                setBottomView()
                requireActivity().itemDiscount.setImageResource(R.drawable.selected_offers_and_discounts)
                findNavController().navigate(R.id.discountFragment)
            }
        }
        requireActivity().itemHome.setOnClickListener {
            if(findNavController().currentDestination?.id!=R.id.homeFragment){
                requireActivity().itemHome.startAnimation(AlphaAnimation(1f, 0.5f))
                setBottomView()
                requireActivity().itemHome.setImageResource(R.drawable.selected_home)
                findNavController().navigate(R.id.homeFragment)
            }

        }
        /* requireActivity().itemSearch.setOnClickListener {
             if(findNavController().currentDestination?.id!=R.id.searchFragment){
                 requireActivity().itemSearch.startAnimation(AlphaAnimation(1f, 0.5f))
                 setBottomView()
                 requireActivity().itemSearch.setImageResource(R.drawable.search_active)
                 findNavController().navigate(R.id.searchFragment)
             }

         }*/
       /* requireActivity().itemProfile.setOnClickListener {
            if(findNavController().currentDestination?.id!=R.id.profileFragment){
                requireActivity().itemProfile.startAnimation(AlphaAnimation(1f, 0.5f))
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
                else {
                    setBottomView()
                    requireActivity().itemProfile.setImageResource(R.drawable.user_active_icon)
                    findNavController().navigate(R.id.profileFragment)
                }
            }

        }
*/

        requireActivity().itemHotSale.setOnClickListener {
            if(findNavController().currentDestination?.id!=R.id.hotdealsFragment){
                requireActivity().itemHotSale.startAnimation(AlphaAnimation(1f, 0.5f))
                setBottomView()
                requireActivity().itemHotSale.setImageResource(R.drawable.selected_item_hot_deals)
                findNavController().navigate(R.id.hotdealsFragment)
            }

        }

        requireActivity().itemNotification.setOnClickListener {
            if(findNavController().currentDestination?.id!=R.id.notificationsFragment){
                requireActivity().itemNotification.startAnimation(AlphaAnimation(1f, 0.5f))
                setBottomView()
                requireActivity().itemNotification.setImageResource(R.drawable.notification_icon)
                findNavController().navigate(R.id.notificationsFragment)
            }
        }


    }

    private fun setLogoutView() {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0] == 0){
            requireActivity().name.setTextColor(getColor(requireContext(), R.color.blue))
            Glide.with(requireContext()).load(R.drawable.user).into(requireActivity().userIcon)
            requireActivity().name.text = getString(R.string.login_signup)
            requireActivity().email.text = ""
            requireActivity().llLogout.visibility=View.GONE
            requireActivity().logoutView.visibility=View.GONE
        }
        else{
            requireActivity().llLogout.visibility=View.VISIBLE
            requireActivity().logoutView.visibility=View.VISIBLE
        }
    }

    fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
            displayLocationSettingsRequest(requireActivity())
        } else {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_CODE
            )
        }
    }
    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2.toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
                googleApiClient,
                builder.build()
        )
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(TAG, "All location settings are satisfied.")
                    getSmartLocation()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                            TAG,
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                    try {
                        startIntentSenderForResult(
                                status.getResolution()?.getIntentSender(),
                                REQUEST_CHECK_SETTINGS,
                                null,
                                0,
                                0,
                                0,
                                null
                        )

                    } catch (e: IntentSender.SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                            TAG,
                            "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )

                }
            }
        }
    }
    fun getSmartLocation(){
        if(!isRefresh){
//            mView!!.progressBar.visibility=View.VISIBLE
            mView!!.mainView2.visibility=View.GONE
            mView!!.shimmerLayout2.visibility=View.VISIBLE
            mView!!.shimmerLayout2.startShimmer()
        }

        SmartLocation.with(activity).location()
                .oneFix()
                .start(object : OnLocationUpdatedListener {
                    override fun onLocationUpdated(location: Location) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        /*latitude= "36.778259"
                        longitude= "-119.417931"*/
                        try {
                            val geocoder = Geocoder(activity, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                            )
                            address = addresses[0].getAddressLine(0).toString()
                         /*   mView!!.txtLoc.text = address*/
                            getHomes()
//                            address = "Jaipur,Rajasthan"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CHECK_SETTINGS ){
            if(resultCode == Activity.RESULT_OK){
                getSmartLocation()
                /*   isShimmerShow=true
                   studentHomeList()*/
            }
            else{
                getHomes()
            }

        }
        else if(requestCode==RequestPermissionsSettings ) {
            requestLocation()
            /*   isShimmerShow=true
               studentHomeList()*/
        }



    }
    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, RequestPermissionsSettings)
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED) {
                        displayLocationSettingsRequest(requireActivity())
//                        getSmartLocation()
//                        getLocationFromLocationManager()
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Location Permission Required")
                    builder.setMessage("Please enable  location permissions in settings")
                    builder.setPositiveButton(R.string.settings) { dialog, which ->
                        dialog.cancel()
                        goToSettings()
                    }
                    builder.setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.cancel()
                    }
                    builder.show()

                }


            }
        }

    }
 /*   private fun getLocationFromLocationManager() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    latitude= location.latitude.toString()
                    longitude= location.longitude.toString()
                    try {
                        val geocoder = Geocoder(activity, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        address = addresses[0].getAddressLine(0).toString()
                        *//*if(isShimmerShow){
                            isShimmerShow=false
                            studentHomeList()
                        }*//*
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }



                }


            }

           *//* isShimmerShow=true
            studentHomeList()*//*
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)


        }

    }*/

    private fun clickOnDrawer() {
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
                setBottomView()
                requireActivity().itemDiscount.setImageResource(R.drawable.selected_offers_and_discounts)
                findNavController().navigate(R.id.discountFragment)
            }
        }
        requireActivity().llAboutUs.setOnClickListener {
            requireActivity().llAboutUs.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.fragmentaboutus)
        }
        requireActivity().llFAQ.setOnClickListener {
            requireActivity().llFAQ.startAnimation(AlphaAnimation(1f, 0.5f))
            requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
           /* val args=Bundle()
            args.putString("title", getString(R.string.faq))
            findNavController().navigate(R.id.webViewFragment, args)*/
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
            } else {
                requireActivity().llAccount.startAnimation(AlphaAnimation(1f, 0.5f))
                requireActivity().drawerLayout.closeDrawer(GravityCompat.START)
                findNavController().navigate(R.id.profileFragment)
            }
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

           /* startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
            requireActivity().finishAffinity()*/
        }
    }

    private fun setBottomView() {
        requireActivity().itemDiscount.setImageResource(R.drawable.discount_icon_1)
        requireActivity().itemMenu.setImageResource(R.drawable.menu_icon_1)
//        requireActivity().itemCart.setImageResource(R.drawable.add_to_basket_icon)
        requireActivity().itemHome.setImageResource(R.drawable.home_icon)
//        requireActivity().itemSearch.setImageResource(R.drawable.search)
//        requireActivity().itemProfile.setImageResource(R.drawable.user_inactive_icon)
        requireActivity().itemNotification.setImageResource(R.drawable.notification_icon)
        requireActivity().itemHotSale.setImageResource(R.drawable.hot_sale_icon)

//        setHostFragment()

    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int{
            return 3
        }

        override fun createFragment(position: Int): Fragment{

            val fragment= ViewPagerFragment()
            return fragment
        }
    }

    private fun setHomeCategoryData() {
        homeCatList.clear()

        val h = HomeCategories()
        h.id=0
        h.name=getString(R.string.category)
        h.icon=R.drawable.dots_menu_transparent
        homeCatList.add(h)

        val h2 = HomeCategories()
        h2.id=1
        h2.name=getString(R.string.homemade_suppliers)
        h2.icon=R.drawable.homemadesuppliers
        homeCatList.add(h2)

        val h3 = HomeCategories()
        h3.id=2
        h3.name=getString(R.string.brands)
        h3.icon=R.drawable.brands
        homeCatList.add(h3)

        val h4 = HomeCategories()
        h4.id=3
        h4.name=getString(R.string.bloggers)
        h4.icon=R.drawable.bloggers
        homeCatList.add(h4)

        val h5 = HomeCategories()
        h5.id=4
        h5.name=getString(R.string.health_and_beauty)
        h5.icon=R.drawable.health_n_beauty_icon
        homeCatList.add(h5)

    /*    val h6 = HomeCategories()
        h6.id=5
        h6.name=getString(R.string.gallery)
        h6.icon=R.drawable.gallery
        homeCatList.add(h6)*/

        val h6 = HomeCategories()
        h6.id=5
        h6.name=getString(R.string.global_market)
        h6.icon=R.drawable.global_markets
        homeCatList.add(h6)

        homeCategoriesAdapter.notifyDataSetChanged()
    }

    private fun getHomes() {
        requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        /*if(!isReferesh) {
            mView!!.progressBar.visibility = View.VISIBLE
        }*/

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(
                arrayOf(
                        "user_id",
                        "device_id",
                        "lang"
                ),
                arrayOf(
                        "155",
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""],
                        SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()
                )
        )
        val call = apiInterface.getHomes(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (mView!!.swipeRefresh2.isRefreshing) {
                    mView!!.swipeRefresh2.isRefreshing = false
                }
//                mView!!.progressBar.visibility = View.GONE
                mView!!.mainView2.visibility = View.VISIBLE
                mView!!.shimmerLayout2.visibility = View.GONE
                mView!!.shimmerLayout2.stopShimmer()

                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("carts_count") != 0) {
                            requireActivity().cartWedgeCount.visibility = View.VISIBLE
                            requireActivity().frag_other_cartWedgeCount.visibility = View.VISIBLE
                            requireActivity().profile_fragment_toolbar.profileFragment_cartWedgeCount.visibility = View.VISIBLE
                            requireActivity().about_us_fragment_toolbar.aboutUsFragment_cartWedgeCount.visibility = View.VISIBLE
                            requireActivity().cartWedgeCount.text = jsonObject.getInt("carts_count").toString()
                            requireActivity().frag_other_cartWedgeCount.text = jsonObject.getInt("carts_count").toString()
                            requireActivity().profile_fragment_toolbar.profileFragment_cartWedgeCount.text = jsonObject.getInt("carts_count").toString()
                            requireActivity().about_us_fragment_toolbar.aboutUsFragment_cartWedgeCount.text = jsonObject.getInt("carts_count").toString()
                        } else {
                            requireActivity().cartWedgeCount.visibility = View.GONE
                            requireActivity().frag_other_cartWedgeCount.visibility = View.GONE
                            requireActivity().profile_fragment_toolbar.profileFragment_cartWedgeCount.visibility = View.GONE
                            requireActivity().about_us_fragment_toolbar.aboutUsFragment_cartWedgeCount.visibility = View.GONE
                        }
                        val banners = jsonObject.getJSONArray("banners")
                        bannersList.clear()
                        for (i in 0 until banners.length()) {
                            if (bannersList.size < 4) {
                                val jsonObj = banners.getJSONObject(i)
                                val cate = Categories()
                                cate.id = jsonObj.getInt("id")
                                cate.name = jsonObj.getString("name")
                                /*cate.desc = jsonObj.getString("desc")*/
                                cate.image = jsonObj.getString("image")
                                bannersList.add(cate)
                            }
                        }
                        /*pagerAdapter.notifyDataSetChanged()*/

                        mView!!.pageIndicator2.attachTo(mView!!.rv_banners)
                        bannerAdapter.notifyDataSetChanged()
//                        autoScrollViewPagerAdapter.notifyDataSetChanged()


                     /*   val categories = jsonObject.getJSONArray("categories")
                        catList.clear()
                        for (i in 0 until categories.length()) {
                            val jsonObj = categories.getJSONObject(i)
                            val cate = Categories()
                            cate.id = jsonObj.getInt("id")
                            cate.name = jsonObj.getString("name")
                            cate.image = jsonObj.getString("image")
                            catList.add(cate)
                        }
                        categoriesAdapter.notifyDataSetChanged()*/

                        val products = jsonObject.getJSONArray("products")
                        productsList.clear()
                        for (i in 0 until products.length()){
                            val jsonObj = products.getJSONObject(i)
                            val products = Products()
                            products.files = jsonObj.getString("files")
                            products.like = jsonObj.getBoolean("like")
                            products.name = jsonObj.getString("name")
                            products.price = jsonObj.getString("price")
                            products.id = jsonObj.getInt("id")
                            productsList.add(products)
                        }
//                        recentProductsAdapter.notifyDataSetChanged()
                        setProducts()


                        val profile = jsonObject.getJSONObject("profile")
                        requireActivity().name.setTextColor(
                                getColor(
                                        requireContext(),
                                        R.color.black
                                )
                        )
                        requireActivity().name.text = profile.getString("name")
                        requireActivity().email.text = profile.getString("email")
                        profile_picture = profile.getString("profile_picture")
                        Glide.with(requireContext()).load(profile.getString("profile_picture"))
                                .placeholder(R.drawable.user).into(requireActivity().frag_other_menuImg)
                        Glide.with(requireContext()).load(profile.getString("profile_picture"))
                                .placeholder(R.drawable.user).into(requireActivity().menuImg)
                        Glide.with(requireContext()).load(profile.getString("profile_picture"))
                                .placeholder(R.drawable.user).into(requireActivity().userIcon)
                        Glide.with(requireContext()).load(profile.getString("profile_picture"))
                                .placeholder(R.drawable.user)
                                .into(requireActivity().about_us_fragment_toolbar.aboutUsFragment_menuImg)

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
                if (mView!!.swipeRefresh2.isRefreshing) {
                    mView!!.swipeRefresh2.isRefreshing = false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), throwable.message)
//                mView!!.progressBar.visibility = View.GONE
                mView!!.mainView2.visibility = View.VISIBLE
                mView!!.shimmerLayout2.visibility = View.GONE
                mView!!.shimmerLayout2.stopShimmer()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
         * @return A new instance of fragment MainHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
       /* requireActivity().backImg.visibility=View.GONE*/
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.VISIBLE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.GONE

    }
}