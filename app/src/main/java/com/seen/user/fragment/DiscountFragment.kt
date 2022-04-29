package com.seen.user.fragment

import android.content.Intent
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
import com.seen.user.R
import com.seen.user.activity.LoginActivity
import com.seen.user.adapter.ProductListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductList
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_discount.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DiscountFragment : Fragment() {

    lateinit var mView:View
    lateinit var productListAdapter: ProductListAdapter
    var productList=ArrayList<ProductList>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_discount, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
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
                                d.category_name_ar = jsonObj.getString("category_name_ar")
                                d.supplier_name = jsonObj.getString("supplier_name")
                                d.supplier_profile_picture = jsonObj.getString("supplier_profile_picture")
                                d.supplier_id = jsonObj.getInt("supplier_id")
                                d.price = jsonObj.getString("price")
                                d.original_price = jsonObj.getString("original_price")
                                d.quantity = jsonObj.getInt("quantity")
                                d.id = jsonObj.getInt("id")
                                d.files = jsonObj.getString("files")
                                d.discount = jsonObj.getString("discount")
                                d.rating = jsonObj.getDouble("rating")
                                d.like = jsonObj.getBoolean("like")
                                productList.add(d)

                            }
                            if(productList.size==1){
                                mView.tv_no_of_items.text = productList.size.toString() + " "+requireContext().getString(R.string.item)
                            }else{
                                mView.tv_no_of_items.text = productList.size.toString() + " "+requireContext().getString(R.string.items)
                            }
                        } else {
                            mView.tv_no_of_items.text = "0"
                            mView.txtNoDataFound.visibility = View.VISIBLE
                            mView.rvList.visibility = View.GONE
                        }

                        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
                        productListAdapter= ProductListAdapter(requireContext(), productList, "discount", object : ClickInterface.ClickPosTypeInterface {
                            override fun clickPostionType(pos: Int, type: String) {
                                if (type == "Like") {
                                    likeUnlikeProduct(pos)
                                }else if(type == "Supplier"){
                                    val bundle = Bundle()
                                    bundle.putInt("supplier_user_id", productList[pos].supplier_id)
                                    findNavController().navigate(R.id.supplierDetailsFragment, bundle)
                                }

                                else {
                                    val bundle = Bundle()
                                    bundle.putInt("product_id", productList[pos].id)
                                    findNavController().navigate(R.id.action_discountFragment_to_productDetailsFragment, bundle)
                                }

                            }

                        })
                        mView.rvList.adapter=productListAdapter

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
//            findNavController().navigate(R.id.chooseLoginSingUpFragment, args)
            requireContext().startActivity(Intent(requireContext(), LoginActivity::class.java).putExtras(args))
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