package com.ndomx.dicom

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ContactsViewModel(application: Application) : AndroidViewModel(application)
{
    companion object
    {
        private const val TAG = "ContactsViewModel"
    }

    fun contactList(): LiveData<List<Contact>>
    {
        val db = DicomDatabase.getInstance(getApplication())
        return db.contactsDao.getAllContacts()
    }

    fun addContact(name: String, phone: String)
    {
        val db = DicomDatabase.getInstance(getApplication())
        db.contactsDao.addContact(Contact(name, phone))

        Log.i(TAG, db.contactsDao.listLenght().toString())
    }
}