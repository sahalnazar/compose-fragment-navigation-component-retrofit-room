package com.sahalnazar.test.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddProductResponse(
    @SerialName("message")
    val message: String? = null,
    @SerialName("product_details")
    val productDetails: ProductDetails? = null,
    @SerialName("product_id")
    val productId: Int? = null,
    @SerialName("success")
    val success: Boolean? = null
) {
    @Serializable
    class ProductDetails
}