package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.UI.ContactAdapter
import com.example.myapplication2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val allContacts = mutableListOf<String>()
    private val filteredContacts = mutableListOf<String>()
    private lateinit var adapter: ContactAdapter

    private lateinit var lvContacts: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                filterContacts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
}
