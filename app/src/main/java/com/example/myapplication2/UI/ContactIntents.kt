package com.example.myapplication2.UI

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.example.myapplication2.R
import com.example.myapplication2.data.Contact

/** Implicit intents that hand off contact actions to other apps (dialer, SMS, email, share sheet). */

fun Context.dialContact(contact: Contact) =
    launchSafely(Intent(Intent.ACTION_DIAL, "tel:${contact.phone}".toUri()))

fun Context.messageContact(contact: Contact) =
    launchSafely(Intent(Intent.ACTION_SENDTO, "smsto:${contact.phone}".toUri()))

fun Context.emailContact(contact: Contact) =
    launchSafely(Intent(Intent.ACTION_SENDTO, "mailto:${contact.email}".toUri()))

fun Context.shareContact(contact: Contact) {
    val details = buildString {
        appendLine(contact.name)
        appendLine(contact.phone)
        if (contact.email.isNotBlank()) appendLine(contact.email)
    }.trim()
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, contact.name)
        putExtra(Intent.EXTRA_TEXT, details)
    }
    launchSafely(Intent.createChooser(sendIntent, getString(R.string.share_contact)))
}

private fun Context.launchSafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_app_found, Toast.LENGTH_SHORT).show()
    }
}
