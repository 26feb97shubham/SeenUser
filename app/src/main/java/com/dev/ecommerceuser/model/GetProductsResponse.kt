package com.dev.ecommerceuser.model

data class GetProductsResponse(
	val response: Int? = null,
	val message: String? = null,
	val products: List<ProductsItemX?>? = null
)



