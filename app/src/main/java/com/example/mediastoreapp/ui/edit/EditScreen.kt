package com.example.mediastoreapp.ui.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediastoreapp.InventoryTopAppBar
import com.example.mediastoreapp.R
import com.example.mediastoreapp.ui.AppViewModelProvider
import com.example.mediastoreapp.ui.navigation.NavigationDestination
import java.util.Calendar
import java.util.Date

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
            editUiState = viewModel.tagUiState,
            onSettingValueChange = viewModel::updateUiState,
            onSaveClick = viewModel::save,
            validators = mapOf(
                "stringValidator" to viewModel::stringValidator,
                "numericValidator" to viewModel::numericValidator,
                "dateValidator" to viewModel::dateValidator
            ),
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
    editUiState: TagUiState,
    onSettingValueChange: (TagDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    validators: Map<String, (String) -> Boolean>
) {
    val localContext = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        InputsForm(
            tagsDetails = editUiState.tagDetails,
            onValueChange = onSettingValueChange,
            modifier = Modifier.fillMaxWidth(),
            validators = validators
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
            enabled = editUiState.validCheck,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save tags")
        }
    }
}

@Composable
fun InputsForm(tagsDetails: TagDetails,
                 modifier: Modifier = Modifier,
                 onValueChange: (TagDetails) -> Unit = {},
               validators: Map<String, (String) -> Boolean>
){
    var deviceTagError by rememberSaveable { mutableStateOf(false) }
    var modelTagError by rememberSaveable { mutableStateOf(false) }
    var dateTagError by rememberSaveable { mutableStateOf(false) }
    var longTagError by rememberSaveable { mutableStateOf(false) }
    var latTagError by rememberSaveable { mutableStateOf(false) }
    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    mCalendar.time = Date()

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }
    val mDate = remember { mutableStateOf("") }

    // Creating a TimePicker dialod
    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "${if(mHour / 10 == 0) "0$mHour" else mHour}:${if(mMinute / 10 == 0) "0$mMinute" else mMinute}:00"
            val space = tagsDetails.dateTime.indexOf(" ")
            val toInsert = tagsDetails.copy(dateTime =
                tagsDetails.dateTime.replaceRange(space+1, tagsDetails.dateTime.length,mTime.value))
            onValueChange(toInsert)
            dateTagError = !validators["dateValidator"]!!.invoke(toInsert.dateTime)
        }, mHour, mMinute, true
    )
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mYear:${if((mMonth+1) / 10 == 0) "0${mMonth+1}" else mMonth+1}:${if(mDayOfMonth / 10 == 0) "0$mDayOfMonth" else mDayOfMonth}"
            val space = tagsDetails.dateTime.indexOf(" ")
            val toInsert = tagsDetails.copy(dateTime =
                tagsDetails.dateTime.replaceRange(0, space,mDate.value))
            onValueChange(toInsert)
            dateTagError = !validators["dateValidator"]!!.invoke(toInsert.dateTime)
        }, mYear, mMonth, mDay
    )

    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){

            Button(onClick = {
                mTimePickerDialog.show()
            }){
                Text("Pick Time")
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                mDatePickerDialog.show()
            }){
                Text("Pick Date")
            }
        }
        TextField(
            value = tagsDetails.dateTime,
            onValueChange = {
                onValueChange(tagsDetails.copy(dateTime = it))
                dateTagError = it.isNotBlank() && !validators["dateValidator"]!!.invoke(it)
            },
            label = { Text("Date") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = dateTagError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (dateTagError){
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = "Invalid data format",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        )
        TextField(
            value = tagsDetails.latitude,
            onValueChange = {
                onValueChange(tagsDetails.copy(latitude = it))
                latTagError = it.isNotBlank() && !validators["numericValidator"]!!.invoke(it)
            },
            label = { Text("GPS latitude") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = latTagError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (latTagError){
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = "Invalid latitude format",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        )
        TextField(
            value = tagsDetails.longitude,
            onValueChange = {
                onValueChange(tagsDetails.copy(longitude = it))
                longTagError = it.isNotBlank() && !validators["numericValidator"]!!.invoke(it)
            },
            label = { Text("GPS longitude") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = longTagError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (longTagError){
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = "Invalid longitude format",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        )
        TextField(
            value = tagsDetails.deviceType,
            onValueChange = {
                onValueChange(tagsDetails.copy(deviceType = it))
                deviceTagError = it.isNotBlank() && !validators["stringValidator"]!!.invoke(it)
            },
            label = { Text("Device type") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = deviceTagError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (deviceTagError){
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = "Invalid device type",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        )
        TextField(
            value = tagsDetails.model,
            onValueChange = {
                onValueChange(tagsDetails.copy(model = it))
                modelTagError = it.isNotBlank() && !validators["stringValidator"]!!.invoke(it)
            },
            label = { Text("Model of device") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = modelTagError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (modelTagError){
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = "Invalid device model",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}