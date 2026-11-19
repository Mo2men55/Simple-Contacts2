package com.example.myapplication2.UI.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication2.R
import com.example.myapplication2.data.Contact
import com.example.myapplication2.UI.AppViewModelProvider
import com.example.myapplication2.UI.components.ContactAvatar
import com.example.myapplication2.UI.dialContact
import com.example.myapplication2.UI.theme.MyApplicationTheme

@Composable
fun ContactListScreen(
    onContactClick: (Contact) -> Unit,
    onAddContact: () -> Unit,
    viewModel: ContactListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ContactListContent(
        query = query,
        contacts = contacts,
        onQueryChange = viewModel::onQueryChange,
        onContactClick = onContactClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onCallClick = context::dialContact,
        onAddContact = onAddContact,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactListContent(
    query: String,
    contacts: List<Contact>?,
    onQueryChange: (String) -> Unit,
    onContactClick: (Contact) -> Unit,
    onFavoriteClick: (Contact) -> Unit,
    onCallClick: (Contact) -> Unit,
    onAddContact: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddContact,
                icon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                text = { Text(stringResource(R.string.add_contact)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchField(
                query = query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when {
                contacts == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                contacts.isEmpty() -> EmptyState(isSearching = query.isNotBlank())

                else -> LazyVerticalGrid(
                    // Adaptive columns: one column on phones, more on tablets / landscape.
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(contacts, key = Contact::id) { contact ->
                        ContactCard(
                            contact = contact,
                            onClick = { onContactClick(contact) },
                            onFavoriteClick = { onFavoriteClick(contact) },
                            onCallClick = { onCallClick(contact) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier,
    )
}

@Composable
private fun ContactCard(
    contact: Contact,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        ) {
            ContactAvatar(initials = contact.initials)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (contact.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = stringResource(
                        if (contact.isFavorite) R.string.remove_favorite else R.string.add_favorite
                    ),
                    tint = if (contact.isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onCallClick) {
                Icon(Icons.Filled.Call, contentDescription = stringResource(R.string.call))
            }
        }
    }
}

@Composable
private fun EmptyState(isSearching: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Filled.Search else Icons.Outlined.Contacts,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.size(16.dp))
        Text(
            text = stringResource(if (isSearching) R.string.no_results else R.string.no_contacts),
            style = MaterialTheme.typography.titleMedium,
        )
        if (!isSearching) {
            Text(
                text = stringResource(R.string.no_contacts_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactListPreview() {
    MyApplicationTheme {
        ContactListContent(
            query = "",
            contacts = listOf(
                Contact(id = 1, name = "Ahmed Ali", phone = "+20 100 123 4567", isFavorite = true),
                Contact(id = 2, name = "Sara Mohamed", phone = "+20 111 987 6543"),
            ),
            onQueryChange = {},
            onContactClick = {},
            onFavoriteClick = {},
            onCallClick = {},
            onAddContact = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactListEmptyPreview() {
    MyApplicationTheme {
        ContactListContent(
            query = "",
            contacts = emptyList(),
            onQueryChange = {},
            onContactClick = {},
            onFavoriteClick = {},
            onCallClick = {},
            onAddContact = {},
        )
    }
}
