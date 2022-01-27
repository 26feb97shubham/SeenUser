package com.seen.user.fragment

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
import com.seen.user.adapter.NameListAdapter
import com.seen.user.adapter.ProductListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.CategoryName
import com.seen.user.model.ProductList
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_favourites.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
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
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    lateinit var nameAdapter: NameListAdapter
    var catNameList=ArrayList<CategoryName>()
    var productList=ArrayList<ProductList>()
    lateinit var productListAdapter: ProductListAdapter
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
        // Inflate the layout for this fragment
//        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_favourites, container, false)
            setUpViews()
            setSupplierTab()

//        }

        return mView
    }
    private fun setUpViews() {

        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView!!.swipeRefresh.setOnRefreshListener {
            getFavouritesSuppliersAndProducts(true)
        }



        mView!!.supplierLayout.setOnClickListener {
            mView!!.supplierLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setSupplierTab()
        }
        mView!!.productLayout.setOnClickListener {
            mView!!.productLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setProductTab()
        }

    }
    private fun getFavouritesSuppliersAndProducts(isReferesh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isReferesh) {
            mView!!.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getFavouritesSuppliersAndProducts(builder.build())
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
                        if(mView!!.supplierView.isSelected){
                            val suppliers = jsonObject.getJSONArray("suppliers")
                            catNameList.clear()
                            if(suppliers.length() != 0) {
                                mView!!.txtNoDataFound.visibility=View.GONE
                                mView!!.rvList.visibility=View.VISIBLE
                                for (i in 0 until suppliers.length()) {
                                    val jsonObj = suppliers.getJSONObject(i)
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

                                }
                            }
                            else{
                                mView!!.txtNoDataFound.visibility=View.VISIBLE
                                mView!!.rvList.visibility=View.GONE
                            }
                            nameAdapter.notifyDataSetChanged()
                        }
                        else{
                            val products = jsonObject.getJSONArray("products")
                            productList.clear()
                            if(products.length() != 0) {
                                mView!!.txtNoDataFound.visibility = View.GONE
                                mView!!.rvList.visibility = View.VISIBLE

                                for (i in 0 until products.length()) {
                                    val jsonObj = products.getJSONObject(i)
                                    val d = ProductList()
                                    d.name = jsonObj.getString("name")
                                    d.id = jsonObj.getInt("id")
                                    d.files = jsonObj.getString("files")
                                    d.category_name = jsonObj.getString("category_name")
                                    d.supplier_name = jsonObj.getString("supplier_name")
                                    d.supplier_profile_picture = jsonObj.getString("supplier_profile_picture")
                                    d.price = jsonObj.getString("price")
                                    d.discount = jsonObj.getString("discount")
                                    d.quantity = jsonObj.getInt("quantity")
                                    d.rating = jsonObj.getDouble("rating")
                                    d.like = jsonObj.getBoolean("like")
                                    productList.add(d)

                                }
                            }
                            else{
                                mView!!.txtNoDataFound.visibility = View.VISIBLE
                                mView!!.rvList.visibility = View.GONE
                            }
                            productListAdapter.notifyDataSetChanged()
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

    private fun setProductTab() {
        mView!!.supplierView.isSelected=false
        mView!!.productView.isSelected=true
        productListAdapter= ProductListAdapter(requireContext(), productList, "fav", object :ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Like"){
                    likeUnlikeProduct(pos)
                }
                else{
                    val bundle = Bundle()
                    bundle.putInt("product_id", productList[pos].id)
                    findNavController().navigate(R.id.action_favouritesFragment_to_productDetailsFragment, bundle)
                }
            }

        })
        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=productListAdapter

        getFavouritesSuppliersAndProducts(false)

    }

    private fun setSupplierTab() {
        mView!!.supplierView.isSelected=true
        mView!!.productView.isSelected=false
        nameAdapter= NameListAdapter(requireContext(), catNameList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Like"){
                    likeUnlike(pos)
                }
                else{
                    val bundle=Bundle()
                    bundle.putInt("supplier_user_id", catNameList[pos].user_id)
                    findNavController().navigate(R.id.action_favouritesFragment_to_supplierDetailsFragment, bundle)
                }
            }

        })

        mView!!.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView!!.rvList.adapter=nameAdapter

        getFavouritesSuppliersAndProducts(false)

    }
    private fun likeUnlike(pos: Int) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
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
//                            getFavouritesSuppliersAndProducts(false)

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
    private fun likeUnlikeProduct(pos: Int) {
        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
            LogUtils.shortToast(requireContext(), getString(R.string.please_login_signup_to_access_this_functionality))
            val args=Bundle()
            args.putString("reference", "OffersDiscount")
            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            return
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), productList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.likeUnlikeProduct(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            productList[pos].like = !productList[pos].like
                            productListAdapter.notifyDataSetChanged()
//                            getFavouritesSuppliersAndProducts(false)

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
}