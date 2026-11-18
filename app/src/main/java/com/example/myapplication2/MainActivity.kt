package com.example.myapplication2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.UI.ContactAdapter
import com.example.myapplication2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "ActivityLifecycle"
    private val PREFS_NAME = "ContactsAppPrefs"
    private val KEY_STUDENT_INFO = "student_info"
    private val KEY_LAST_SEARCH = "last_search"

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val allContacts = mutableListOf<String>()
    private val filteredContacts = mutableListOf<String>()
    private lateinit var adapter: ContactAdapter

    private lateinit var lvContacts: ListView
    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MainActivity - onCreate()")

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Display Student Header Info from SharedPreferences
        val savedStudentInfo = sharedPreferences.getString(
            KEY_STUDENT_INFO,
            getString(R.string.student_info_header)
        )
        binding.tvStudentInfo.text = savedStudentInfo

        // Restore Search State across rotation or from SharedPreferences
        if (savedInstanceState != null) {
            currentSearchQuery = savedInstanceState.getString(KEY_LAST_SEARCH, "")
            binding.etSearch.setText(currentSearchQuery)
        } else {
            currentSearchQuery = sharedPreferences.getString(KEY_LAST_SEARCH, "") ?: ""
            if (currentSearchQuery.isNotEmpty()) {
                binding.etSearch.setText(currentSearchQuery)
            }
        }

        lvContacts = findViewById(R.id.lv_contacts)

        // Initialize ListView with ArrayAdapter
        adapter = ContactAdapter(this, mutableListOf())
        lvContacts.adapter = adapter

        // Open ContactDetailActivity on list item click
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            val contact = adapter.getItem(position)
            if (contact != null) {
                val intent = Intent(this, ContactDetailActivity::class.java)
                intent.putExtra("CONTACT_ID", contact.id)
                startActivity(intent)
            }
        }

        // Open ContactEditActivity on Add button click
        btnAddContact.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            startActivity(intent)
        }

        setupSearchFilter()
        setupAddButton()
        updateTotalCount()
    }

    private fun setupSearchFilter() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString()?.trim() ?: ""
                saveLastSearchToPrefs(currentSearchQuery)
                filterContacts(currentSearchQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun saveLastSearchToPrefs(query: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_SEARCH, query)
            .apply()
    }

    private fun filterContacts(query: String) {
        filteredContacts.clear()
        if (query.trim().isEmpty()) {
            filteredContacts.addAll(allContacts)
        } else {
            val lowerQuery = query.trim().lowercase()
            allContacts.filterTo(filteredContacts) { contact ->
                contact.lowercase().contains(lowerQuery)
            }
        }
        adapter.notifyDataSetChanged()
        updateEmptyView()
        updateTotalCount()
    }

    private fun setupAddButton() {
        binding.btnAddContact.setOnClickListener {
            // Action handled in subsequent features
        }
    }

    private fun updateEmptyView() {
        if (filteredContacts.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = if (binding.etSearch.text.isNullOrEmpty()) {
                getString(R.string.no_contacts)
            } else {
                getString(R.string.no_results)
            }
        } else {
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun updateTotalCount() {
        binding.tvTotalCount.text = getString(R.string.total_contacts, filteredContacts.size)
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity - onResume()")
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
}
