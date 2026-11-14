package com.example.myapplication2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val allContacts = mutableListOf<String>()
    private val filteredContacts = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListView()
        setupSearchFilter()
        setupAddButton()
        updateTotalCount()
    }

    private fun setupListView() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredContacts)
        binding.lvContacts.adapter = adapter
        updateEmptyView()
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
