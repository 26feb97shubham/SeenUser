package com.seen.user.model

data class GlobalSrchResponse(
	val data: List<DataItem?>? = null,
	val products: List<ProductsItemXX?>? = null
)

data class DataItem(
	val data: List<DataItem?>? = null,
	val name: String? = null,
	val id: Int? = null
)

data class ProductsItemXX(
	val name: String? = null,
	val id: Int? = null
)

