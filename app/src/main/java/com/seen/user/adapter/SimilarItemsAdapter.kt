package com.seen.user.adapter

import android.content.Context
import android.content.Intent
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
import com.seen.user.activity.LoginActivity
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.ProductsItemX
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.item_recent_products.view.*
import kotlinx.android.synthetic.main.layout_similar_items.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class SimilarItemsAdapter(
    private val context: Context,
    private val similarItemsList: ArrayList<ProductsItemX>,
    private val findNavController: NavController,
    private val clickPosInterface: ClickInterface.ClickPosInterface
) : RecyclerView.Adapter<SimilarItemsAdapter.SimilarItemsAdapterVH>() {
    inner class SimilarItemsAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarItemsAdapterVH {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_similar_items, parent, false)
        return SimilarItemsAdapterVH(view)
    }

    override fun onBindViewHolder(holder: SimilarItemsAdapterVH, position: Int) {
        val requestOptions: RequestOptions =
            RequestOptions().error(R.drawable.def_product).centerCrop()

        val similarProductsImage = if (similarItemsList[position].files!!.isEmpty()){
            context.getDrawable(R.drawable.def_product).toString()
        }else{
            similarItemsList[position].files
        }

        val supplierImage = if (similarItemsList[position].files!!.isEmpty()){
            context.getDrawable(R.drawable.def_product).toString()
        }else{
            similarItemsList[position].files
        }


        Glide.with(context).load(supplierImage)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.similarItemListProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.similarItemListProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.civ_supplier_image)

        Glide.with(context).load(similarProductsImage)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.similarItemListProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.similarItemListProgressBar.visibility = View.GONE
                    return false
                }

            })
            .apply(requestOptions).into(holder.itemView.product_image)
        holder.itemView.item_name.text = similarItemsList[position].name
        holder.itemView.item_price.text = "AED ${similarItemsList[position].price}"

        if(similarItemsList[position].like == true){
            holder.itemView.add_to_fav_iv.setImageResource(R.drawable.heart_red)
        }
        else{
            holder.itemView.add_to_fav_iv.setImageResource(R.drawable.heart_white)
        }
        holder.itemView.add_to_fav_iv.setOnClickListener {
            Log.e("position_3", ""+position)
            val products = similarItemsList[position]
            Log.e("position_3_id", ""+similarItemsList[position].id+ "   "+position)
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(context, context.getString(R.string.please_login_signup_to_access_this_functionality))
                val args= Bundle()
                args.putString("reference", "OffersDiscount")
//                findNavController.navigate(R.id.chooseLoginSingUpFragment, args)
                context.startActivity(Intent(context, LoginActivity::class.java).putExtras(args))
                return@setOnClickListener
            }

            Log.e("product_id", similarItemsList[position].id.toString())
            Log.e("pos", position.toString())

            val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

            val builder = ApiClient.createBuilder(arrayOf("user_id", "product_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), products.id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

            val call = apiInterface.likeUnlikeProduct(builder.build())
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    try {
                        if (response.body() != null) {
                            val jsonObject = JSONObject(response.body()!!.string())

                            if(jsonObject.getInt("response")==1){
                                products.like = !products.like!!
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

        holder.itemView.add_to_cart_iv.setOnClickListener {
            if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                LogUtils.shortToast(context, context.getString(R.string.please_login_signup_to_access_this_functionality))
                val args= Bundle()
                args.putString("reference", "similar items ")
//                navController.navigate(R.id.chooseLoginSingUpFragment, args)
                context.startActivity(Intent(context, LoginActivity::class.java).putExtras(args))
                return@setOnClickListener
            }else{
            }
          // clickPosInterface.clickPostion(position, "Cart")
        }

        holder.itemView.civ_supplier_image.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0]==0){
                    LogUtils.shortToast(context, context.getString(R.string.please_login_signup_to_access_this_functionality))
                    val args= Bundle()
                    args.putString("reference", "similar items")
                    context.startActivity(Intent(context, LoginActivity::class.java).putExtras(args))
                    return
                }else{
                    clickPosInterface.clickPostion(position, "Supplier")
                }
            }

        })

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            similarItemsList[position].id?.let { it1 -> bundle.putInt("product_id", it1) }
            findNavController.navigate(R.id.productDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return similarItemsList.size
    }
}