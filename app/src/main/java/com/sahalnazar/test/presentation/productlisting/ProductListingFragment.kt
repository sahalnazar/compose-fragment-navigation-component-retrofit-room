package com.sahalnazar.test.presentation.productlisting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.sahalnazar.test.data.model.ProductListResponseItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListingFragment : Fragment() {

    val viewModel by viewModels<ProductListingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProductListScreen(viewModel)
            }
        }

    }
}

@Composable
fun ProductListScreen(viewModel: ProductListingViewModel) {
    val productListApiState by viewModel.productListApiState.collectAsState()

    when (productListApiState) {
        is ProductListApiState.Idle -> {
            // Show initial state or message
        }

        is ProductListApiState.Loading -> {
            // Show loading indicator
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ProductListApiState.Success -> {
            // Show product list
            val products = viewModel.filteredProducts.collectAsState()
            val searchTextValue = viewModel.searchText.collectAsState()
            ProductList(
                products = products.value,
                searchText = searchTextValue.value,
                onSearchValueChange = viewModel::setSearchText
            )
        }

        is ProductListApiState.Error -> {
            // Show error message
            val message = (productListApiState as ProductListApiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: $message")
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<ProductListResponseItem>,
    searchText: String,
    onSearchValueChange: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("Search") }
            )
        }

        items(products) { product ->
            ProductListItem(product)
        }
    }
}

@Composable
fun ProductListItem(product: ProductListResponseItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column {
            AsyncImage(
                model = product.image.takeIf { !it.isNullOrBlank() } ?: "https://picsum.photos/200",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = product.productName.orEmpty(),
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Type: ${product.productType}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ${product.price}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tax: ${product.tax}%",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
