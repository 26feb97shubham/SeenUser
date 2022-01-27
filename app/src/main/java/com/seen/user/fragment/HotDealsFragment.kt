package com.seen.user.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.HotdealsAdapter
import com.seen.user.model.ProductList
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_hot_deals.view.*
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
 * Use the [HotDealsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HotDealsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var productList=ArrayList<ProductList>()
    var filteredList = ArrayList<ProductList>()
    var search_filteredList = ArrayList<ProductList>()
    var hotdealsAdapter : HotdealsAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_hot_deals, container, false)
        requireActivity().frag_other_toolbar.frag_other_backImg.visibility = View.VISIBLE
        requireActivity().frag_other_toolbar.frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_toolbar.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_toolbar.frag_other_backImg)
            findNavController().popBackStack()
        }
        getHotDeals()
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mView!!.txtLoc.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {

                setAdapter(c.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun setAdapter(name:String) {
        if(name.isNotEmpty()){
            filteredList.clear()
            for(i in 0 until productList.size){
                if(productList[i].name.contains(name, true)){
                    filteredList.add(productList[i])
                }
            }
            mView!!.filtered_items.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            hotdealsAdapter = HotdealsAdapter(
                requireContext(),
                filteredList,
                SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),
                findNavController()
            )
            mView!!.filtered_items.adapter = hotdealsAdapter
        }
        else{
            setProductAdapter()
        }

       /* filteredList = productList

        mView!!.txt_total_count_hot_deals.text = productList.size.toString()

        if(productList.size==1){
            mView!!.txt_total_count_hot_deals.text = productList.size.toString() + " item"
        }else{
            mView!!.txt_total_count_hot_deals.text = productList.size.toString() + " items"
        }
        hotdealsAdapter!!.notifyDataSetChanged()*/
    }

    override fun onResume() {
        super.onResume()
        getHotDeals()
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

    fun getHotDeals(){
        mView!!.progressBar.visibility = View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getHotDeals(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val my_response = jsonObject.getInt("response")
                        if (my_response==0){
                            mView!!.txt_no_hot_deals.visibility = View.VISIBLE
                            mView!!.txt_total_count_hot_deals.text = "0"
                            mView!!.filtered_items.visibility = View.GONE
                        }else{
                            val products = jsonObject.getJSONArray("products")
                            productList.clear()
                            if (products.length() != 0) {
                                mView!!.txt_no_hot_deals.visibility = View.GONE
                                mView!!.filtered_items.visibility = View.VISIBLE
                                for (i in 0 until products.length()) {
                                    val jsonObj = products.getJSONObject(i)
                                    val d = ProductList()
                                    d.name = jsonObj.getString("name")
                                    d.category_name = jsonObj.getString("category_name")
                                    d.supplier_name = jsonObj.getString("supplier_name")
                                    d.supplier_profile_picture = jsonObj.getString("supplier_profile_picture")
                                    d.price = jsonObj.getString("price")
                                    d.files = jsonObj.getString("files")
                                    d.all_files = jsonObj.getJSONArray("all_files")
                                    d.discount = jsonObj.getString("discount")
                                    d.rating = jsonObj.getDouble("rating")
                                    d.like = jsonObj.getBoolean("like")
                                    d.id = jsonObj.getInt("id")
                                    productList.add(d)
                                }

                                setProductAdapter()

                            } else {
                                mView!!.txt_no_hot_deals.visibility = View.VISIBLE
                                mView!!.filtered_items.visibility = View.GONE
                            }
                        }

                        //productListAdapter.notifyDataSetChanged()

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

    private fun setProductAdapter() {
        mView!!.filtered_items.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        hotdealsAdapter = HotdealsAdapter(requireContext(), productList, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(),findNavController())
        mView!!.filtered_items.adapter = hotdealsAdapter

        mView!!.txt_total_count_hot_deals.text = productList.size.toString()

        if(productList.size==1){
            mView!!.txt_total_count_hot_deals.text = productList.size.toString() + " item"
        }else{
            mView!!.txt_total_count_hot_deals.text = productList.size.toString() + " items"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HotDealsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HotDealsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}