package com.seen.user.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.activity.LoginActivity
import com.seen.user.adapter.GlobalItemListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Supplier
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_global_market_details.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class GlobalMarketDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var name: String=""
    private var flag: String=""
    private var countryId: String=""
//    private var countryId:Int=0

    var mView:View?=null
    lateinit var globalItemListAdapter:GlobalItemListAdapter
    var supplierList=ArrayList<Supplier>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name").toString()
            flag = it.getString("flag").toString()
            countryId = it.getString("ref_key").toString()
            //countryId = it.getInt("countryId", "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
           mView = inflater.inflate(R.layout.fragment_global_market_details, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
           setUpViews()
           getGlobalMarketSuppliers(false)
           return mView
    }

    private fun setUpViews() {
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_backImg.visibility= View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }
        mView!!.swipeRefresh.setOnRefreshListener {
            getGlobalMarketSuppliers(true)
        }
        if(countryId=="1"){
            mView!!.txtName.text=getString(R.string.united_arab_emirates)
            mView!!.imgLogo.setImageResource(R.drawable.uae)
        }
        else if(countryId=="2"){
            mView!!.txtName.text=getString(R.string.saudi_arabia)
            mView!!.imgLogo.setImageResource(R.drawable.saudi_arabia)
        }
        else if(countryId=="3"){
            mView!!.txtName.text=getString(R.string.oman)
            mView!!.imgLogo.setImageResource(R.drawable.oman)
        }
        else if(countryId=="4"){
            mView!!.txtName.text=getString(R.string.kuwait)
            mView!!.imgLogo.setImageResource(R.drawable.kuwait)
        }
        else if(countryId=="5"){
            mView!!.txtName.text=getString(R.string.bahrain)
            mView!!.imgLogo.setImageResource(R.drawable.bahrain)
        }else{
            mView!!.txtName.text=getString(R.string.qatar)
            mView!!.imgLogo.setImageResource(R.drawable.qatar)
        }
    }
    private fun getGlobalMarketSuppliers(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView!!.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "country_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), countryId.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getGlobalMarketSuppliers(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val usersData = jsonObject.getJSONArray("usersData")
                        supplierList.clear()
                        if(usersData.length() != 0) {
                            mView!!.txtNoDataFound.visibility=View.GONE
                            mView!!.rvList.visibility=View.VISIBLE
                            for (i in 0 until usersData.length()) {
                                val jsonObj = usersData.getJSONObject(i)
                                val s = Supplier()
                                s.user_id = jsonObj.getInt("user_id")
                                s.name = jsonObj.getString("name")
                                s.categories = jsonObj.getString("categories")
                                s.categories_ar = jsonObj.getString("categories_ar")
                                s.rating = jsonObj.getDouble("rating")
                                s.profile_picture = jsonObj.getString("profile_picture")
                                s.country_name = jsonObj.getString("country_name")
                                s.country_id = jsonObj.getInt("country_id")
                                s.like= jsonObj.getBoolean("like")
                                supplierList.add(s)
                            }
                            mView!!.rvList.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            globalItemListAdapter= GlobalItemListAdapter(requireContext(), supplierList, object : ClickInterface.ClickPosTypeInterface{
                                override fun clickPostionType(pos: Int, type: String) {
                                    if(type=="Like"){
                                        likeUnlike(pos)
                                    }
                                    else{
                                        val bundle=Bundle()
                                        bundle.putInt("supplier_user_id", supplierList[pos].user_id)
                                        findNavController().navigate(R.id.action_globalMarketDetailsFragment_to_supplierDetailsFragment, bundle)
                                    }
                                }

                            })
                            mView!!.rvList.adapter=globalItemListAdapter
                            globalItemListAdapter.notifyDataSetChanged()
                        }
                        else{
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
                        }
                        globalItemListAdapter.notifyDataSetChanged()

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
                if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun likeUnlike(pos: Int) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
//                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
            val args=Bundle()
            args.putString("reference", "GlobalMarketDetails")
//            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            requireContext().startActivity(Intent(requireContext(), LoginActivity::class.java).putExtras(args))
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_type_user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), supplierList[pos].user_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.likeUnlike(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            supplierList[pos].like = !supplierList[pos].like
                            globalItemListAdapter.notifyDataSetChanged()

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
                mView!!.progressBar.visibility = View.GONE
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