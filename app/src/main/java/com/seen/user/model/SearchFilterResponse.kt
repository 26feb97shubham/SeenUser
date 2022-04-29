package com.seen.user.model

import com.google.gson.annotations.SerializedName

data class SearchFilterResponse(
	@SerializedName("products_count" ) var productsCount : Int?                = null,
	@SerializedName("products"       ) var products      : ArrayList<Products> = arrayListOf()
)

