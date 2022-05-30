package com.seen.user.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalcShippingFees(
    @SerializedName("response" ) var response : MyResponse? = MyResponse(),
    @SerializedName("message"  ) var message  : String?   = null
): Serializable

data class MyResponse (

    @SerializedName("shipping_fee" ) var shippingFee : Int? = null

)