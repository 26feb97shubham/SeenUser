package com.seen.user.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.ItemListAdapter
import com.seen.user.adapter.PaymentCardsAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.CalcShippingFees
import com.seen.user.model.Cards
import com.seen.user.model.Cart
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_check_out.view.*
import kotlinx.android.synthetic.main.fragment_check_out.view.progressBar
import kotlinx.android.synthetic.main.fragment_check_out.view.taxes
import kotlinx.android.synthetic.main.fragment_check_out.view.totalDiscAmt
import kotlinx.android.synthetic.main.fragment_check_out.view.totalPrice
import kotlinx.android.synthetic.main.fragment_check_out.view.txtTotalDiscount
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class CheckOutFragment : Fragment() {

    var mView:View?=null
    lateinit var itemListAdapter: ItemListAdapter
    lateinit var cardsAdapter: PaymentCardsAdapter
    var cardList=ArrayList<Cards>()
    var cartList=ArrayList<Cart>()
    var order_data=JSONArray()
    var cart_ids=JSONArray()
    var location_id:Int=0
    var coupon_id:Int=0
    var subtotal:String=""
    var myShippingFees=0
    var myTotalPrice=0
    var myTotalDisc=0
    var myTaxes=0
    var mySubTotal=0
    var shipping_fee:String=""
    var total_discount:String=""
    var taxes:String=""
    var total_price:String=""
    var card_id:Int=0
    var supplier_id:Int=0
    var amount:Float=0f
    var allowCoupans = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        mView = inflater.inflate(R.layout.fragment_check_out, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
            myCart()
            setUpViews()
           return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility= View.VISIBLE

        underlineForChangeaddress()

        mView!!.txtChangeAddress.setOnClickListener {
            mView!!.txtChangeAddress.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_checkOutFragment_to_myLocationFragment)

        }

        mView!!.btnAddCard.setOnClickListener {
            mView!!.btnAddCard.startAnimation(AlphaAnimation(1f, .5f))
            findNavController().navigate(R.id.action_checkOutFragment_to_addCardFragment)

        }

        mView!!.txtCoupon.setOnClickListener {
            mView!!.txtCoupon.startAnimation(AlphaAnimation(1f, .5f))
            val bundle=Bundle()
            bundle.putInt("supplier_id", supplier_id)
            bundle.putString("amount", total_price)
            findNavController().navigate(R.id.action_checkOutFragment_to_applyCouponFragment, bundle)
        }

        mView!!.btnPlaceOrder.setOnClickListener {
            mView!!.btnPlaceOrder.startAnimation(AlphaAnimation(1f, .5f))
            myValidations()


           /* for(j in 0 until cardList.size){
                if(cardList[j].set_as_default==1){
                    card_id=cardList[j].id
                }
            }*/

          /*  Log.e("allowCoupans", ""+allowCoupans)*/


        }
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.cartFragment)
        }


        mView!!.rvCard.layoutManager= LinearLayoutManager(requireContext())
        cardsAdapter= PaymentCardsAdapter(requireContext(), cardList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type : String) {
                /*for(i in 0 until cardList.size){
                    if(i==pos){
                        cardList[i].set_as_default=1
                        card_id = cardList[i].id
                    }
                    else{
                        cardList[i].set_as_default=0
                    }
                }*/
                cardList[pos].set_as_default=1
                card_id = cardList[pos].id
            }

        })
        mView!!.rvCard.adapter=cardsAdapter

        mView!!.rvOrder.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        itemListAdapter= ItemListAdapter(requireContext(), cartList)
        mView!!.rvOrder.adapter=itemListAdapter
    }

    private fun underlineForChangeaddress() {
        val underline = SpannableString(requireContext().getString(R.string.change_address))
        underline.setSpan(UnderlineSpan(), 0, underline.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mView!!.txtChangeAddress.text=underline
    }


    private fun myValidations(){
        when {
            location_id==0 -> {
                LogUtils.shortToast(requireContext(), getString(R.string.please_select_def_delivery_location))
            }
            card_id==0 -> {
                LogUtils.shortToast(requireContext(), getString(R.string.please_select_card_through_which_payment_would_be_processed))
            }
            else -> {
                order_data= JSONArray()
                cart_ids= JSONArray()
                for(i in 0 until cartList.size){
                    val obj=JSONObject()
                    obj.put("product_id", cartList[i].product_id)
                    obj.put("product_item_id", cartList[i].product_item_id)
                    obj.put("quantity", cartList[i].quantity)
                    obj.put("price", cartList[i].price)
                    order_data.put(obj)

                    val obj2=JSONObject()
                    obj2.put("id", cartList[i].id)
                    cart_ids.put(obj2)
                }
                placeOrder()
            }
        }
    }

    private fun myCart() {
        mView!!.progressBar.visibility= View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "device_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""]
                    , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.myCart(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                       val responseBody=response.body()!!.string()
                        val jsonObject = JSONObject(responseBody)
                        if (jsonObject.getInt("response") == 1){

         /*                   subtotal=jsonObject.getString("sub_total")
                            shipping_fee=jsonObject.getString("shipping_fee")
                            taxes=jsonObject.getString("taxes")
                            total_price=jsonObject.getString("total_price")
                            total_discount=jsonObject.getString("total_discount")*/
                            allowCoupans = jsonObject.getInt("total_allow_coupons_count")


                            mySubTotal = jsonObject.getString("sub_total").toInt()
                            myTaxes = jsonObject.getString("taxes").toInt()
                            myTotalDisc = jsonObject.getString("total_discount").toInt()


                            if(allowCoupans>0){
                                mView!!.txtCouponCode.visibility = View.VISIBLE
                                mView!!.txtCoupon.visibility = View.VISIBLE
                                mView!!.txtTotalDiscount.visibility = View.VISIBLE
                                mView!!.totalDiscAmt.visibility = View.VISIBLE
                            }else{
                                mView!!.txtCouponCode.visibility = View.GONE
                                mView!!.txtCoupon.visibility = View.GONE
                                mView!!.txtTotalDiscount.visibility = View.GONE
                                mView!!.totalDiscAmt.visibility = View.GONE
                            }

                            if(total_discount!="0"){
                                mView!!.txtTotalDiscount.visibility=View.VISIBLE
                                mView!!.totalDiscAmt.visibility=View.VISIBLE
                            }
                            else{
                                mView!!.txtTotalDiscount.visibility=View.GONE
                                mView!!.totalDiscAmt.visibility=View.GONE
                            }



                            val carts=jsonObject.getJSONArray("carts")
                            cartList.clear()
                            amount=0f
                            for(i in 0  until carts.length()){
                                val obj = carts.getJSONObject(i)
                                val c = Cart()
                                c.id = obj.getInt("id")
                                c.user_id = obj.getInt("user_id")
                                supplier_id = obj.getInt("supplier_id")
                                c.add_offer = obj.getInt("add_offer")
                                c.allow_coupans = obj.getInt("allow_coupans")
                                c.quantity = obj.getInt("quantity")
                                c.sold_out = obj.getInt("sold_out")
                                c.like = obj.getBoolean("like")
                                c.product_id = obj.getInt("product_id")
                                c.price = obj.getString("price")
                                c.discount = obj.getString("discount")
                                c.product_item_id = obj.getString("product_item_id")
                                c.category_name = obj.getString("category_name")
                                c.product_name = obj.getString("product_name")
                                c.supplier_name = obj.getString("supplier_name")
                                c.files = obj.getString("files")
                                c.from_date = obj.getString("from_date")
                                c.to_date = obj.getString("to_date")
                                cartList.add(c)

                                if(obj.getInt("allow_coupans")==1){
                                    amount += obj.getString("price").toFloat()
                                    allowCoupans += 1
                                }
                            }
                            itemListAdapter.notifyDataSetChanged()

                            val cards=jsonObject.getJSONArray("cards")
                            cardList.clear()

                            mView!!.rvCard.visibility=View.VISIBLE
                            mView!!.txtNoCardFound.visibility=View.GONE

                            for(i in 0  until cards.length()){
                                val obj = cards.getJSONObject(i)
                                val c = Cards()
                                c.id = obj.getInt("id")
                                c.user_id = obj.getInt("user_id")
                                c.set_as_default = obj.getInt("set_as_default")
                                c.expiry = obj.getString("expiry")
                                c.cvv = obj.getString("cvv")
                                c.card_number = obj.getString("card_number")
                                cardList.add(c)
                            }
                            if(cardList.size==0){
                                mView!!.rvCard.visibility=View.GONE
                                mView!!.txtNoCardFound.visibility=View.VISIBLE
                            }
                            cardsAdapter.notifyDataSetChanged()

                            coupon_id = jsonObject.getInt("coupon_id")
                            val coupon_code = jsonObject.getString("code")

                            if(!TextUtils.isEmpty(coupon_code)){
                                mView!!.txtCoupon.text=coupon_code
                            }

                            val location=jsonObject.getJSONObject("location")
                            location_id=location.getInt("id")
                            mView!!.txtTitle.text=location.getString("title")
                            if (location.getString("address").equals("")){
                                val address = location.getString("street") + " " + location.getString("city") + " " + location.getString("country")
                                mView!!.txtAddress.text=address
                            }else{
                                mView!!.txtAddress.text=location.getString("address")
                            }

                            calculateShippingFees()
                        }
                        else {
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
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                /* if(mView.swipeRefresh.isRefreshing){
                     mView.swipeRefresh.isRefreshing=false
                 }*/
            }
        })

        Log.e("allowCoupans 1", ""+allowCoupans)

    }


    private fun calculateShippingFees() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()))

        val call = apiInterface.calcShippingFees(builder.build())
        call!!.enqueue(object : Callback<CalcShippingFees?> {
            override fun onResponse(call: Call<CalcShippingFees?>, response: Response<CalcShippingFees?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        myShippingFees = response.body()!!.response!!.shippingFee!!

                        mView!!.txtSubTotalPrice.text= "AED "+mySubTotal
                        mView!!.totalDiscAmt.text= "- AED "+myTotalDisc
                        mView!!.shippingFee.text= "AED "+myShippingFees
                        mView!!.taxes.text= "AED "+myTaxes
                        myTotalPrice = myShippingFees + myTaxes + mySubTotal - myTotalDisc
                        mView!!.totalPrice.text=  "AED "+myTotalPrice
                        Log.e("Shipping Fee", response.body()!!.response.toString())
                        Log.e("Shipping Fee", response.body()!!.response.toString())
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CalcShippingFees?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }


    private fun placeOrder() {
        Log.e("order_data", order_data.toString())
        mView!!.progressBar.visibility= View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "device_id", "order_data", "location_id", "coupon_id"
            , "subtotal", "shipping_fee", "taxes", "total_price", "total_discount", "cart_ids",  "card_id", "supplier_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.DeviceId, ""]
                ,order_data.toString(), location_id.toString(), coupon_id.toString(), mySubTotal.toString(), myShippingFees.toString(), myTaxes.toString(), myTotalPrice.toString(), myTotalDisc.toString(), cart_ids.toString(), card_id.toString()
                , supplier_id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.placeOrder(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val responseBody=response.body()!!.string()
                        val jsonObject = JSONObject(responseBody)
                        if (jsonObject.getInt("response") == 1){
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            var bundle = Bundle()
                            val data = jsonObject.getJSONObject("data")
                            val transaction = data.getJSONObject("transaction")
                            val url = transaction.getString("url")
                            Log.e("url", url)
                            bundle.putString("url", url)
                            findNavController().navigate(R.id.tapPaymentGatewayFragment, bundle)
                        }
                        else {
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
                mView!!.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                /* if(mView.swipeRefresh.isRefreshing){
                     mView.swipeRefresh.isRefreshing=false
                 }*/
            }
        })


    }



    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        requireActivity().frag_other_signup_tv.setText(R.string.checkout)
        requireActivity().frag_other_signup_tv.letterSpacing = 0.06F
        requireActivity().frag_other_signup_tv.isAllCaps = true
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.GONE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_signup_tv.text = "SEEN"
        requireActivity().frag_other_signup_tv.letterSpacing = 0.1F
        requireActivity().frag_other_signup_tv.isAllCaps = true
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_signup_tv.text = "SEEN"
        requireActivity().frag_other_signup_tv.letterSpacing = 0.1F
        requireActivity().frag_other_signup_tv.isAllCaps = true
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
}