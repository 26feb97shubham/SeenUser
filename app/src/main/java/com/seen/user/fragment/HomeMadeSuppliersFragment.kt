package com.seen.user.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.activity.LoginActivity
import com.seen.user.adapter.CategoryListAdapter
import com.seen.user.adapter.NameListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Categories
import com.seen.user.model.CategoryName
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_filtered_products.view.*
import kotlinx.android.synthetic.main.fragment_health_and_beauty.view.*
import kotlinx.android.synthetic.main.fragment_home_made_suppliers.view.*
import kotlinx.android.synthetic.main.fragment_home_made_suppliers.view.imgSearch
import kotlinx.android.synthetic.main.fragment_home_made_suppliers.view.rvList
import kotlinx.android.synthetic.main.fragment_home_made_suppliers.view.txtNoDataFound
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class HomeMadeSuppliersFragment : Fragment() {
    var mView: View?=null
    var nameAdapter: NameListAdapter?=null
    var catNameList=ArrayList<CategoryName>()
    var allCatNameList=ArrayList<CategoryName>()
    var allCatList=ArrayList<Categories>()
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var account_types_id:String="3"
    private var search_keyword : String = ""
    private var queryMap = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_home_made_suppliers, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
            setUpViews()
            //setNameTab()

//        }
        
        return mView
    }
    private fun setUpViews() {

       /* requireActivity().frag_other_backImg.visibility=View.VISIBLE*/
        requireActivity().frag_other_backImg.visibility=View.GONE
        mView!!.imgSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_color), android.graphics.PorterDuff.Mode.SRC_IN)

        /*requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.homeFragment)
        }*/

        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("account_types_id", account_types_id)
        queryMap.put("search", "")
        queryMap.put("country_id", "")
        getNamesAndCategories(queryMap)

        mView!!.textSearch1.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    search_keyword = mView!!.textSearch1.text.toString().trim()
                    SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(),  mView!!.textSearch1)
                    //  getAllServicesListing(latitude!!, longitude!!, 0,"", search_keyword, 1)
                    if (TextUtils.isEmpty(search_keyword)){
                        LogUtils.shortToast(requireContext(), getString(R.string.please_enter_search_keyword_for_searching))
                    }else{
                        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
                        queryMap.put("account_types_id", account_types_id)
                        queryMap.put("search", search_keyword)
                        queryMap.put("country_id", "")
                        getNamesAndCategories(queryMap)
                    }
                    return true
                }
                return false
            }

        })


        /*mView!!.nameLayout.setOnClickListener {
            mView!!.nameLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setNameTab()
        }
        mView!!.categoryLayout.setOnClickListener {
            mView!!.categoryLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setCategoryTab()
        }*/

