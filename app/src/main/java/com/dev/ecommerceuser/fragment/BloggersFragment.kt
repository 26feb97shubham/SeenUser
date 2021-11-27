package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.CategoryListAdapter
import com.dev.ecommerceuser.adapter.NameListAdapter
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.Categories
import com.dev.ecommerceuser.model.CategoryName
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_bloggers.view.*
import kotlinx.android.synthetic.main.fragment_bloggers.view.imgSearch
import kotlinx.android.synthetic.main.fragment_bloggers.view.progressBar
import kotlinx.android.synthetic.main.fragment_bloggers.view.rvList
import kotlinx.android.synthetic.main.fragment_bloggers.view.txtNoDataFound

import kotlinx.android.synthetic.main.fragment_brands.view.*
import kotlinx.android.synthetic.main.fragment_home_made_suppliers.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.QueryMap
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BloggersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BloggersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    lateinit var nameAdapter: NameListAdapter
    var catNameList=ArrayList<CategoryName>()
    var categoryList=ArrayList<Categories>()
    lateinit var categoryListAdapter: CategoryListAdapter
    var account_types_id:String="1"
    var allCatNameList=ArrayList<CategoryName>()
    var allCatList=ArrayList<Categories>()
    private var search_keyword : String = ""
    private var queryMap = HashMap<String, String>()
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
            mView = inflater.inflate(R.layout.fragment_bloggers, container, false)
            setUpViews()
            return mView
    }
    private fun setUpViews() {

        requireActivity().frag_other_backImg.visibility=View.GONE
        mView!!.imgSearch.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_color), android.graphics.PorterDuff.Mode.SRC_IN)

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.homeFragment)
        }

        queryMap.put("user_id", SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.UserId, 0).toString())
        queryMap.put("account_types_id", account_types_id)
        queryMap.put("search", "")
        queryMap.put("country_id", "")
        getNamesAndCategories(queryMap)


        mView!!.bloggers_textSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    search_keyword = mView!!.bloggers_textSearch.text.toString().trim()
                    SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(),  mView!!.bloggers_textSearch)
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

        mView!!.iv_bloggers_loc.setOnClickListener {
            mView!!.iv_bloggers_loc.startAnimation(AlphaAnimation(1f, 0.5f))
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

        mView!!.iv_bloggers_filter.setOnClickListener {
            val healthAndBeautyFilterBottomSheetDialogFragment = HealthAndBeautyFilterBottomSheetDialogFragment.newInstance(requireContext(), account_types_id)
            healthAndBeautyFilterBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, FilterBottomSheetDialogFragment.TAG)
            healthAndBeautyFilterBottomSheetDialogFragment.setFilterClickListenerCallback(object : HealthAndBeautyFilterBottomSheetDialogFragment.OnFilterClick {
                override fun onFilter(queryMap : HashMap<String, String>) {
                    /*findNavController().navigate(R.id.action_filterbottomsheetdialogfragment_to_filteredproductsfragment)*/
                    this@BloggersFragment.queryMap = queryMap
                    getNamesAndCategories(queryMap)
                }
            })
        }

    }
    private fun getNamesAndCategories(queryMap: HashMap<String, String>) {
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_types_id", "search", "country_id"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                        queryMap["account_types_id"].toString(),
                        queryMap["search"].toString(),
                        queryMap["country_id"].toString()))

        val call = apiInterface.getNamesAndCategories(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
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
                                cate.rating = jsonObj.getDouble("rating")
                                cate.profile_picture = jsonObj.getString("profile_picture")
                                cate.country_name = jsonObj.getString("country_name")
                                cate.country_served_name = jsonObj.getString("country_served_name")
                                cate.country_id = jsonObj.getInt("country_id")
                                cate.like= jsonObj.getBoolean("like")
                                catNameList.add(cate)

                                nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
                                    override fun clickPostionType(pos: Int, type: String) {
                                        if(type=="Like"){
                                            likeUnlike(pos)
                                        }
                                        else{
                                            val bundle=Bundle()
                                            bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                                            findNavController().navigate(R.id.action_bloggersFragment_to_supplierDetailsFragment, bundle)
                                        }
                                    }

                                })

                                mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
                                mView!!.rvList.adapter=nameAdapter


                            }
                        }
                        else{
                            mView!!.txtNoDataFound.visibility=View.VISIBLE
                            mView!!.rvList.visibility=View.GONE
                        }
                        allCatNameList.clear()
                        allCatNameList.addAll(catNameList)
                        nameAdapter.notifyDataSetChanged()

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

    private fun likeUnlike(pos: Int) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
//                    startActivity(Intent(requireContext(), ChooseLoginSignUpActivity::class.java))
            val args=Bundle()
            args.putString("reference", "HomeMadeSuppliers")
            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            return
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "account_type_user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), catNameList[pos].user_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.likeUnlike(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            catNameList[pos].like = !catNameList[pos].like
                            nameAdapter.notifyDataSetChanged()

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMadeSuppliersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeMadeSuppliersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}