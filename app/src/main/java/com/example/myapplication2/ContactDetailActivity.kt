package com.example.myapplication // أو com.example.myapplication2 حسب الباكيج عندك

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.Contact
import com.example.myapplication.data.ContactDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Contact Detail Activity.
 * Displays contact details and handles navigation to edit screen (Member 5).
 */
class ContactDetailActivity : AppCompatActivity() {

    private val TAG = "ActivityLifecycle"

    private lateinit var tvInitials: TextView
    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvFavorite: TextView

    private lateinit var btnCall: Button
    private lateinit var btnSms: Button
    private lateinit var btnEmail: Button
    private lateinit var btnShare: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    private var contactId: Long = -1L
    private var contact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Log.d(TAG, "ContactDetailActivity - onCreate()")

        // View initialization
        tvInitials = findViewById(R.id.tv_detail_initials)
        tvName = findViewById(R.id.tv_detail_name)
        tvPhone = findViewById(R.id.tv_detail_phone)
        tvEmail = findViewById(R.id.tv_detail_email)
        tvFavorite = findViewById(R.id.tv_detail_favorite)

        btnCall = findViewById(R.id.btn_call)
        btnSms = findViewById(R.id.btn_sms)
        btnEmail = findViewById(R.id.btn_email)
        btnShare = findViewById(R.id.btn_share)
        btnEdit = findViewById(R.id.btn_edit)
        btnDelete = findViewById(R.id.btn_delete)

        // 🟢 [Member 5 - Intent Extra]: استقبال ID جهة الاتصال
        contactId = intent.getLongExtra("CONTACT_ID", -1L)

        // 🟢 [Member 5 - Explicit Intent]: ربط زر التعديل بالانتقال لشاشة ContactEditActivity
        btnEdit.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            intent.putExtra("CONTACT_ID", contactId)
            startActivity(intent)
        }

        setupImplicitListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ContactDetailActivity - onResume()")
        if (contactId != -1L) {
            loadContact(contactId)
        }
    }

    private fun loadContact(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = ContactDatabase.getInstance(this@ContactDetailActivity)
            val loadedContact = db.contactDao().getContactById(id)
            contact = loadedContact

            withContext(Dispatchers.Main) {
                if (loadedContact != null) {
                    displayContact(loadedContact)
                } else {
                    Toast.makeText(this@ContactDetailActivity, "Contact not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun displayContact(contact: Contact) {
        tvInitials.text = contact.initials
        tvName.text = contact.name
        tvPhone.text = contact.phone
        tvEmail.text = if (contact.email.isNotEmpty()) contact.email else "None"
        tvFavorite.visibility = if (contact.isFavorite) View.VISIBLE else View.GONE
    }
    private fun setupImplicitListeners() {
        // abdallah

        btnDelete.setOnClickListener {
            contact?.let { c ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = ContactDatabase.getInstance(this@ContactDetailActivity)
                    db.contactDao().delete(c)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ContactDetailActivity, "Contact deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }






    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ContactDetailActivity - onStart()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "ContactDetailActivity - onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "ContactDetailActivity - onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ContactDetailActivity - onDestroy()")
    }
}
