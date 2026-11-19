package com.example.myapplication2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication2.data.Contact
import com.example.myapplication2.data.ContactDatabase
import com.example.myapplication2.ui.ContactAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val TAG = "ActivityLifecycle"
    private val PREFS_NAME = "ContactsAppPrefs"
    private val KEY_STUDENT_INFO = "student_info"
    private val KEY_LAST_SEARCH = "last_search"

    private lateinit var tvStudentInfo: TextView
    private lateinit var etSearch: EditText
    private lateinit var tvTotalCount: TextView
    private lateinit var lvContacts: ListView
    private lateinit var tvEmpty: TextView
    private lateinit var btnAddContact: Button

    private lateinit var adapter: ContactAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity - onCreate()")

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        tvStudentInfo = findViewById(R.id.tv_student_info)
        etSearch = findViewById(R.id.et_search)
        tvTotalCount = findViewById(R.id.tv_total_count)
        lvContacts = findViewById(R.id.lv_contacts)
        tvEmpty = findViewById(R.id.tv_empty)
        btnAddContact = findViewById(R.id.btn_add_contact)

        val savedStudentInfo = sharedPreferences.getString(
            KEY_STUDENT_INFO,
            getString(R.string.student_info_header)
        )
        tvStudentInfo.text = savedStudentInfo

        if (savedInstanceState != null) {
            currentSearchQuery = savedInstanceState.getString(KEY_LAST_SEARCH, "")
            etSearch.setText(currentSearchQuery)
        } else {
            currentSearchQuery = sharedPreferences.getString(KEY_LAST_SEARCH, "") ?: ""
            if (currentSearchQuery.isNotEmpty()) {
                etSearch.setText(currentSearchQuery)
            }
        }

        adapter = ContactAdapter(this, mutableListOf())
        lvContacts.adapter = adapter

        lvContacts.setOnItemClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position)
            if (contact != null) {
                val intent = Intent(this, ContactDetailActivity::class.java)
                intent.putExtra("CONTACT_ID", contact.id)
                startActivity(intent)
            }
        }

        btnAddContact.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            startActivity(intent)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                saveLastSearchToPrefs(currentSearchQuery)
                loadContactsFromRoom(currentSearchQuery)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        seedSampleDataIfEmpty()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity - onResume()")
        loadContactsFromRoom(currentSearchQuery)
    }

    private fun loadContactsFromRoom(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = ContactDatabase.getInstance(this@MainActivity)
            val contactsList = if (query.isEmpty()) {
                db.contactDao().getAllContacts()
            } else {
                db.contactDao().searchContacts(query)
            }

            withContext(Dispatchers.Main) {
                adapter.updateData(contactsList)
                tvTotalCount.text = getString(R.string.total_contacts, contactsList.size)

                if (contactsList.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = if (query.isEmpty()) {
                        getString(R.string.no_contacts)
                    } else {
                        getString(R.string.no_results)
                    }
                } else {
                    tvEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun saveLastSearchToPrefs(query: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_SEARCH, query)
            .apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "MainActivity - onSaveInstanceState()")
        outState.putString(KEY_LAST_SEARCH, currentSearchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "MainActivity - onRestoreInstanceState()")
        currentSearchQuery = savedInstanceState.getString(KEY_LAST_SEARCH, "")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity - onStart()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity - onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "MainActivity - onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity - onDestroy()")
    }

    private fun seedSampleDataIfEmpty() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = ContactDatabase.getInstance(this@MainActivity)
            val existing = db.contactDao().getAllContacts()
            if (existing.isEmpty()) {
                db.contactDao().insert(
                    Contact(name = "Alice Smith", phone = "555-0192", email = "alice@example.com", isFavorite = true)
                )
                db.contactDao().insert(
                    Contact(name = "Bob Johnson", phone = "555-0143", email = "bob@example.com", isFavorite = false)
                )
                db.contactDao().insert(
                    Contact(name = "Charlie Brown", phone = "555-0188", email = "charlie@example.com", isFavorite = true)
                )
                withContext(Dispatchers.Main) {
                    loadContactsFromRoom(currentSearchQuery)
                }
            }
        }
    }
}
