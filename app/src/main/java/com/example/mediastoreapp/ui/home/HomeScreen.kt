package com.example.mediastoreapp.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mediastoreapp.InventoryTopAppBar
import com.example.mediastoreapp.R
import com.example.mediastoreapp.ui.General.currentImageUri
import com.example.mediastoreapp.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToImageChooser: () -> Unit,
    navigateToTagEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                onTagEditClick = navigateToTagEdit,
                isHomeScreen = true
            )
        }
    ) { innerPadding ->
        HomeBody(
            onItemClick = navigateToImageChooser,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun HomeBody(
    onItemClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val context = LocalContext.current
        if (currentImageUri != null) {
            val conf = LocalConfiguration.current
            AsyncImage(
                model = currentImageUri,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.height(conf.screenWidthDp.dp).width(conf.screenWidthDp.dp)
            )

            Text("Image Tags: ", modifier = Modifier.padding(top=20.dp))
            Spacer(Modifier.weight(1f))
            val imageUri = MediaStore.setRequireOriginal(currentImageUri!!)
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                ExifInterface(input).run {
                    getAttribute(ExifInterface.TAG_DATETIME)?.let {
                        Text("Creation date: $it")
                    }
                    getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                        Text("GPS_latitude: $it")
                    }
                    getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                        Text("GPS_longitude: $it")
                    }
                    getAttribute(ExifInterface.TAG_MAKE)?.let {
                        Text("Device type: $it")
                    }
                    getAttribute(ExifInterface.TAG_MODEL)?.let {
                        Text("Model: $it")
                    }
                }
                input.close()
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onItemClick,
            shape = MaterialTheme.shapes.small,
            enabled = true
        ){
            Text("Select Image")
        }
        Spacer(Modifier.weight(1f))
    }
}

//{
//    val intent = Intent(Intent.ACTION_PICK,
//        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//    launcher.launch(intent)
//}