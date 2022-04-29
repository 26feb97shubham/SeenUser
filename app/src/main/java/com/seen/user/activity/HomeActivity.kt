package com.seen.user.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.adapter.CategoryNameListAdapter
import com.seen.user.model.Categories
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.profile_toolbar_layout.view.*
import kotlinx.android.synthetic.main.side_top_view.*
import kotlinx.android.synthetic.main.supplier_profile_fragment_toolbar.view.*
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    var catNameList=ArrayList<String>()
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryNameListAdapter
    var allCatList=ArrayList<Categories>()
    var login_data : String ?= null
    var login_data_bundle = Bundle()
    private var category_id:String = ""
    var jsonObject  :JSONObject?=null
    var loggedInUserName : String ? = null
    var loggedInUserEmail : String ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utility.changeLanguage(
            this,
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setContentView(R.layout.activity_home)
        setUpViews()
    }
    companion object{
        var type:String=""
        var profile_picture : String = ""
    }
    private fun setUpViews() {
        if(intent != null){
            type=intent.getStringExtra("type").toString()
            Log.e("login_data_bundle" , ""+login_data_bundle)
        }

        profile_picture = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.ProfilePic, "")
        loggedInUserName = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserName, "")
        loggedInUserEmail = SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserEmail, "")

        name.text = loggedInUserName
        email.text = loggedInUserEmail

        uploadProfilePic(profile_picture, menuImg)
        uploadProfilePic(profile_picture, frag_other_menuImg)
        uploadProfilePic(profile_picture, profile_fragment_toolbar.profileFragment_menuImg)
        uploadProfilePic(profile_picture, supplier_fragment_toolbar.frag_profile)
        uploadProfilePic(profile_picture, userIcon)


        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)

        itemMenu.setOnClickListener {
            itemMenu.startAnimation(AlphaAnimation(1f, 0.5f))
            openCloseDrawer()
        }

       /* menuImg.setOnClickListener {
            openCloseDrawer()
        }

        frag_other_menuImg.setOnClickListener {
            openCloseDrawer()
        }

        profile_fragment_toolbar.profileFragment_menuImg.setOnClickListener {
            openCloseDrawer()
        }

        about_us_fragment_toolbar.aboutUsFragment_menuImg.setOnClickListener {
            openCloseDrawer()
        }

        supplier_fragment_toolbar.frag_profile.setOnClickListener {
            openCloseDrawer()
        }*/

        /*setHomeCategoryAdapter()*/

    }

    private fun setBottomView() {
        itemDiscount.setImageResource(R.drawable.discount_icon_1)
        itemMenu.setImageResource(R.drawable.menu_icon_1)
//        requireActivity().itemCart.setImageResource(R.drawable.add_to_basket_icon)
        itemHome.setImageResource(R.drawable.home_icon)
//        requireActivity().itemSearch.setImageResource(R.drawable.search)
//        requireActivity().itemProfile.setImageResource(R.drawable.user_inactive_icon)
       itemNotification.setImageResource(R.drawable.notification_icon)
        itemHotSale.setImageResource(R.drawable.hot_sale_icon)

//        setHostFragment()

    }

    private fun uploadProfilePic(profilePicture: String, civ: CircleImageView) {
        Glide.with(this).load(profilePicture)
            .placeholder(R.drawable.user).into(civ)
    }

    /*  private fun setHomeCategoryAdapter() {
          home_frag_categories.rv_categories.layoutManager= LinearLayoutManager(
                  this,
                  LinearLayoutManager.HORIZONTAL,
                  false
          )
          homeCategoriesAdapter= HomeCategoriesAdapter(
                  this,
                  homeCatList,
                  object : ClickInterface.ClickPosInterface {
                      override fun clickPostion(pos: Int) {
                          if (homeCatList[pos].id == 0){
                              opencloseDrawer()
                          } else if (homeCatList[pos].id == 1) {
                              findNavController().navigate(R.id.action_homeFragment_to_homeMadeSuppliersFragment)
                          } else if (homeCatList[pos].id == 2) {
                              findNavController().navigate(R.id.action_mainHomeFragment_to_brandsFragment)
                          } else if (homeCatList[pos].id == 3) {
                              findNavController().navigate(R.id.action_homeFragment_to_bloggersFragment)
                          } else if (homeCatList[pos].id == 4) {
                              findNavController().navigate(R.id.action_homeFragment_to_healthandBeautyFragment)
                          } else if (homeCatList[pos].id == 5) {
                              findNavController().navigate(R.id.action_homeFragment_to_galleryFragment)
                          } else if (homeCatList[pos].id == 6) {
                              findNavController().navigate(R.id.action_homeFragment_to_globalMarketFragment)
                          }
                      }
                  })

      }*/

    private fun openCloseDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }



    /*  private fun getProfile() {

          window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
  //        mView!!.progressBar.visibility = View.VISIBLE

          val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
          val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                  arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


          val call = apiInterface.getProfile(builder.build())
          call!!.enqueue(object : Callback<ResponseBody?> {
              override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
  //                mView!!.progressBar.visibility = View.GONE
                  window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                  try {
                      if (response.body() != null) {
                          val jsonObject = JSONObject(response.body()!!.string())
                          if (jsonObject.getInt("response") == 1) {
                              val data = jsonObject.getJSONObject("data")
                              name.setTextColor(
                                      ContextCompat.getColor(
                                              this@HomeActivity,
                                              R.color.black
                                      )
                              )
                              name.text = data.getString("name")
                              email.text = data.getString("email")
                              Glide.with(this@HomeActivity).load(data.getString("profile_picture")).placeholder(R.drawable.user).into(userIcon)
                          } else {
                              LogUtils.shortToast(this@HomeActivity, jsonObject.getString("message"))
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
                  LogUtils.shortToast(this@HomeActivity, getString(R.string.check_internet))
  //                mView!!.progressBar.visibility = View.GONE
                  window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
              }
          })
      }*/


    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        when(findNavController(R.id.nav_home_host_fragment).currentDestination?.id){
            R.id.homeFragment -> exitApp()
            R.id.otpVerificationFragment -> exitApp()
            R.id.resetPasswordFragment -> exitApp()
            R.id.discountFragment -> {
                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            R.id.cartFragment -> {
                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            R.id.bloggersFragment -> {
                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            R.id.brandsFragment -> {
                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            R.id.homeMadeSuppliersFragment -> {
                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
           /* R.id.checkOutFragment -> {
                itemCart.setImageResource(R.drawable.shopping_cart_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.cartFragment)
            }*/
            R.id.loginFragment -> {
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.chooseLoginSingUpFragment)
            }
            R.id.profileFragment -> {

                itemHome.setImageResource(R.drawable.home_active)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            R.id.chooseLoginSingUpFragment -> {
               exitApp()
            }
            else-> findNavController(R.id.nav_home_host_fragment).popBackStack()
        }


    }

    private fun exitApp() {
        val toast = Toast.makeText(
            this,
            getString(R.string.please_click_back_again_to_exist),
            Toast.LENGTH_SHORT
        )


        if(doubleClick){
            finishAffinity()
            doubleClick=false
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 500)
        }
    }
}