package com.example.myapplication2.UI.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication2.UI.detail.ContactDetailScreen
import com.example.myapplication2.UI.edit.ContactEditScreen
import com.example.myapplication2.UI.list.ContactListScreen

object ContactsDestinations {
    const val CONTACT_ID_ARG = "contactId"
    const val NEW_CONTACT_ID = -1L

    const val LIST = "contacts"
    const val DETAIL = "contact/{$CONTACT_ID_ARG}"
    const val EDIT = "edit?$CONTACT_ID_ARG={$CONTACT_ID_ARG}"

    fun detail(id: Long) = "contact/$id"
    fun edit(id: Long = NEW_CONTACT_ID) = "edit?$CONTACT_ID_ARG=$id"
}

@Composable
fun ContactsNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ContactsDestinations.LIST,
        modifier = modifier,
    ) {
        composable(ContactsDestinations.LIST) {
            ContactListScreen(
                onContactClick = { navController.navigate(ContactsDestinations.detail(it.id)) },
                onAddContact = { navController.navigate(ContactsDestinations.edit()) },
            )
        }
        composable(
            route = ContactsDestinations.DETAIL,
            arguments = listOf(
                navArgument(ContactsDestinations.CONTACT_ID_ARG) { type = NavType.LongType }
            ),
        ) {
            ContactDetailScreen(
                onBack = navController::navigateUp,
                onEdit = { navController.navigate(ContactsDestinations.edit(it)) },
            )
        }
        composable(
            route = ContactsDestinations.EDIT,
            arguments = listOf(
                navArgument(ContactsDestinations.CONTACT_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = ContactsDestinations.NEW_CONTACT_ID
                }
            ),
        ) {
            ContactEditScreen(onDone = navController::navigateUp)
        }
    }
}
