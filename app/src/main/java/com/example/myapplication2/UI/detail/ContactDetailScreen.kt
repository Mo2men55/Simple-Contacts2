package com.example.myapplication2.UI.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication2.R
import com.example.myapplication2.data.Contact
import com.example.myapplication2.UI.AppViewModelProvider
import com.example.myapplication2.UI.components.ContactAvatar
import com.example.myapplication2.UI.dialContact
import com.example.myapplication2.UI.emailContact
import com.example.myapplication2.UI.messageContact
import com.example.myapplication2.UI.shareContact
import com.example.myapplication2.UI.theme.MyApplicationTheme

@Composable
fun ContactDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: ContactDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val contact by viewModel.contact.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    ContactDetailContent(
        contact = contact,
        onBack = onBack,
        onEdit = { contact?.let { onEdit(it.id) } },
        onDelete = { showDeleteDialog = true },
        onFavoriteClick = viewModel::toggleFavorite,
        onCall = { contact?.let(context::dialContact) },
        onMessage = { contact?.let(context::messageContact) },
        onEmail = { contact?.let(context::emailContact) },
        onShare = { contact?.let(context::shareContact) },
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_contact_title)) },
            text = { Text(stringResource(R.string.delete_contact_message, contact?.name.orEmpty())) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(onDeleted = onBack)
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactDetailContent(
    contact: Contact?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onEmail: () -> Unit,
    onShare: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    if (contact != null) {
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (contact.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = stringResource(
                                    if (contact.isFavorite) R.string.remove_favorite else R.string.add_favorite
                                ),
                            )
                        }
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Filled.Edit, stringResource(R.string.edit))
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        if (contact == null) return@Scaffold

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Keep content readable on tablets / landscape instead of stretching edge to edge.
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                ContactAvatar(
                    initials = contact.initials,
                    size = 112.dp,
                    textStyle = MaterialTheme.typography.displaySmall,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    QuickAction(Icons.Filled.Call, stringResource(R.string.call), onCall)
                    QuickAction(Icons.AutoMirrored.Filled.Message, stringResource(R.string.message), onMessage)
                    QuickAction(
                        icon = Icons.Filled.Email,
                        label = stringResource(R.string.email),
                        onClick = onEmail,
                        enabled = contact.email.isNotBlank(),
                    )
                    QuickAction(Icons.Filled.Share, stringResource(R.string.share), onShare)
                }
                Spacer(Modifier.height(24.dp))
                Card(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(contact.phone) },
                        supportingContent = { Text(stringResource(R.string.phone)) },
                        leadingContent = { Icon(Icons.Filled.Phone, contentDescription = null) },
                    )
                    if (contact.email.isNotBlank()) {
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(contact.email) },
                            supportingContent = { Text(stringResource(R.string.email)) },
                            leadingContent = { Icon(Icons.Filled.Email, contentDescription = null) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(onClick = onClick, enabled = enabled) {
            Icon(icon, contentDescription = label)
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailPreview() {
    MyApplicationTheme {
        ContactDetailContent(
            contact = Contact(
                id = 1,
                name = "Ahmed Ali",
                phone = "+20 100 123 4567",
                email = "ahmed@example.com",
                isFavorite = true,
            ),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onFavoriteClick = {},
            onCall = {},
            onMessage = {},
            onEmail = {},
            onShare = {},
        )
    }
}
