package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.adapter.ProductListAdapter
import com.dev.ecommerceuser.interfaces.ClickInterface
import com.dev.ecommerceuser.model.ProductList
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_discount.view.*
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
 * Use the [DiscountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    lateinit var productListAdapter: ProductListAdapter
    var productList=ArrayList<ProductList>()

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
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_discount, container, false)
        setUpViews()
        getOffersAndDiscounts(false)
        return mView
    }
    private fun setUpViews() {
     /*   setToolbarIcons()*/
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_backImg.visibility=View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.homeFragment)
        }



        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
        productListAdapter= ProductListAdapter(requireContext(), productList, object : ClickInterface.ClickPosTypeInterface {
            override fun clickPostionType(pos: Int, type: String) {
                if (type == "Like") {
                    likeUnlikeProduct(pos)
                } else {
                    val bundle = Bundle()
                    bundle.putInt("product_id", productList[pos].id)
                    findNavController().navigate(R.id.action_discountFragment_to_productDetailsFragment, bundle)
                }

            }

        })
        mView.rvList.adapter=productListAdapter

        mView.swipeRefresh.setOnRefreshListener {
            getOffersAndDiscounts(true)
        }
    }

    private fun setToolbarIcons() {
        requireActivity().toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gold))
        requireActivity().frag_other_backImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
        requireActivity().notificationImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
        requireActivity().menuImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun getOffersAndDiscounts(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getOffersAndDiscounts(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (mView.swipeRefresh.isRefreshing) {
                    mView.swipeRefresh.isRefreshing = false
                }
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val products = jsonObject.getJSONArray("products")
                        productList.clear()
                        if (products.length() != 0) {
                            mView.txtNoDataFound.visibility = View.GONE
                            mView.rvList.visibility = View.VISIBLE
                            for (i in 0 until products.length()) {
                                val jsonObj = products.getJSONObject(i)
                                val d = ProductList()
                                d.name = jsonObj.getString("name")
                                d.category_name = jsonObj.getString("category_name")
                                d.supplier_name = jsonObj.getString("supplier_name")
                                d.price = jsonObj.getString("price")
                                d.quantity = jsonObj.getInt("quantity")
                                d.id = jsonObj.getInt("id")
                                d.files = jsonObj.getString("files")
                                d.discount = jsonObj.getString("discount")
                                d.rating = jsonObj.getDouble("rating")
                                d.like = jsonObj.getBoolean("like")
                                productList.add(d)

                            }
                            mView.tv_no_of_items.text = productList.size.toString()
                        } else {
                            mView.tv_no_of_items.text = "0"
                            mView.txtNoDataFound.visibility = View.VISIBLE
                            mView.rvList.visibility = View.GONE
                        }

                        productListAdapter.notifyDataSetChanged()

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
                if (mView.swipeRefresh.isRefreshing) {
                    mView.swipeRefresh.isRefreshing = false
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

                        if (jsonObject.getInt("response") == 1) {
                            productList[pos].like = !productList[pos].like
                            productListAdapter.notifyDataSetChanged()

                        } else {
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


   /* override fun onStop() {
        super.onStop()
        setDefaultToolbarIcons()
    }*/

    private fun setDefaultToolbarIcons() {
        requireActivity().toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        requireActivity().frag_other_backImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gold))
        requireActivity().notificationImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gold))
        requireActivity().menuImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gold))
    }

}