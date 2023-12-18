package com.example.mediastoreapp.ui.edit

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediastoreapp.InventoryTopAppBar
import com.example.mediastoreapp.R
import com.example.mediastoreapp.ui.AppViewModelProvider
import com.example.mediastoreapp.ui.navigation.NavigationDestination

object EditDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.edit_tags_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(EditDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) { innerPadding ->
        EditBody(
            editUiState = viewModel.itemUiState,
            onSettingValueChange = viewModel::updateUiState,
            onSaveClick = viewModel::save,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            navigateBack = navigateBack
        )
    }
}

@Composable
fun EditBody(
    editUiState: ItemUiState,
    onSettingValueChange: (ItemUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit
) {
    val localContext = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        InputsForm(
            tagsDetails = editUiState,
            onValueChange = onSettingValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        BackHandler(onBack = {
            navigateBack()
        })
        Button(
            onClick = {
                onSaveClick()
                navigateBack()
                Toast.makeText(localContext, "Tags were changed!", Toast.LENGTH_SHORT).show()
            },
            enabled = true,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save tags")
        }
    }
}

@Composable
fun InputsForm(tagsDetails: ItemUiState,
                 modifier: Modifier = Modifier,
                 onValueChange: (ItemUiState) -> Unit = {}
){

    Column(
        modifier = modifier
    ) {
        TextField(
            value = tagsDetails.deviceType,
            onValueChange = {
                onValueChange(tagsDetails.copy(deviceType = it))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}