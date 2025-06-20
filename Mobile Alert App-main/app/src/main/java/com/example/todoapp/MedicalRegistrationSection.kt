// Required imports (add these to your file)
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.local.entities.MedicalUnitsEntity
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Composable
fun MedicalRegistrationSection(
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingUnit by remember { mutableStateOf<MedicalUnitsEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf<MedicalUnitsEntity?>(null) }

    val viewModel: MyviewModel = viewModel()

    val medicalUnits by viewModel.allMedicalUnits.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medical Units",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Unit")
            }
        }

        // Medical Units List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(medicalUnits) { unit ->
                MedicalUnitCard(
                    medicalUnit = unit,
                    onEdit = { editingUnit = it },
                    onDelete = { showDeleteDialog = it }
                )
            }

            if (medicalUnits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No medical units registered yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingUnit != null) {
        MedicalUnitDialog(
            medicalUnit = editingUnit,
            onDismiss = {
                showAddDialog = false
                editingUnit = null
            },
            onSave = { unit ->
                coroutineScope.launch(Dispatchers.IO) {
                    if (editingUnit != null) {
                        viewModel.updateMedicalUnit(unit)
                    } else {
                        viewModel.createMedicalUnit(unit)
                    }
                }
                showAddDialog = false
                editingUnit = null
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { unit ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Medical Unit") },
            text = { Text("Are you sure you want to delete '${unit.medical_unit_name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteMedicalUnit(unit)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MedicalUnitCard(
    medicalUnit: MedicalUnitsEntity,
    onEdit: (MedicalUnitsEntity) -> Unit,
    onDelete: (MedicalUnitsEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicalUnit.medical_unit_name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Address: ${medicalUnit.address}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Phone: ${medicalUnit.phone_number}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (medicalUnit.is_active) Color.Green else Color.Red,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (medicalUnit.is_active) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (medicalUnit.is_active) Color.Green else Color.Red
                        )
                    }
                }

                Column {
                    IconButton(onClick = { onEdit(medicalUnit) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onDelete(medicalUnit) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalUnitDialog(
    medicalUnit: MedicalUnitsEntity? = null,
    onDismiss: () -> Unit,
    onSave: (MedicalUnitsEntity) -> Unit
) {
    var name by remember { mutableStateOf(medicalUnit?.medical_unit_name ?: "") }
    var address by remember { mutableStateOf(medicalUnit?.address ?: "") }
    var phoneNumber by remember { mutableStateOf(medicalUnit?.phone_number ?: "") }
    var isActive by remember { mutableStateOf(medicalUnit?.is_active ?: true) }

    var nameError by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    fun validateInputs(): Boolean {
        var isValid = true

        nameError = if (name.isBlank()) {
            isValid = false
            "Name is required"
        } else ""

        addressError = if (address.isBlank()) {
            isValid = false
            "Address is required"
        } else ""

        phoneError = if (phoneNumber.isBlank()) {
            isValid = false
            "Phone number is required"
        } else ""

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (medicalUnit == null) "Add Medical Unit" else "Edit Medical Unit")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = ""
                    },
                    label = { Text("Medical Unit Name") },
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(nameError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Address Field
                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        addressError = ""
                    },
                    label = { Text("Address") },
                    maxLines = 3,
                    isError = addressError.isNotEmpty(),
                    supportingText = if (addressError.isNotEmpty()) {
                        { Text(addressError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneError = ""
                    },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError.isNotEmpty(),
                    supportingText = if (phoneError.isNotEmpty()) {
                        { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Status",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateInputs()) {
                        val unit = if (medicalUnit == null) {
                            MedicalUnitsEntity(
                                medical_unit_name = name.trim(),
                                address = address.trim(),
                                phone_number = phoneNumber.trim(),
                                is_active = isActive
                            )
                        } else {
                            medicalUnit.copy(
                                medical_unit_name = name.trim(),
                                address = address.trim(),
                                phone_number = phoneNumber.trim(),
                                is_active = isActive
                            )
                        }
                        onSave(unit)
                    }
                }
            ) {
                Text(if (medicalUnit == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}