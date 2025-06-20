//package com.example.todoapp
//
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
//import androidx.wear.compose.foundation.rememberRevealState
//import androidx.wear.compose.material.ExperimentalWearMaterialApi
//import androidx.wear.compose.material.SwipeToRevealCard
//import com.example.todoapp.local.entities.ContactEntity
//import com.example.todoapp.viewModel.MyviewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ContactsScreen(navHostController: NavHostController? = null) {
//    var expanded by remember { mutableStateOf(false) }
//    var searchQuery by remember { mutableStateOf("") }
//    var showSearch by remember { mutableStateOf(false) }
//    var openModal by remember { mutableStateOf(false) }
//    var contactToEdit by remember { mutableStateOf<ContactEntity?>(null) }
//    var showDeleteConfirmation by remember { mutableStateOf<ContactEntity?>(null) }
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    val viewModel: MyviewModel = viewModel()
//
//    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())
//
//    // Keep track of deleted contact for undo functionality
//    var lastDeletedContact by remember { mutableStateOf<ContactEntity?>(null) }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
//                title = {
//                    if (showSearch) {
//                        OutlinedTextField(
//                            value = searchQuery,
//                            onValueChange = { searchQuery = it },
//                            placeholder = { Text("Search contacts...") },
//                            modifier = Modifier.fillMaxWidth(),
//                            singleLine = true,
//                            shape = CircleShape,
//                            trailingIcon = {
//                                IconButton(onClick = {
//                                    searchQuery = ""
//                                    showSearch = false
//                                }) {
//                                    Icon(Icons.Default.Close, "Close Search")
//                                }
//                            }
//                        )
//                    } else {
//                        Text("Contacts")
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        if (navHostController != null) {
//                            navHostController.popBackStack()
//                        }
//                    }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    if (!showSearch) {
//                        IconButton(onClick = { showSearch = true }) {
//                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//                        }
//                        IconButton(onClick = { expanded = !expanded }) {
//                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
//                        }
//                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                            DropdownMenuItem(
//                                onClick = {
//                                    if (navHostController != null) {
//                                        navHostController.navigate("settings")
//                                    }
//                                },
//                                text = { Text("Settings") },
//                                leadingIcon = {
//                                    Icon(Icons.Default.Settings, "Settings")
//                                }
//                            )
//                        }
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    contactToEdit = null
//                    openModal = true
//                },
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(Icons.Default.Add, "Add Contact")
//            }
//        }
//    ) { paddingValues ->
//        ContactsScreenContent(
//            modifier = Modifier.padding(paddingValues),
//            contacts = contacts.filter { contact ->
//                if (searchQuery.isEmpty()) true
//                else contact.name.contains(searchQuery, ignoreCase = true) ||
//                        contact.phoneNumber.contains(searchQuery, ignoreCase = true)
//            },
//            openModal = openModal,
//            onCloseModal = { openModal = false },
//            contactToEdit = contactToEdit,
//            onEditContact = { contact ->
//                contactToEdit = contact
//                openModal = true
//            },
//            onDeleteContact = { contact ->
//                showDeleteConfirmation = contact
//            },
//            viewModel = viewModel,
//            snackbarHostState = snackbarHostState,
//            showDeleteConfirmation = showDeleteConfirmation,
//            onConfirmDelete = { contact ->
//                lastDeletedContact = contact
//                viewModel.deleteContacts(contact)
//                showDeleteConfirmation = null
//
//                scope.launch {
//                    val result = snackbarHostState.showSnackbar(
//                        message = "Contact deleted",
//                        actionLabel = "UNDO",
//                        duration = SnackbarDuration.Short
//                    )
//
//                    if (result == SnackbarResult.ActionPerformed) {
//                        lastDeletedContact?.let {
//                            viewModel.addContact(it.copy(id = 0)) // Reset ID for new insertion
//                        }
//                    }
//                }
//            },
//            onDismissDeleteConfirmation = {
//                showDeleteConfirmation = null
//            },
//            scope = scope
//        )
//    }
//}
//
//@Composable
//private fun ContactsScreenContent(
//    modifier: Modifier = Modifier,
//    contacts: List<ContactEntity>,
//    openModal: Boolean,
//    onCloseModal: () -> Unit,
//    contactToEdit: ContactEntity?,
//    onEditContact: (ContactEntity) -> Unit,
//    onDeleteContact: (ContactEntity) -> Unit,
//    viewModel: MyviewModel,
//    snackbarHostState: SnackbarHostState,
//    showDeleteConfirmation: ContactEntity?,
//    onConfirmDelete: (ContactEntity) -> Unit,
//    onDismissDeleteConfirmation: () -> Unit,
//    scope: CoroutineScope
//) {
//    Column(modifier = modifier) {
//        if (contacts.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Phone,
//                        contentDescription = null,
//                        modifier = Modifier.size(100.dp),
//                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        text = "No contacts found",
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                    )
//                }
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(
//                    items = contacts,
//                    key = { contact -> contact.id }
//                ) { contact ->
//                    ContactItem(
//                        contact = contact,
//                        onDelete = { onDeleteContact(contact) },
//                        onEdit = { onEditContact(contact) },
//                        scope = scope,
//                        snackbarHostState = snackbarHostState
//                    )
//                }
//            }
//        }
//
//        if (openModal) {
//            ContactDialog(
//                contact = contactToEdit,
//                onDismiss = onCloseModal,
//                onSave = { name, number, isActive ->
//                    if (contactToEdit != null) {
//                        viewModel.addContact(
//                            contactToEdit.copy(
//                                name = name,
//                                phoneNumber = number,
//                                isActive = isActive,
//                                dateModified = Date(),
//                                modifiedBy = "User"
//                            )
//                        )
//                    } else {
//                        viewModel.addContact(
//                            ContactEntity(
//                                userId = 1,
//                                name = name,
//                                phoneNumber = number,
//                                isActive = isActive,
//                                dateCreated = Date(),
//                                createdBy = "User"
//                            )
//                        )
//                    }
//                    onCloseModal()
//                }
//            )
//        }
//
//        // Delete confirmation dialog
//        if (showDeleteConfirmation != null) {
//            AlertDialog(
//                onDismissRequest = onDismissDeleteConfirmation,
//                title = { Text("Delete Contact") },
//                text = { Text("Are you sure you want to delete ${showDeleteConfirmation.name}?") },
//                confirmButton = {
//                    Button(
//                        onClick = { onConfirmDelete(showDeleteConfirmation) },
//                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//                    ) {
//                        Text("Delete")
//                    }
//                },
//                dismissButton = {
//                    OutlinedButton(onClick = onDismissDeleteConfirmation) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalWearMaterialApi::class, ExperimentalWearFoundationApi::class)
//@Composable
//private fun ContactItem(
//    contact: ContactEntity,
//    onDelete: () -> Unit,
//    onEdit: () -> Unit,
//    scope: CoroutineScope,
//    snackbarHostState: SnackbarHostState
//) {
//
//    val revealState = rememberRevealState()
//    val context = LocalContext.current
//
//    SwipeToRevealCard (
//        revealState = revealState,
//        primaryAction = {
//            IconButton(onClick = {
//                Toast.makeText(context, "Deleting button Clicked", Toast.LENGTH_SHORT).show()
//            }) {
//                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
//            }
//        },
//        onFullSwipe = {
//            scope.launch {
//                snackbarHostState.showSnackbar(message = "Contact has been deleted", actionLabel = "UNDO", withDismissAction = true, duration = SnackbarDuration.Short)
//            }
//        },
//        secondaryAction = {
//            IconButton(onClick = {
//                Toast.makeText(context, "Edit button Clicked", Toast.LENGTH_SHORT).show()
//            }) {
//                Icon(imageVector = Icons.Default.Edit, contentDescription = "Delete")
//            }
//        },
//        undoPrimaryAction = {},
//        undoSecondaryAction = {}
//    ){
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = MaterialTheme.colorScheme.surface,
//            )
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.primaryContainer),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = contact.name.take(1).uppercase(),
//                        style = MaterialTheme.typography.headlineSmall,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        text = contact.name,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = contact.phoneNumber,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                if (contact.isActive) {
//                    Badge(
//                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
//                    ) {
//                        Text(
//                            text = "Active",
//                            color = MaterialTheme.colorScheme.primary,
//                            style = MaterialTheme.typography.labelSmall,
//                            modifier = Modifier.padding(horizontal = 4.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ContactDialog(
//    contact: ContactEntity?,
//    onDismiss: () -> Unit,
//    onSave: (name: String, number: String, isActive: Boolean) -> Unit
//) {
//    var name by remember { mutableStateOf(contact?.name ?: "") }
//    var number by remember { mutableStateOf(contact?.phoneNumber ?: "") }
//    var isActive by remember { mutableStateOf(contact?.isActive ?: true) }
//
//    var nameError by remember { mutableStateOf<String?>(null) }
//    var numberError by remember { mutableStateOf<String?>(null) }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Text(
//                    text = if (contact == null) "Add Contact" else "Edit Contact",
//                    style = MaterialTheme.typography.headlineSmall
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = {
//                        name = it
//                        nameError = if (it.isBlank()) "Name cannot be empty" else null
//                    },
//                    label = { Text("Name") },
//                    modifier = Modifier.fillMaxWidth(),
//                    isError = nameError != null,
//                    supportingText = nameError?.let { { Text(it) } }
//                )
//
//                OutlinedTextField(
//                    value = number,
//                    onValueChange = {
//                        number = it
//                        numberError = when {
//                            it.isBlank() -> "Phone number cannot be empty"
//                            !it.all { c -> c.isDigit() || c in "+-() " } -> "Invalid phone number format"
//                            else -> null
//                        }
//                    },
//                    label = { Text("Phone Number") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                    modifier = Modifier.fillMaxWidth(),
//                    isError = numberError != null,
//                    supportingText = numberError?.let { { Text(it) } }
//                )
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                ) {
//                    Text("Is Active", style = MaterialTheme.typography.bodyMedium)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Switch(
//                        checked = isActive,
//                        onCheckedChange = { isActive = it }
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Cancel")
//                    }
//                    Button(
//                        onClick = { onSave(name, number, isActive) },
//                        modifier = Modifier.weight(1f),
//                        enabled = name.isNotBlank() && number.isNotBlank() && nameError == null && numberError == null
//                    ) {
//                        Text(if (contact == null) "Add" else "Update")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewContactsScreen() {
//    ContactsScreen()
//}














package com.example.todoapp

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.SwipeToRevealCard
import com.example.todoapp.local.entities.ContactEntity
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navHostController: NavHostController? = null) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var openModal by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<ContactEntity?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<ContactEntity?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val viewModel: MyviewModel = viewModel()

    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())

    // Keep track of deleted contact for undo functionality
    var lastDeletedContact by remember { mutableStateOf<ContactEntity?>(null) }

    // Permission launcher for contacts access
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, show import dialog
            showImportDialog = true
        } else {
            // Permission denied
            Toast.makeText(context, "Contacts permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search contacts...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = CircleShape,
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    showSearch = false
                                }) {
                                    Icon(Icons.Default.Close, "Close Search")
                                }
                            }
                        )
                    } else {
                        Text("Contacts")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navHostController != null) {
                            navHostController.popBackStack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {
                            // Request contacts permission before showing import dialog
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                                PackageManager.PERMISSION_GRANTED) {
                                showImportDialog = true
                            } else {
                                contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Import Contacts")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    contactToEdit = null
                    openModal = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Contact")
            }
        }
    ) { paddingValues ->
        ContactsScreenContent(
            modifier = Modifier.padding(paddingValues),
            contacts = contacts.filter { contact ->
                if (searchQuery.isEmpty()) true
                else contact.name.contains(searchQuery, ignoreCase = true) ||
                        contact.phoneNumber.contains(searchQuery, ignoreCase = true)
            },
            openModal = openModal,
            onCloseModal = { openModal = false },
            contactToEdit = contactToEdit,
            onEditContact = { contact ->
                contactToEdit = contact
                openModal = true
            },
            onDeleteContact = { contact ->
                showDeleteConfirmation = contact
            },
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            showDeleteConfirmation = showDeleteConfirmation,
            onConfirmDelete = { contact ->
                lastDeletedContact = contact
                viewModel.deleteContacts(contact)
                showDeleteConfirmation = null

                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Contact deleted",
                        actionLabel = "UNDO",
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        lastDeletedContact?.let {
                            viewModel.addContact(it.copy(id = 0)) // Reset ID for new insertion
                        }
                    }
                }
            },
            onDismissDeleteConfirmation = {
                showDeleteConfirmation = null
            },
            scope = scope
        )

        // Import Contacts Dialog
        if (showImportDialog) {
            ImportContactsDialog(
                onDismiss = { showImportDialog = false },
                onImport = { selectedContacts ->
                    // Check for existing contacts to avoid duplicates
                    val existingNumbers = contacts.map { it.phoneNumber }
                    val newContacts = selectedContacts.filter { contact ->
                        contact.phoneNumber !in existingNumbers
                    }

                    if (newContacts.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("No new contacts to import")
                        }
                    } else {
                        // Add new contacts to database
                        newContacts.forEach { contact ->
                            viewModel.addContact(contact)
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${newContacts.size} contacts imported successfully",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    showImportDialog = false
                }
            )
        }
    }
}

