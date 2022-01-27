package com.seen.user.model

data class GlobalSearchResponse(
	val data: Data? = null,
	val products: ArrayList<ProductItem?>? = null
)

data class Data(
	val brands: ArrayList<BrandsItem?>? = null,
	val homemadeSuppliers: ArrayList<HomeMadeSuppliersItem?>? = null,
	val healthAndBeauty: ArrayList<HealthAndBeautyItem?>? = null,
	val bloggers: ArrayList<BloggersItem?>? = null
)

data class ProductItem(
	val name: String? = null,
	val id: Int? = null
)

data class BrandsItem(
	val name: String? = null,
	val id: Int? = null
)
data class HomeMadeSuppliersItem(
	val name: String? = null,
	val id: Int? = null
)

data class HealthAndBeautyItem(
	val name: String? = null,
	val id: Int? = null
)

data class BloggersItem(
	val name: String? = null,
	val id: Int? = null
)

