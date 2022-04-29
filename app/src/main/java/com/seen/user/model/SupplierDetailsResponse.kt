package com.seen.user.model

import com.google.gson.annotations.SerializedName

data class SupplierDetailsResponse(
/*	val response: Int? = null,
	val profile: Profile? = null,
	val categories: List<CategoriesItem?>? = null,
	val message: String? = null,
	val products: List<ProductsItem?>? = null,*/

	@SerializedName("response"   ) var response   : Int?                  = null,
	@SerializedName("message"    ) var message    : String?               = null,
	@SerializedName("categories" ) var categories : ArrayList<CategoriesItemX> = arrayListOf(),
	@SerializedName("products"   ) var products   : ArrayList<ProductsItemZ>   = arrayListOf(),
	@SerializedName("profile"    ) var profile    : ProfileItemX?              = ProfileItemX()
)

data class CategoriesItemX (

	@SerializedName("id"         ) var id         : Int?    = null,
	@SerializedName("name"       ) var name       : String? = null,
	@SerializedName("name_ar"    ) var nameAr     : String? = null,
	@SerializedName("attributes" ) var attributes : String? = null,
	@SerializedName("image"      ) var image      : String? = null,
	@SerializedName("status"     ) var status     : Int?    = null,
	@SerializedName("created"    ) var created    : String? = null,
	@SerializedName("modified"   ) var modified   : String? = null

)

data class ProductsItemZ (

	@SerializedName("id"                 ) var productId              : Int?              = null,
	@SerializedName("user_id"            ) var supplierUserId          : Int?              = null,
	@SerializedName("name"               ) var name            : String?           = null,
	@SerializedName("category_id"        ) var categoryId      : Int?              = null,
	@SerializedName("attributes"         ) var attributes      : String?           = null,
	@SerializedName("description"        ) var description     : String?           = null,
	@SerializedName("allow_coupans"      ) var allowCoupans    : Int?              = null,
	@SerializedName("add_offer"          ) var addOffer        : Int?              = null,
	@SerializedName("discount"           ) var discount        : Int?              = null,
	@SerializedName("from_date"          ) var fromDate        : String?           = null,
	@SerializedName("to_date"            ) var toDate          : String?           = null,
	@SerializedName("files"              ) var files           : String?           = null,
	@SerializedName("delete_status"      ) var deleteStatus    : Int?              = null,
	@SerializedName("status"             ) var status          : Int?              = null,
	@SerializedName("hot_deal_status"    ) var hotDealStatus   : Int?              = null,
	@SerializedName("hot_deal_from_date" ) var hotDealFromDate : String?           = null,
	@SerializedName("hot_deal_to_date"   ) var hotDealToDate   : String?           = null,
	@SerializedName("modified"           ) var modified        : String?           = null,
	@SerializedName("created"            ) var created         : String?           = null,
	@SerializedName("all_files"          ) var allFiles        : ArrayList<String> = arrayListOf(),
	@SerializedName("quantity"           ) var quantity        : Int?              = null,
	@SerializedName("category_name"      ) var categoryName    : String?           = null,
	@SerializedName("category_name_ar"   ) var categoryNameAr  : String?           = null,
	@SerializedName("price"              ) var price           : String?           = null,
	@SerializedName("like"               ) var like            : Boolean?          = null

)

data class ProfileItemX (

	@SerializedName("name"                   ) var name                : String?  = null,
	@SerializedName("email"                  ) var email               : String?  = null,
	@SerializedName("profile_picture"        ) var profilePicture      : String?  = null,
	@SerializedName("like"                   ) var like                : Boolean? = null,
	@SerializedName("rating"                 ) var rating              : Int?     = null,
	@SerializedName("bio"                    ) var bio                 : String?  = null,
	@SerializedName("account_type"           ) var accountType         : Int?     = null,
	@SerializedName("account_name"           ) var accountName         : String?  = null,
	@SerializedName("account_name_ar"        ) var accountNameAr       : String?  = null,
	@SerializedName("country_served_name"    ) var countryServedName   : String?  = null,
	@SerializedName("country_served_name_ar" ) var countryServedNameAr : String?  = null

)