@Composable
private fun ContactsScreenContent(
    modifier: Modifier = Modifier,
    contacts: List<ContactEntity>,
    openModal: Boolean,
    onCloseModal: () -> Unit,
    contactToEdit: ContactEntity?,
    onEditContact: (ContactEntity) -> Unit,
    onDeleteContact: (ContactEntity) -> Unit,
    viewModel: MyviewModel,
    snackbarHostState: SnackbarHostState,
    showDeleteConfirmation: ContactEntity?,
    onConfirmDelete: (ContactEntity) -> Unit,
    onDismissDeleteConfirmation: () -> Unit,
    scope: CoroutineScope
) {
    Column(modifier = modifier) {
        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No contacts found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = contacts,
                    key = { contact -> contact.id }
                ) { contact ->
                    ContactItem(
                        contact = contact,
                        onDelete = { onDeleteContact(contact) },
                        onEdit = { onEditContact(contact) },
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        viewModel = viewModel
                    )
                }
            }
        }

        if (openModal) {
            ContactDialog(
                contact = contactToEdit,
                onDismiss = onCloseModal,
                onSave = { name, number, isActive ->
                    if (contactToEdit != null) {
                        viewModel.updateContact(
                            contactToEdit.copy(
                                name = name,
                                phoneNumber = number,
                                isActive = isActive,
                                dateModified = Date(),
                                modifiedBy = "User"
                            )
                        )
                    } else {
                        viewModel.addContact(
                            ContactEntity(
                                userId = 1,
                                name = name,
                                phoneNumber = number,
                                isActive = isActive,
                                dateCreated = Date(),
                                createdBy = "User"
                            )
                        )
                    }
                    onCloseModal()
                }
            )
        }

        // Delete confirmation dialog
        if (showDeleteConfirmation != null) {
            AlertDialog(
                onDismissRequest = onDismissDeleteConfirmation,
                title = { Text("Delete Contact") },
                text = { Text("Are you sure you want to delete ${showDeleteConfirmation.name}?") },
                confirmButton = {
                    Button(
                        onClick = { onConfirmDelete(showDeleteConfirmation) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = onDismissDeleteConfirmation) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalWearFoundationApi::class)
@Composable
private fun ContactItem(
    contact: ContactEntity,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    viewModel: MyviewModel
) {
    val revealState = rememberRevealState()
    val context = LocalContext.current

    SwipeToRevealCard(
        revealState = revealState,
        primaryAction = {
            IconButton(onClick = {
                onDelete()
                Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
            }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        },
        onFullSwipe = {
            scope.launch {
                viewModel.deleteContacts(contact)
                snackbarHostState.showSnackbar(
                    message = "Contact has been deleted",
                    actionLabel = "UNDO",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        },
        secondaryAction = {
            IconButton(onClick = {
                onEdit()
                Toast.makeText(context, "Editing contact", Toast.LENGTH_SHORT).show()
            }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
        },
        undoPrimaryAction = {},
        undoSecondaryAction = {}
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (contact.isActive) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Active",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDialog(
    contact: ContactEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, number: String, isActive: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var number by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var isActive by remember { mutableStateOf(contact?.isActive ?: true) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var numberError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (contact == null) "Add Contact" else "Edit Contact",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Name cannot be empty" else null
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } }
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = {
                        number = it
                        numberError = when {
                            it.isBlank() -> "Phone number cannot be empty"
                            !it.all { c -> c.isDigit() || c in "+-() " } -> "Invalid phone number format"
                            else -> null
                        }
                    },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = numberError != null,
                    supportingText = numberError?.let { { Text(it) } }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Is Active", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onSave(name, number, isActive) },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && number.isNotBlank() && nameError == null && numberError == null
                    ) {
                        Text(if (contact == null) "Add" else "Update")
                    }
                }
            }
        }
    }
}

@Composable
fun ImportContactsDialog(
    onDismiss: () -> Unit,
    onImport: (List<ContactEntity>) -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    // Fetch device contacts
    val deviceContacts = remember {
        mutableStateListOf<DeviceContact>()
    }

    // Selected contacts to import
    val selectedContacts = remember {
        mutableStateListOf<DeviceContact>()
    }

    // Load device contacts
    LaunchedEffect(Unit) {
        deviceContacts.clear()
        deviceContacts.addAll(fetchDeviceContacts(contentResolver))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Import Contacts",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Search field
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search contacts...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selection indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedContacts.size} selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (deviceContacts.isNotEmpty() && selectedContacts.size < deviceContacts.size) {
                        TextButton(onClick = {
                            selectedContacts.clear()
                            selectedContacts.addAll(deviceContacts)
                        }) {
                            Text("Select All")
                        }
                    } else if (selectedContacts.isNotEmpty()) {
                        TextButton(onClick = {
                            selectedContacts.clear()
                        }) {
                            Text("Clear All")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Contact list
                val filteredContacts = deviceContacts.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.phoneNumber.contains(searchQuery, ignoreCase = true)
                }

                if (filteredContacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (deviceContacts.isEmpty()) "No contacts found on device" else "No contacts match your search",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(filteredContacts) { contact ->
                            val isSelected = selectedContacts.contains(contact)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!selectedContacts.contains(contact)) {
                                                selectedContacts.add(contact)
                                            }
                                        } else {
                                            selectedContacts.remove(contact)
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = contact.phoneNumber,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (filteredContacts.last() != contact) {
                                Divider(
                                    modifier = Modifier.padding(start = 56.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Convert selected device contacts to ContactEntity objects
                            val contactEntities = selectedContacts.map { contact ->
                                ContactEntity(
                                    userId = 1,
                                    name = contact.name,
                                    phoneNumber = contact.phoneNumber,
                                    isActive = true,
                                    dateCreated = Date(),
                                    createdBy = "Import"
                                )
                            }
                            onImport(contactEntities)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedContacts.isNotEmpty()
                    ) {
                        Text("Import (${selectedContacts.size})")
                    }
                }
            }
        }
    }
}

// Data class to represent device contacts
data class DeviceContact(
    val id: String,
    val name: String,
    val phoneNumber: String
)

// Function to fetch contacts from device
private fun fetchDeviceContacts(contentResolver: ContentResolver): List<DeviceContact> {
    val contacts = mutableListOf<DeviceContact>()

    try {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            val processedNumbers = HashSet<String>() // To avoid duplicate phone numbers

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex) ?: "Unknown"
                var number = cursor.getString(numberIndex) ?: ""

                // Format phone number (remove spaces, dashes, etc.)
                number = number.replace(Regex("[\\s-]"), "")

                // Skip if we've already processed this number
                if (number.isNotEmpty() && !processedNumbers.contains(number)) {
                    processedNumbers.add(number)
                    contacts.add(DeviceContact(id, name, number))
                }
            }
        }
    } catch (e: Exception) {
        // Handle exceptions
        e.printStackTrace()
    }

    return contacts
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewContactsScreen() {
    ContactsScreen()
}