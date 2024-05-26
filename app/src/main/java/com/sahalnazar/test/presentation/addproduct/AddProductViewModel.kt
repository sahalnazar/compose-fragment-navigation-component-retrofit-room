package com.sahalnazar.test.presentation.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahalnazar.test.data.model.AddProductResponse
import com.sahalnazar.test.data.repository.AppRepository
import com.sahalnazar.test.util.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _addProductApiState = MutableStateFlow<AddProductApiState>(AddProductApiState.Idle)
    val addProductApiState = _addProductApiState.asStateFlow()

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState = _uiState.asStateFlow()

    fun setProductName(name: String) {
        _uiState.update { it.copy(productName = name) }
    }

    fun setProductType(type: String) {
        _uiState.update { it.copy(productType = type) }
    }

    fun setPrice(price: String) {
        _uiState.update { it.copy(price = price) }
    }

    fun setTax(tax: String) {
        _uiState.update { it.copy(tax = tax) }
    }

    fun setFiles(files: List<MultipartBody.Part>?) {
        _uiState.update { it.copy(files = files) }
    }

    fun hasAllRequiredData(): Boolean {
        return (uiState.value.productType.isNotEmpty()
                && uiState.value.productName.isNotEmpty()
                && uiState.value.tax.isNotEmpty()
                && uiState.value.price.isNotEmpty())
    }

    fun isPriceAndTaxNumbers(): Boolean {
        return uiState.value.tax.toDoubleOrNull() != null
                && uiState.value.price.toDoubleOrNull() != null
    }

    fun addProduct() {
        _addProductApiState.value = AddProductApiState.Loading
        viewModelScope.launch {
            val result = repository.addProduct(
                _uiState.value.productName,
                _uiState.value.productType,
                _uiState.value.price,
                _uiState.value.tax,
                _uiState.value.files
            )
            _addProductApiState.value = when (result) {
                is ResultWrapper.Success -> {
                    result.data?.let {
                        AddProductApiState.Success(it)
                    } ?: AddProductApiState.Error("Empty response")
                }

                is ResultWrapper.Failure -> {
                    AddProductApiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _addProductApiState.value = AddProductApiState.Idle
    }

}

sealed class AddProductApiState {
    data object Idle : AddProductApiState()
    data object Loading : AddProductApiState()
    data class Success(val response: AddProductResponse) : AddProductApiState()
    data class Error(val message: String) : AddProductApiState()
}

data class AddProductUiState(
    val productName: String = "",
    val productType: String = "",
    val price: String = "",
    val tax: String = "",
    val files: List<MultipartBody.Part>? = null
)