/*        mView!!.loc_view.setOnClickListener {
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
        }*/

        mView!!.loc_view_home_made_suppliers.setOnClickListener {
            findNavController().navigate(R.id.locationFragment)
        }

        mView!!.iv_home_made_filter.setOnClickListener {
           /* val filterBottomSheetDialogFragment = FilterBottomSheetDialogFragment.newInstance(requireContext())
            filterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, FilterBottomSheetDialogFragment.TAG)
            filterBottomSheetDialogFragment.setFilterClickListenerCallback(object : FilterBottomSheetDialogFragment.OnFilterClick{
                override fun onFilter(queryMap : HashMap<String, String>) {
                    *//*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*//*
                }
            })*/

            val healthAndBeautyFilterBottomSheetDialogFragment = HealthAndBeautyFilterBottomSheetDialogFragment.newInstance(requireContext(), account_types_id)
            healthAndBeautyFilterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, FilterBottomSheetDialogFragment.TAG)
            healthAndBeautyFilterBottomSheetDialogFragment.setFilterClickListenerCallback(object : HealthAndBeautyFilterBottomSheetDialogFragment.OnFilterClick {
                override fun onFilter(queryMap : HashMap<String, String>) {
                    /*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*/
                    this@HomeMadeSuppliersFragment.queryMap = queryMap
                    getNamesAndCategories(queryMap)
                }
            })
        }
    }

    private fun getNamesAndCategories(queryMap: HashMap<String, String>) {
        mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.VISIBLE
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_types_id", "search", "country_id"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                        queryMap["account_types_id"].toString(),
                        queryMap["search"].toString(),
                        queryMap["country_id"].toString()))
        val call = apiInterface.getNamesAndCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val usersData = jsonObject.getJSONArray("usersData")
                        catNameList.clear()
                        if(usersData.length() != 0) {
                            mView!!.txtNoDataFound.visibility=View.GONE
                            mView!!.rvList.visibility=View.VISIBLE
                            for (i in 0 until usersData.length()) {
                                val jsonObj = usersData.getJSONObject(i)
                                val cate = CategoryName()
                                cate.user_id = jsonObj.getInt("user_id")
                                cate.name = jsonObj.getString("name")
                                cate.categories = jsonObj.getString("categories")
                                cate.categories_ar = jsonObj.getString("categories_ar")
                                cate.country_name_ar = jsonObj.getString("country_name_ar")
                                cate.country_served_name_ar = jsonObj.getString("country_served_name_ar")
                                cate.rating = jsonObj.getDouble("rating")
                                cate.profile_picture = jsonObj.getString("profile_picture")
                                cate.country_name = jsonObj.getString("country_name")
                                cate.country_served_name = jsonObj.getString("country_served_name")
                                cate.country_id = jsonObj.getInt("country_id")
                                cate.like= jsonObj.getBoolean("like")
                                catNameList.add(cate)

                            }

                            /*nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
                                override fun clickPostionType(pos: Int, type: String) {
                                    if(type=="Like"){
                                        likeUnlike(pos)
                                    }
                                    else{
                                        val bundle=Bundle()
                                        bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                                        findNavController().navigate(R.id.action_homeMadeSuppliersFragment_to_supplierDetailsFragment, bundle)
                                    }
                                }

                            })
                            mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
                            mView!!.rvList.adapter=nameAdapter*/
                        }
                        else{
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
                        }
                        allCatNameList.clear()
                        allCatNameList.addAll(catNameList)
                        nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
                            override fun clickPostionType(pos: Int, type: String) {
                                if(type=="Like"){
                                    likeUnlike(pos)
                                }
                                else{
                                    val bundle=Bundle()
                                    bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                                    findNavController().navigate(R.id.action_homeMadeSuppliersFragment_to_supplierDetailsFragment, bundle)
                                }
                            }

                        })
                        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
                        mView!!.rvList.adapter=nameAdapter
                        nameAdapter!!.notifyDataSetChanged()

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
                mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }


    private fun setNameTab() {
        if(!TextUtils.isEmpty(mView!!.textSearch1.text.toString())){
            mView!!.textSearch1.setText("")
        }
        nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Like"){
                    likeUnlike(pos)
                }
                else{
                    val bundle=Bundle()
                    bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                    findNavController().navigate(R.id.action_homeMadeSuppliersFragment_to_supplierDetailsFragment, bundle)
                }
            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=nameAdapter

        /*getNamesAndCategories(queryMap)*/

    }
    private fun searchItem(s: String) {
/*        if(mView!!.nameView.isSelected) {
            if (!TextUtils.isEmpty(s)) {
                val searchList = ArrayList<CategoryName>()
                for (i in 0 until allCatNameList.size) {
                    if (allCatNameList[i].name.contains(s, true)) {
                        searchList.add(allCatNameList[i])
                    }
                }
                catNameList.clear()
                catNameList.addAll(searchList)
            } else {
                catNameList.clear()
                catNameList.addAll(allCatNameList)
            }
            nameAdapter.notifyDataSetChanged()
        }
        else{
            if (!TextUtils.isEmpty(s)) {
                val searchList = ArrayList<Categories>()
                for (i in 0 until allCatList.size) {
                    if (allCatList[i].name.contains(s, true)) {
                        searchList.add(allCatList[i])
                    }
                }
                categoryList.clear()
                categoryList.addAll(searchList)
            } else {
                categoryList.clear()
                categoryList.addAll(allCatList)
            }
            categoryListAdapter.notifyDataSetChanged()
        }*/

        if (!TextUtils.isEmpty(s)) {
            val searchList = ArrayList<CategoryName>()
            for (i in 0 until allCatNameList.size) {
                if (allCatNameList[i].name.contains(s, true)) {
                    searchList.add(allCatNameList[i])
                }
            }
            catNameList.clear()
            catNameList.addAll(searchList)
        } else {
            catNameList.clear()
            catNameList.addAll(allCatNameList)
        }

        nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Like"){
                    likeUnlike(pos)
                }
                else{
                    val bundle=Bundle()
                    bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                    findNavController().navigate(R.id.action_homeMadeSuppliersFragment_to_supplierDetailsFragment, bundle)
                }
            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=nameAdapter
        nameAdapter!!.notifyDataSetChanged()
    }
    private fun likeUnlike(pos: Int) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
//                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
            val args=Bundle()
            args.putString("reference", "HomeMadeSuppliers")
//            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            requireContext().startActivity(Intent(requireContext(), LoginActivity::class.java).putExtras(args))
            return
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_type_user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), catNameList[pos].user_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.likeUnlike(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            catNameList[pos].like = !catNameList[pos].like
                            nameAdapter!!.notifyDataSetChanged()

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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBarHomeMadeSuppliersFragment.visibility = View.GONE
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
        requireActivity().home_frag_categories.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.VISIBLE
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