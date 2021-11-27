package com.dev.ecommerceuser.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.AccountTypeAdapter
import com.dev.ecommerceuser.adapter.CategoryListAdapter
import com.dev.ecommerceuser.adapter.CategoryNameListAdapter
import com.dev.ecommerceuser.adapter.CountriesListDataAdapter
import com.dev.ecommerceuser.dialog.LogoutDialog
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.*
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.dev.ecommerceuser.utils.Utility.Companion.apiInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.filter_bottom_sheet_dialog.*
import kotlinx.android.synthetic.main.filter_bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.frag_profile.view.*
import kotlinx.android.synthetic.main.fragment_bloggers.view.*
import kotlinx.android.synthetic.main.fragment_bloggers.view.progressBar
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var apply_filter_tv: TextView? = null
    private val mainView: FrameLayout? = null
    private var onFilterClickallback: OnFilterClick? = null
    var catNameList=ArrayList<String>()
    var categoryList=ArrayList<Categories>()
    var countryList = ArrayList<CountriesItem>()
    var accTypeList = ArrayList<AccountTypesItem>()
    var allCatNameList=ArrayList<CategoryName>()
    var allCatList=ArrayList<Categories>()
    private var category_id:String = ""
    private var country_id:String = ""
    private var acc_type_id:String = ""
    private var country_code= java.util.ArrayList<String>()
    var cCodeList= arrayListOf<String>()
    lateinit var categoryListAdapter: CategoryNameListAdapter
    lateinit var countriesListDataAdapter: CountriesListDataAdapter
    lateinit var accountTypeAdapter: AccountTypeAdapter
    private var category_clicked = false
    private var country_clicked = false
    private var price_filter_clicked = false
    private var acc_type_clicked = false
    private var price = 0
    private var price_from : String = ""
    private var price_to : String = ""
    private var mView : View?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.filter_bottom_sheet_dialog, container, false)
        apply_filter_tv = mView!!.findViewById(R.id.apply_filter_tv)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCategories()
