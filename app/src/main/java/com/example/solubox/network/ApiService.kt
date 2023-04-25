package com.example.solubox.network

import com.example.solubox.entities.AccessToken
import com.example.solubox.model.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("invit_promo") invit_promo: String?): Call<AccessToken?>?


    @POST("fillProfile")
    @FormUrlEncoded
    fun fillForm(
        @Field("name") name: String?,
        @Field("lastname") lastname: String?,
        @Field("phonenumber") phonenumber: String?,
        @Field("address") address: String?,
        @Field("post_code") post_code: String?,
        @Field("city") city: String?
        ): Call<AccessToken?>?

    @POST("login")
    @FormUrlEncoded
    fun login(@Field("username") username: String?, @Field("password") password: String?): Call<AccessToken?>?


    @PUT("update")
    @FormUrlEncoded
    fun update(
        @Field("name") name: String?,
        @Field("lastname") lastname: String?,
        @Field("email") email: String?,
        @Field("phonenumber") phonenumber: String?,
        @Field("address") address: String?,
        @Field("post_code") post_code: String?,
        @Field("city") city: String?,
    ): Call<AccessToken?>?


    @POST("social_auth")
    @FormUrlEncoded
    fun socialAuth(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("provider") provider: String?,
        @Field("provider_user_id") providerUserId: String?,
    ): Call<AccessToken?>?

    @POST("refresh")
    @FormUrlEncoded
    fun refresh(@Field("refresh_token") refreshToken: String?): Call<AccessToken?>?


    @GET("profilDetails")
    fun getUser(): Call<UserResponse?>?



    @GET("shopDetails")
    fun getShop(): Call<List<ShopModel>>

    @GET("productDetails")
    fun getProduct(@Query("id", encoded=false) id: String): Call<List<Menu>>


    @GET("promoInv")
    fun getInvCode(@Query("id", encoded=false) id: String): Call<List<User>?>?


    @GET("promo")
    fun getPromo(@Query("code", encoded=false) code: String): Call<List<User>?>?

    @POST("createPromoInv")
    @FormUrlEncoded
    fun storeCode(@Field("owner_id") user_id: String,
                  @Field("code") code: String,
                  @Field("code_type") code_type: String,
                  @Field("usage_limit") usage_limit: Int,
                  @Field("active") active: Int,
                  @Field("amount") amount: Int): Call<List<InviteCode>?>?

    @GET("displayCode")
    fun displayCode(@Query("user_id", encoded=false) user_id: String): Call<List<InviteCode>?>?


    @POST("orderDetails")
    @FormUrlEncoded
    fun orderDetails(
        @Field("user_id") user_id: String?,
        @Field("product_name") product_name: String?,
        @Field("quantity") quantity: String?,
        @Field("amount") amount: String?,
        @Field("solu_service_fee") solu_service_fee: String?,
        @Field("total_amount") total_amount: String?,
    ): Call<AccessToken?>?


    @POST("logout")
    @FormUrlEncoded
    fun logout(@Field("access_token") accessToken: String?): Call<AccessToken?>?

}