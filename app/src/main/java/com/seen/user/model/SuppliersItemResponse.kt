package com.seen.user.model

data class SuppliersItemResponse(
	val suppliers: List<SuppliersItem?>? = null,
	val response: Int? = null,
	val message: String? = null
)

data class SuppliersItem(
	val name: String? = null,
	val id: Int? = null
)

