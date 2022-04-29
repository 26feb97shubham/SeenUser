package com.seen.user.model

import com.google.gson.annotations.SerializedName

data class Products (

    @SerializedName("id"                       ) var id                     : Int?              = null,
    @SerializedName("user_id"                  ) var userId                 : Int?              = null,
    @SerializedName("name"                     ) var name                   : String?           = null,
    @SerializedName("category_id"              ) var categoryId             : Int?              = null,
    @SerializedName("attributes"               ) var attributes             : String?           = null,
    @SerializedName("description"              ) var description            : String?           = null,
    @SerializedName("allow_coupans"            ) var allowCoupans           : Int?              = null,
    @SerializedName("add_offer"                ) var addOffer               : Int?              = null,
    @SerializedName("discount"                 ) var discount               : Int?              = null,
    @SerializedName("from_date"                ) var fromDate               : String?           = null,
    @SerializedName("to_date"                  ) var toDate                 : String?           = null,
    @SerializedName("files"                    ) var files                  : String?           = null,
    @SerializedName("delete_status"            ) var deleteStatus           : Int?              = null,
    @SerializedName("status"                   ) var status                 : Int?              = null,
    @SerializedName("hot_deal_status"          ) var hotDealStatus          : Int?              = null,
    @SerializedName("hot_deal_from_date"       ) var hotDealFromDate        : String?           = null,
    @SerializedName("hot_deal_to_date"         ) var hotDealToDate          : String?           = null,
    @SerializedName("modified"                 ) var modified               : String?           = null,
    @SerializedName("created"                  ) var created                : String?           = null,
    @SerializedName("supplier_id"              ) var supplierId             : Int?              = null,
    @SerializedName("supplier_name"            ) var supplierName           : String?           = null,
    @SerializedName("supplier_profile_picture" ) var supplierProfilePicture : String?           = null,
    @SerializedName("account_type"             ) var accountType            : Int?              = null,
    @SerializedName("country_id"               ) var countryId              : Int?              = null,
    @SerializedName("countries_to_be_served"   ) var countriesToBeServed    : String?           = null,
    @SerializedName("rating"                   ) var rating                 : Int?              = null,
    @SerializedName("all_files"                ) var allFiles               : ArrayList<String> = arrayListOf(),
    @SerializedName("quantity"                 ) var quantity               : Int?              = null,
    @SerializedName("category_name"            ) var categoryName           : String?           = null,
    @SerializedName("price"                    ) var price                  : String?              = null,
    @SerializedName("actual_price"             ) var actualPrice            : String?              = null,
    @SerializedName("like"                     ) var like                   : Boolean?          = null

)