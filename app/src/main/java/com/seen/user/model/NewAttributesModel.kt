package com.seen.user.model

import com.google.gson.annotations.SerializedName

data class NewAttributesModel (
    @SerializedName("data"     ) var data     : ArrayList<DataY> = arrayListOf(),
    @SerializedName("id"       ) var id       : String?         = null,
    @SerializedName("quantity" ) var quantity : Int?            = null,
    @SerializedName("price"    ) var price    : String?         = null,
    @SerializedName("sold_out" ) var soldOut  : Int?            = null
)

data class DataY (

    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("name_ar"  ) var nameAr  : String? = null,
    @SerializedName("value" ) var value : String? = null,
    @SerializedName("id"    ) var id    : String? = null,
    @SerializedName("type"  ) var type  : String? = null

)