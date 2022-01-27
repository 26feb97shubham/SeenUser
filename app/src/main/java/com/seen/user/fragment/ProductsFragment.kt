package com.seen.user.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.ProductListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_products.view.*
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
 * Use the [ProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var mView:View
    lateinit var productListAdapter: ProductListAdapter
    var productList=ArrayList<ProductList>()
    var allProductList=ArrayList<ProductList>()
    var supplier_user_id:Int=0
    var category_id:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            supplier_user_id = it.getInt("supplier_user_id", 0)
           // category_id =  it.getInt("category_id", 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_products, container, false)
        setUpViews()
        getProducts(false)
        return mView
    }
    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchItem(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
        productListAdapter= ProductListAdapter(requireContext(), productList, "pro", object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Like"){
                    likeUnlikeProduct(pos)
                }
                else{
                    val bundle = Bundle()
                    bundle.putInt("product_id", productList[pos].id)
                    findNavController().navigate(R.id.action_productsFragment_to_productDetailsFragment, bundle)
                }

            }

        })
        mView.rvList.adapter=productListAdapter

        mView.swipeRefresh.setOnRefreshListener {
            getProducts(true)
        }
    }

    private fun searchItem(s: String) {
        if (!TextUtils.isEmpty(s)) {
            val searchList = ArrayList<ProductList>()
            for (i in 0 until allProductList.size) {
                if (allProductList[i].name.contains(s, true)) {
                    searchList.add(allProductList[i])
                }
            }
            productList.clear()
            productList.addAll(searchList)
        } else {
            productList.clear()
            productList.addAll(allProductList)
        }
        productListAdapter.notifyDataSetChanged()
    }

    private fun getProducts(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "supplier_user_id", "category_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), supplier_user_id.toString(), category_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getProducts(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val products = jsonObject.getJSONArray("products")
                        productList.clear()
                        if(products.length() != 0) {
                            mView.txtNoDataFound.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                            for (i in 0 until products.length()) {
                                val jsonObj = products.getJSONObject(i)
                                val d = ProductList()
                                d.name = jsonObj.getString("name")
                                d.category_name = jsonObj.getString("category_name")
                                d.supplier_name = jsonObj.getString("supplier_name")
                                d.supplier_profile_picture = jsonObj.getString("supplier_profile_picture")
                                d.price = jsonObj.getString("price")
                                d.quantity = jsonObj.getInt("quantity")
                                d.id = jsonObj.getInt("id")
                                d.files = jsonObj.getString("files")
                                d.discount = jsonObj.getString("discount")
                                d.rating = jsonObj.getDouble("rating")
                                d.like = jsonObj.getBoolean("like")
                                productList.add(d)

                            }
                            allProductList.clear()
                            allProductList.addAll(productList)
                        }
                        else{
                            mView.txtNoDataFound.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }

                        productListAdapter.notifyDataSetChanged()

                    }else{
                        mView.txtNoDataFound.visibility=View.VISIBLE
                        mView.rvList.visibility=View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    mView.txtNoDataFound.visibility=View.VISIBLE
                    mView.rvList.visibility=View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    mView.txtNoDataFound.visibility=View.VISIBLE
                    mView.rvList.visibility=View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.txtNoDataFound.visibility=View.VISIBLE
                    mView.rvList.visibility=View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
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
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), productList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.likeUnlikeProduct(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            productList[pos].like = !productList[pos].like
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            productListAdapter.notifyDataSetChanged()

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
         * @return A new instance of fragment ProductsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProductsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}