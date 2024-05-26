package com.sahalnazar.test.data.remote

import com.sahalnazar.test.data.model.AddProductResponse
import com.sahalnazar.test.data.model.ProductListResponseItem
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AppService {
    @GET("get")
    suspend fun getProducts(): Response<List<ProductListResponseItem>>

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part("product_name") productName: String,
        @Part("product_type") productType: String,
        @Part("price") price: String,
        @Part("tax") tax: String,
        @Part files: List<MultipartBody.Part>?
    ): Response<AddProductResponse>

}