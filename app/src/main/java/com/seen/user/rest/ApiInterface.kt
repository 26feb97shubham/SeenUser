package com.seen.user.rest

import com.seen.user.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST(ApiUtils.LOGIN)
    fun login(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.Register)
    fun signUp(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ForgotPassword)
    fun forgotPassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ChangePassword)
    fun changePassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.VerifyAccount)
    fun verifyAccount(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ResendOtp)
    fun resendOtp(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ResetPassword)
    fun resetPassword(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetProfile)
    fun getProfile(@Body body: RequestBody?): Call<ResponseBody?>?

    @GET(ApiUtils.GetCountries)
    fun getCountries(): Call<ResponseBody?>?

    @GET(ApiUtils.GetCountries)
    fun getCountriesList(): Call<CountryListResponse?>?

    @POST(ApiUtils.GetHomes)
    fun getHomes(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.EditProfile)
    fun editProfile(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetNamesAndCategories)
    fun getNamesAndCategories(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetCategories)
    fun getCategories(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetNotifications)
    fun getNotifications(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.NotificationDelete)
    fun notificationDelete(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ContactUs)
    fun contactUs(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.LikeUnlike)
    fun likeUnlike(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.LikeUnlikeProduct)
    fun likeUnlikeProduct(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetGlobalMarket)
    fun getGlobalMarket(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetGlobalMarketSuppliers)
    fun getGlobalMarketSuppliers(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetOffersAndDiscounts)
    fun getOffersAndDiscounts(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.SupplierDetails)
    fun supplierDetails(@Body body: RequestBody?): Call<SupplierDetailsResponse?>?

    @POST(ApiUtils.GetGallery)
    fun getGallery(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetFavouritesSuppliersAndProducts)
    fun getFavouritesSuppliersAndProducts(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.AddLocations)
    fun addLocations(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.MyLocations)
    fun myLocations(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.DeleteLocation)
    fun deleteLocation(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ChnageNotificationStatus)
    fun chnageNotificationStatus(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ProductDetailPage)
    fun productDetailPage(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetProducts)
    fun getProducts(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetProducts)
    fun getProductsCategories(@Body body: RequestBody?): Call<GetProductsResponse?>?

    @POST(ApiUtils.CheckProductAvailable)
    fun checkProductAvailable(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.CheckProductPrice)
    fun checkProductPrice(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.AddCards)
    fun addCards(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.MyCards)
    fun myCards(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.DeleteCard)
    fun deleteCard(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.CartAdd)
    fun cartAdd(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.MyCart)
    fun myCart(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.PlaceOrder)
    fun placeOrder(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.CalcShippingFee)
    fun calcShippingFees(@Body body: RequestBody?): Call<CalcShippingFees?>?

    @POST(ApiUtils.MyOrder)
    fun myOrder(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetCoupons)
    fun getCoupons(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.ApplyCoupons)
    fun applyCoupons(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetTermsConditions)
    fun getTermsConditions(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetPrivacyPolicy)
    fun getPrivacyPolicy(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetAboutUs)
    fun getAboutUs(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.GetFaq)
    fun getFaq(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.MakeDefaultLocation)
    fun makeDefaultLocation(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST(ApiUtils.SEARCHFILTER)
    fun searchFilter(@Body body: RequestBody?) : Call<SearchFilterResponse?>?

    @POST(ApiUtils.ACCOUNTTYPE)
    fun getAccType() : Call<AccountTypeResponse?>?

    @POST(ApiUtils.GLOBALSEARCH)
    fun globalSearch(@Body body: RequestBody?) : Call<GlobalSrchResponse?>?

    @POST(ApiUtils.SUPPLIERSEARCH)
    fun getSuppliers(@Body body: RequestBody?) : Call<SuppliersItemResponse?>?

    @POST(ApiUtils.POSTRATING)
    fun postRating(@Body body: RequestBody?) : Call<PostRatingResponse?>?

    @POST(ApiUtils.HOTDEALS)
    fun getHotDeals(@Body body: RequestBody?) : Call<ResponseBody?>?

    @GET(ApiUtils.getTrackDetails)
    fun getTrackDetails(@Query("track_id") track_id : String) : Call<TrackFinalResultResponse?>?
}