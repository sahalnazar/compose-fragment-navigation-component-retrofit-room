package com.sahalnazar.test.presentation.productlisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahalnazar.test.data.model.ProductListResponseItem
import com.sahalnazar.test.data.repository.AppRepository
import com.sahalnazar.test.util.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListingViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _productListApiState = MutableStateFlow<ProductListApiState>(ProductListApiState.Idle)
    val productListApiState = _productListApiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<ProductListResponseItem>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _productListApiState.value = ProductListApiState.Loading
        viewModelScope.launch {
            val result = repository.getProducts()
            _productListApiState.value = when (result) {
                is ResultWrapper.Success -> {
                    result.data?.let {
                        _filteredProducts.value = it
                        ProductListApiState.Success(it)
                    } ?: ProductListApiState.Error("Empty response")
                }
                is ResultWrapper.Failure -> {
                    ProductListApiState.Error(result.message)
                }
            }
        }
    }

    fun setSearchText(query: String) {
        _searchText.value = query
        filterProducts()
    }

    private fun filterProducts() {
        val query = _searchText.value
        val products = (productListApiState.value as? ProductListApiState.Success)?.products ?: emptyList()
        _filteredProducts.value = if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.productName?.contains(query, ignoreCase = true) == true ||
                        it.productType?.contains(query, ignoreCase = true) == true ||
                        it.price.toString().contains(query, ignoreCase = true) ||
                        it.tax.toString().contains(query, ignoreCase = true)
            }
        }
    }

}

sealed class ProductListApiState {
    data object Idle : ProductListApiState()
    data object Loading : ProductListApiState()
    data class Success(val products: List<ProductListResponseItem>) : ProductListApiState()
    data class Error(val message: String) : ProductListApiState()
}
