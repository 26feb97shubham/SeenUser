package com.seen.user.model

data class ProductsItem(
        val all_files: List<String?>? = null,
        val quantity: Int? = null,
        val category_name: String? = null,
        val from_date: String? = null,
        val add_offer: Int? = null,
        var like: Boolean? = null,
        val created: String? = null,
        val allow_coupans: Int? = null,
        val rating: Int? = null,
        val description: String? = null,
        val discount: Int? = null,
        val category_id: Int? = null,
        val to_date: String? = null,
        val user_id: Int? = null,
        val delete_status: Int? = null,
        val price: String? = null,
        val name: String? = null,
        val files: String? = null,
        val modified: String? = null,
        val attributes: String? = null,
        val id: Int? = null,
        val supplier_name: String? = null,
        val status: Int? = null
)