//        setCategory()
        getCountries()
        //setCountry()
        getAccType()
        //setAccType()
        mView!!.category_layout.setOnClickListener {
            if (!category_clicked){
                category_clicked = true
                mView!!.ll_categories_list.visibility = View.VISIBLE
            }else{
                category_clicked = false
                mView!!.ll_categories_list.visibility = View.GONE
            }

        }

        mView!!.brand_layout.setOnClickListener {
            if (!acc_type_clicked){
                acc_type_clicked = true
                mView!!.ll_type_list.visibility = View.VISIBLE
            }else{
                acc_type_clicked = false
                mView!!.ll_type_list.visibility = View.GONE
            }
        }

        mView!!.emi_filter_layout.setOnClickListener {
            if (!country_clicked){
                country_clicked = true
                mView!!.ll_countries_list.visibility = View.VISIBLE
            }else{
                country_clicked = false
                mView!!.ll_countries_list.visibility = View.GONE
            }
        }

        mView!!.price_filter_layout.setOnClickListener {
            if (!price_filter_clicked){
                price_filter_clicked = true
                mView!!.ll_price_filter.visibility = View.VISIBLE
            }else{
                price_filter_clicked = false
                mView!!.ll_price_filter.visibility = View.GONE
            }
        }

        mView!!.tv_lowest_to_highest.setOnClickListener {
            price = 0
            mView!!.price_filter_tv.text =    mView!!.tv_lowest_to_highest.text
            mView!!.ll_price_filter.visibility = View.GONE
        }

        mView!!.tv_highest_to_lowest.setOnClickListener {
            price = 1
            mView!!.price_filter_tv.text =    mView!!.tv_highest_to_lowest.text
            mView!!.ll_price_filter.visibility = View.GONE
        }


        mView!!.apply_filter_tv!!.setOnClickListener { // dismiss dialog
            /*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*/
            price_from =    mView!!.from_tv.text.toString().trim()
            price_to =    mView!!.to_tv.text.toString().trim()
            val queryMap = HashMap<String, String>()
            queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
            queryMap.put("device_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.DeviceId, ""))
            queryMap.put("search", "")
            queryMap.put("category_id", category_id)
            queryMap.put("account_type", acc_type_id)
            queryMap.put("country_id", country_id)
            queryMap.put("price", price.toString())
            queryMap.put("price_from", price_from)
            queryMap.put("price_to", price_to)
            onFilterClickallback!!.onFilter(queryMap)
            dismiss()
        }
    }

    private fun getAccType() {
        val call = apiInterface.getAccType()
        call?.enqueue(object : Callback<AccountTypeResponse?>{
            override fun onResponse(call: Call<AccountTypeResponse?>, response: Response<AccountTypeResponse?>) {
                try {
                    if (response.body() != null) {
                        accTypeList = response.body()!!.account_types as ArrayList<AccountTypesItem>
//                        txtCountryCode.text=country_code[0]
                        if (accTypeList.size!=0){
                            mView!!.txtNoDataFound_type.visibility = View.GONE
                            mView!!.rv_type_list.visibility = View.VISIBLE
                            mView!!.rv_type_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            accountTypeAdapter= AccountTypeAdapter(requireContext(), accTypeList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    acc_type_id = accTypeList[pos].id.toString()
                                    mView!!.ll_type_list.visibility = View.GONE
                                    mView!!.brand_tv.text = accTypeList[pos].name
                                }

                            })
                            mView!!.rv_type_list.adapter=accountTypeAdapter
                            accountTypeAdapter.notifyDataSetChanged()
                        }else{
                            mView!!.txtNoDataFound_type.visibility = View.VISIBLE
                            mView!!.rv_type_list.visibility = View.GONE
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

    private fun setAccType() {
        mView!!.rv_type_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        accountTypeAdapter= AccountTypeAdapter(requireContext(), accTypeList,  object : ClickInterface.ClickPositionInterface{
            override fun clickPostion(pos: Int) {
                acc_type_id = accTypeList[pos].id.toString()
                mView!!.ll_type_list.visibility = View.GONE
                mView!!.brand_tv.text = accTypeList[pos].name
            }

        })
        mView!!.rv_type_list.adapter=accountTypeAdapter
        accountTypeAdapter.notifyDataSetChanged()
    }

    private fun getCountries() {
        val call = apiInterface.getCountriesList()
        call!!.enqueue(object : Callback<CountryListResponse?> {
            override fun onResponse(call: Call<CountryListResponse?>, response: Response<CountryListResponse?>) {
                try {
                    if (response.body() != null) {
                        countryList = response.body()!!.countries as ArrayList<CountriesItem>
//                        txtCountryCode.text=country_code[0]
                        if (countryList.size!=0){
                            mView!!.txtNoDataFound_countries.visibility = View.GONE
                            mView!!.rv_countries_list.visibility = View.VISIBLE
                            mView!!.rv_countries_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            countriesListDataAdapter= CountriesListDataAdapter(requireContext(), countryList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    country_id = countryList[pos].id.toString()
                                    mView!!.ll_countries_list.visibility = View.GONE
                                    mView!!.emi_filter_tv.text = countryList[pos].country_name
                                }

                            })
                            mView!!.rv_countries_list.adapter=countriesListDataAdapter
                            countriesListDataAdapter.notifyDataSetChanged()
                        }else{
                            mView!!.txtNoDataFound_countries.visibility = View.VISIBLE
                            mView!!.rv_countries_list.visibility = View.GONE
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

            override fun onFailure(call: Call<CountryListResponse?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
            }
        })
    }

    private fun getCategories() {
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val categories = jsonObject.getJSONArray("categories")
                        categoryList.clear()
                        catNameList.clear()
                        if(categories.length() != 0) {
                            mView!!.txtNoDataFound_categories.visibility = View.GONE
                            mView!!.rv_categories_list.visibility = View.VISIBLE

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

                            mView!!.rv_categories_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            categoryListAdapter= CategoryNameListAdapter(requireContext(), categoryList,  object : ClickInterface.ClickPositionInterface{
                                override fun clickPostion(pos: Int) {
                                    category_id = categoryList[pos].id.toString()
                                    mView!!.ll_categories_list.visibility = View.GONE
                                    mView!!.category_tv.text = categoryList[pos].name
                                }
                            })
                            mView!!.rv_categories_list.adapter=categoryListAdapter
                            categoryListAdapter.notifyDataSetChanged()
                        }
                        else{
                            mView!!.txtNoDataFound_categories.visibility = View.VISIBLE
                            mView!!.rv_categories_list.visibility = View.GONE
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

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
            }
        })
    }

   /* private fun setCategory() {
        mView!!.rv_categories_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
       categoryListAdapter= CategoryNameListAdapter(requireContext(), categoryList,  object : ClickInterface.ClickPositionInterface{
           override fun clickPostion(pos: Int) {
               category_id = categoryList[pos].id.toString()
               mView!!.ll_categories_list.visibility = View.GONE
               mView!!.category_tv.text = categoryList[pos].name
           }
       })
        mView!!.rv_categories_list.adapter=categoryListAdapter
        categoryListAdapter.notifyDataSetChanged()
    }*/

    fun setFilterClickListenerCallback(onFilterClickallback: OnFilterClick){
        this.onFilterClickallback = onFilterClickallback
    }

    private fun setCountry(){
        mView!!.rv_countries_list.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        countriesListDataAdapter= CountriesListDataAdapter(requireContext(), countryList,  object : ClickInterface.ClickPositionInterface{
            override fun clickPostion(pos: Int) {
                country_id = countryList[pos].id.toString()
                mView!!.ll_countries_list.visibility = View.GONE
                mView!!.emi_filter_tv.text = countryList[pos].country_name
            }

        })
        mView!!.rv_countries_list.adapter=countriesListDataAdapter
        countriesListDataAdapter.notifyDataSetChanged()
    }

    companion object {
        const val TAG = "FilterBottomSheetDialogFragment"
        fun newInstance(context: Context?): FilterBottomSheetDialogFragment {
            //this.context = context;
            return FilterBottomSheetDialogFragment()
        }
    }

    interface OnFilterClick{
        fun onFilter(queryMap : HashMap<String, String>)
    }
}