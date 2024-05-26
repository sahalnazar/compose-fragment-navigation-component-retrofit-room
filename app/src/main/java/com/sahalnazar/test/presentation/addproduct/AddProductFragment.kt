package com.sahalnazar.test.presentation.addproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sahalnazar.test.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class AddProductFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<AddProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddProductScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addProductApiState.collect {
                    when (it) {
                        is AddProductApiState.Success -> {
                            Toast.makeText(
                                requireContext(),
                                ("success: " + it.response.success
                                        + "message:" + it.response.message),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetState()
                            dismiss()
                        }

                        is AddProductApiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.resetState()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun AddProductScreen(
    viewModel: AddProductViewModel
) {
    val apiState = viewModel.addProductApiState.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            val parts = uris.map { uri ->
                val file = uriToFile(context, uri)
                MultipartBody.Part.createFormData(
                    "jpeg",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
            }
            viewModel.setFiles(parts)
        }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        val padding = it
        Box(modifier = Modifier.fillMaxSize()) {
            if (apiState.value is AddProductApiState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = uiState.value.productName,
                    onValueChange = viewModel::setProductName,
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.value.productType,
                    onValueChange = viewModel::setProductType,
                    label = { Text("Product Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.value.price,
                    onValueChange = viewModel::setPrice,
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.value.tax,
                    onValueChange = viewModel::setTax,
                    label = { Text("Tax") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = {
                        launcher.launch("image/*")
                    }) {
                        Text("Attach image")
                    }
                    Text(text = "${uiState.value.files?.size ?: 0} number of images attached")
                }
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        if (viewModel.hasAllRequiredData() && viewModel.isPriceAndTaxNumbers()) {
                            viewModel.addProduct()
                        } else if (!viewModel.isPriceAndTaxNumbers()) {
                            Toast.makeText(
                                context,
                                "Price and tax needs to be number",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all the information",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                    Text("Add Product")
                }
            }
        }

    }
}
