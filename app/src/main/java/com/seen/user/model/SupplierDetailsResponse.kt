package com.seen.user.model

data class SupplierDetailsResponse(
	val response: Int? = null,
	val profile: Profile? = null,
	val categories: List<CategoriesItem?>? = null,
	val message: String? = null,
	val products: List<ProductsItem?>? = null
)



