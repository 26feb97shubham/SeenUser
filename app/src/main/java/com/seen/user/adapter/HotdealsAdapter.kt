package com.seen.user.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.seen.user.R
import com.seen.user.model.ProductList
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_offers_discounts.view.*
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.*
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.category
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.discountPer
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.discountedprice
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.img
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.imgLike
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.imgcatgory
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.price
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.productName
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.supplierImg
import kotlinx.android.synthetic.main.layout_hot_deals_items.view.supplierName
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class HotdealsAdapter(
    private val context: Context,
    private val productList: ArrayList<ProductList>,
    private val user_id: String,
    private val findNavController: NavController
):
    RecyclerView.Adapter<HotdealsAdapter.HotdealsAdapterVH>() {
    inner class HotdealsAdapterVH(val itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bind(product: ProductList, position: Int) {
            val requestOptions: RequestOptions =
                RequestOptions().error(R.drawable.def_product).centerCrop()

            val pro_id = product.id.toString()
            Log.e("product_list", product.toString())
            itemView.productName.text = product.name
            itemView.category.text = product.category_name
            itemView.price.text = "AED "+product.price
            itemView.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            itemView.discountedprice.text = "AED "+product.discount
            itemView.discountPer.text = product.discount
            itemView.supplierName.text = product.supplier_name
            itemView.txtRating.text = product.rating.toString()
            itemView.ratingBar.rating = product.rating.toFloat()
            val productImage = if (product.all_files.length()==0){
                context.getDrawable(R.drawable.def_product).toString()
            }else{
                product.all_files[0]
            }
            Glide.with(context).load(productImage).into(itemView.img)
            /*Glide.with(context).load(productImage)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemView.hotDealsProductProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemView.hotDealsProductProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .apply(requestOptions)
                .into(itemView.img)*/

            Log.e("pro_id", pro_id)


            if(product.like){
                itemView.imgLike.setImageResource(R.drawable.heart_red)
            }
            else{
                itemView.imgLike.setImageResource(R.drawable.gray_heart)
            }
            itemView.imgLike.setOnClickListener {
                Log.e("position_3", ""+position)
                Log.e("position_3_id", ""+product.id+ "   "+position)
                /*if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                    LogUtils.shortToast(context, context.getString(R.string.please_login_signup_to_access_this_functionality))
                    val args= Bundle()
                    args.putString("reference", "OffersDiscount")
                    findNavController.navigate(R.id.chooseLoginSingUpFragment, args)
                    return@setOnClickListener
                }else{

                }*/

                Log.e("product_id", product.id.toString())
                Log.e("pos", position.toString())
                Log.e("user_id", user_id)

                val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

                val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "lang"),
                    arrayOf(user_id, product.id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

                val call = apiInterface.likeUnlikeProduct(builder.build())
                call!!.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        try {
                            if (response.body() != null) {
                                val jsonObject = JSONObject(response.body()!!.string())

                                if(jsonObject.getInt("response")==1){
                                    product.like = !product.like
                                   LogUtils.shortToast(context, jsonObject.getString("message"))
                                    notifyDataSetChanged()

                                }
                                else{
                                    LogUtils.shortToast(context, jsonObject.getString("message"))
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
                        LogUtils.shortToast(context, context.getString(R.string.check_internet))
                    }
                })
            }

         /*   itemView.imgcatgory.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("product_id", product.id)
                findNavController.navigate(R.id.productDetailsFragment, bundle)
            }*/

            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("product_id", product.id)
                findNavController.navigate(R.id.productDetailsFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotdealsAdapterVH {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_hot_deals_items, parent, false)
        return HotdealsAdapterVH(view)
    }

    override fun onBindViewHolder(holder: HotdealsAdapterVH, position: Int) {
        val product = productList[position]
        holder.bind(product, position)
    }

    override fun getItemCount(): Int {
       return productList.size
    }
}