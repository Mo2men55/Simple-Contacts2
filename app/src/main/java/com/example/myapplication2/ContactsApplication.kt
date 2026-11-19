package com.example.myapplication2

import android.app.Application
import com.example.myapplication2.data.ContactDatabase
import com.example.myapplication2.data.ContactRepository

class ContactsApplication : Application() {

    val repository: ContactRepository by lazy {
        ContactRepository(ContactDatabase.getInstance(this).contactDao())
    }
}
