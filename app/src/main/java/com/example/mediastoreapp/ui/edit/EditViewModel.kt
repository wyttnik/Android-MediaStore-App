package com.example.mediastoreapp.ui.edit

import android.app.Application
import androidx.exifinterface.media.ExifInterface
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mediastoreapp.ui.General
import java.io.File

/**
 * ViewModel to retrieve and update an item from the [ItemsRepository]'s data source.
 */
class EditViewModel(private val app: Application) : AndroidViewModel(app) {
    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    init {
        initState()
    }

    private fun initState() {
        val imageUri = MediaStore.setRequireOriginal(General.currentImageUri!!)
        app.contentResolver.openInputStream(imageUri)?.use { input ->
            ExifInterface(input).run {
                itemUiState = ItemUiState(
                    dateTime = getAttribute(ExifInterface.TAG_DATETIME) ?: "",
                    latitude = getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "",
                    longitude = getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "",
                    deviceType = getAttribute(ExifInterface.TAG_MAKE) ?: "",
                    model= getAttribute(ExifInterface.TAG_MODEL) ?: ""
                )
            }
            input.close()
        }
    }

    fun updateUiState(details: ItemUiState) {
        itemUiState = details
    }

    fun getFileUri():String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        app.contentResolver.query(General.currentImageUri!!, proj, null, null, null)?.use {cursor->
            val dataColumn = cursor.getColumnIndexOrThrow(proj[0])
            if (cursor.moveToFirst()){
                path = cursor.getString(dataColumn)
            }
            cursor.close()
        }
        return path
    }

    fun save() {
//        val imageUri = MediaStore.setRequireOriginal()
        getFileUri()?.run {
            ExifInterface(File(this)).run {
                setAttribute(ExifInterface.TAG_MAKE, itemUiState.deviceType)
                saveAttributes()
            }
        }


//        val imageUri = MediaStore.setRequireOriginal(General.currentImageUri!!)
//        app.contentResolver.openInputStream(imageUri)?.use { input ->
//            ExifInterface(input).run {
//                setAttribute(ExifInterface.TAG_MAKE, itemUiState.deviceType)
//                saveAttributes()
//            }
//            input.close()
//        }
//        val imageUri = MediaStore.setRequireOriginal(General.currentImageUri!!)

//        val imageUri = MediaStore.setRequireOriginal(General.currentImageUri!!)
//        app.contentResolver.openFileDescriptor(General.currentImageUri!!, "w")?.use {pfd->
//            ExifInterface(pfd.fileDescriptor).run {
//                setAttribute(ExifInterface.TAG_MAKE, itemUiState.deviceType)
//                saveAttributes()
//            }
//            pfd.close()
//        }
    }
}

data class ItemUiState(
    val dateTime: String = "",
    val latitude:String = "",
    val longitude: String = "",
    val deviceType: String = "",
    val model: String = ""
)