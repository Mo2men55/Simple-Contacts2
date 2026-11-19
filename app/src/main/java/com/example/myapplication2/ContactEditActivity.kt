package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication2.data.Contact
import com.example.myapplication2.data.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Contact Edit / Add Activity.
 * Handles form input validation, incoming shared text handling,
 * Room Database persistence on background threads, and lifecycle logging.
 */
class ContactEditActivity : AppCompatActivity() {

    private val TAG = "ActivityLifecycle"

    private lateinit var tvTitle: TextView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var cbFavorite: CheckBox
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var contactId: Long = -1L
    private var currentContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        Log.d(TAG, "ContactEditActivity - onCreate()")

        // View initialization
        tvTitle = findViewById(R.id.tv_edit_title)
        etName = findViewById(R.id.et_name)
        etPhone = findViewById(R.id.et_phone)
        etEmail = findViewById(R.id.et_email)
        cbFavorite = findViewById(R.id.cb_favorite)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)

        // Read data from Intent extra
        contactId = intent.getLongExtra("CONTACT_ID", -1L)

        // Handle incoming implicit intent (ACTION_SEND with text/plain)
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrEmpty()) {
                etName.setText(sharedText)
            }
        }


        if (contactId != -1L) {
            tvTitle.text = getString(R.string.edit_contact)
            loadContactData(contactId)
        } else {
            tvTitle.text = getString(R.string.new_contact)
        }

        btnSave.setOnClickListener {
            saveContact()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadContactData(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = ContactDatabase.getInstance(this@ContactEditActivity)
            val contact = db.contactDao().getContactById(id)
            currentContact = contact

            withContext(Dispatchers.Main) {
                if (contact != null) {
                    etName.setText(contact.name)
                    etPhone.setText(contact.phone)
                    etEmail.setText(contact.email)
                    cbFavorite.isChecked = contact.isFavorite
                }
            }
        }
    }

    private fun saveContact() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val isFavorite = cbFavorite.isChecked

        if (name.isEmpty()) {
            etName.error = getString(R.string.error_name)
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = getString(R.string.error_phone)
            return
        }


        lifecycleScope.launch(Dispatchers.IO) {
            val db = ContactDatabase.getInstance(this@ContactEditActivity)

            if (contactId == -1L) {
                val newContact = Contact(
                    name = name,
                    phone = phone,
                    email = email,
                    isFavorite = isFavorite
                )
                db.contactDao().insert(newContact)
            } else {
                val updatedContact = Contact(
                    id = contactId,
                    name = name,
                    phone = phone,
                    email = email,
                    isFavorite = isFavorite
                )
                db.contactDao().update(updatedContact)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ContactEditActivity,
                    "Contact saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ContactEditActivity - onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ContactEditActivity - onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "ContactEditActivity - onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "ContactEditActivity - onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ContactEditActivity - onDestroy()")
    }
}