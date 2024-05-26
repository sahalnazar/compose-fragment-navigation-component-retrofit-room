package com.sahalnazar.test.data.repository

import com.sahalnazar.test.data.db.AppDatabase
import com.sahalnazar.test.data.model.ProductListResponseItem
import com.sahalnazar.test.data.remote.AppService
import com.sahalnazar.test.util.ResultWrapper
import com.sahalnazar.test.util.safeApiCall
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appService: AppService,
    private val db: AppDatabase
) {

    suspend fun getProducts(): ResultWrapper<List<ProductListResponseItem>> {
        val cachedProducts = db.productDao().getAllProducts().firstOrNull()
        if (!cachedProducts.isNullOrEmpty()) {
            return ResultWrapper.Success(cachedProducts, 200)
        }
        return safeApiCall {
            appService.getProducts()
        }.also { result ->
            if (result is ResultWrapper.Success) {
                result.data?.let { products ->
                    db.productDao().insertAll(products)
                }
            }
        }
    }

    suspend fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        files: List<MultipartBody.Part>?
    ) = safeApiCall {
        appService.addProduct(
            productName = productName,
            productType = productType,
            price = price,
            tax = tax,
            files = files
        )
    }
}