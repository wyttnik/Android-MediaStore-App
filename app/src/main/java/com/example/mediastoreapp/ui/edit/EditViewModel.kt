package com.example.mediastoreapp.ui.edit

import android.app.Application
import androidx.exifinterface.media.ExifInterface
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mediastoreapp.ui.General
import java.io.File
import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * ViewModel to retrieve and update an item from the [ItemsRepository]'s data source.
 */
class EditViewModel(private val app: Application) : AndroidViewModel(app) {
    /**
     * Holds current item ui state
     */
    var tagUiState by mutableStateOf(TagUiState())
        private set

    init {
//        val text = "2022-01:06 20:30:45"
//        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        val localDateTime = LocalDateTime.parse(text, pattern)

//        Log.d("test-time", "${localDateTime}")
        initState()
    }

    private fun initState() {
        val imageUri = MediaStore.setRequireOriginal(General.currentImageUri!!)
        app.contentResolver.openInputStream(imageUri)?.use { input ->
            ExifInterface(input).run {
                val tagDetails = TagDetails(
                    dateTime = getAttribute(ExifInterface.TAG_DATETIME) ?: "",
                    latitude = getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "",
                    longitude = getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "",
                    deviceType = getAttribute(ExifInterface.TAG_MAKE) ?: "",
                    model= getAttribute(ExifInterface.TAG_MODEL) ?: ""
                )
                tagUiState = TagUiState(tagDetails,validateInput(tagDetails))
            }
            input.close()
        }
    }

    fun updateUiState(tagDetails: TagDetails) {
        tagUiState = TagUiState(TagDetails(
            dateTime = tagDetails.dateTime,
            latitude = tagDetails.latitude,
            longitude = tagDetails.longitude,
            deviceType = tagDetails.deviceType,
            model = tagDetails.model
        ), validateInput(tagDetails))

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
                setAttribute(ExifInterface.TAG_MAKE, tagUiState.tagDetails.deviceType)
                setAttribute(ExifInterface.TAG_DATETIME, tagUiState.tagDetails.dateTime)
                setAttribute(ExifInterface.TAG_GPS_LATITUDE, tagUiState.tagDetails.latitude)
                setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tagUiState.tagDetails.longitude)
                setAttribute(ExifInterface.TAG_MODEL, tagUiState.tagDetails.model)
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

    private fun validateInput(uiState: TagDetails = tagUiState.tagDetails): Boolean {
        return with(uiState) {
            stringValidator(deviceType) && stringValidator(model) && dateValidator(dateTime) &&
                    numericValidator(latitude) && numericValidator(longitude)
        }
    }

    fun stringValidator(string: String) = string.length >= 2
    fun numericValidator(numString: String) = Regex("[^a-zA-Z]+").matches(numString)

    fun dateValidator(string: String):Boolean {
        val pattern = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
        try{
            pattern.parse(string)
        } catch(e: DateTimeParseException) {
            return false
        }
        return true
//        val localDateTime = LocalDateTime.parse(text, pattern)
    }
}

data class TagDetails(
    val dateTime: String = "",
    val latitude:String = "",
    val longitude: String = "",
    val deviceType: String = "",
    val model: String = ""
)

data class TagUiState(
    val tagDetails: TagDetails = TagDetails(),
    val validCheck: Boolean = false
)