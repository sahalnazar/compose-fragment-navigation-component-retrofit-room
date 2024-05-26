package com.sahalnazar.test.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class ProductListResponseItem(
    @PrimaryKey(autoGenerate = true)
    @SerialName("productId")
    val productId: Int? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("product_name")
    val productName: String? = null,
    @SerialName("product_type")
    val productType: String? = null,
    @SerialName("tax")
    val tax: Double? = null
)