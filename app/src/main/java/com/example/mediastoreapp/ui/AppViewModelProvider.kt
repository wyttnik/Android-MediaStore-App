package com.example.mediastoreapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mediastoreapp.MediaApplication
import com.example.mediastoreapp.ui.chooser.ChooserViewModel
import com.example.mediastoreapp.ui.edit.EditViewModel
import com.example.mediastoreapp.ui.home.HomeViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel()
        }
        initializer {
            ChooserViewModel(mediaApplication())
        }
        initializer {
            EditViewModel(mediaApplication())
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [MediaApplication].
 */
fun CreationExtras.mediaApplication(): MediaApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MediaApplication)