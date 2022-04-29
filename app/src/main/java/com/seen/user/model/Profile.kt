package com.seen.user.model

data class Profile(
    val account_type: Int? = null,
    val like: Boolean? = null,
    val account_name: String? = null,
    val account_name_ar: String? = null,
    val name: String? = null,
    val rating: Double? = null,
    val bio: String? = null,
    val profile_picture: String? = null,
    val country_served_name: String? = null,
    val country_served_name_ar: String? = null,
    val email: String? = null
)