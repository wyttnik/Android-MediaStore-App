package com.example.mediastoreapp.ui.chooser

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediastoreapp.InventoryTopAppBar
import com.example.mediastoreapp.R
import com.example.mediastoreapp.data.ImageItem
import com.example.mediastoreapp.ui.AppViewModelProvider
import com.example.mediastoreapp.ui.General.currentImageUri
import com.example.mediastoreapp.ui.home.HomeDestination
import com.example.mediastoreapp.ui.navigation.NavigationDestination

object ChooserDestination : NavigationDestination {
    override val route = "chooser"
    override val titleRes = R.string.chooser_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageChooserScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChooserViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val itemsUiState by viewModel.imagesUiState.collectAsState()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        HomeBody(
            itemList = itemsUiState.imageList,
            onItemClick = navigateBack,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            getImages = viewModel::getImages
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<ImageItem>, onItemClick: () -> Unit, modifier: Modifier = Modifier,
    getImages: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            InventoryList(
                itemList = itemList,
                onItemClick = onItemClick,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun InventoryList(
    itemList: List<ImageItem>, onItemClick: () -> Unit, modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(itemList.size) {id->
            InventoryItem(itemList[id], modifier = Modifier
                .clickable {
                    currentImageUri = itemList[id].uri
                    onItemClick()
                })
        }
    }
//    LazyColumn(modifier = modifier) {
//        items(items = itemList, key = { it.id }) { item ->
//            InventoryItem(item = item,
//                modifier = Modifier
//                    .padding(dimensionResource(id = R.dimen.padding_small))
//                    .clickable { onItemClick() })
//        }
//    }
}

@Composable
private fun InventoryItem(
    item: ImageItem, modifier: Modifier = Modifier
) {
//    Card(
//        modifier = modifier,
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
    val conf = LocalConfiguration.current
    AsyncImage(
        model = item.uri,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.height(conf.screenWidthDp.dp/3).width(conf.screenWidthDp.dp/3)
    )
//    }
}

//modifier = Modifier.height(conf.screenWidthDp.dp/2).width(conf.screenWidthDp.dp/2)