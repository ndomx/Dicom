package com.ndomx.dicom

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.*

class ContactsViewModel(application: Application) : AndroidViewModel(application)
{
    private val db = DicomDatabase.getInstance(application)

    val contacts = mutableListOf<Contact>()
    val selectedContacts = mutableListOf<Contact>()

    val count = MutableLiveData<Int>()

    fun contactOnClickListener(position: Int)
    {
        val contact = contacts[position]
        when (selectedContacts.contains(contact))
        {
            true -> selectedContacts.remove(contact)
            false -> selectedContacts.add(contact)
        }

        count.value = selectedContacts.size
    }

    fun saveExpense(title: String, description: String, amount: Int)
    {
        db.addContacts(selectedContacts)
        val expenses = mutableListOf<Expense>()
        val date = Calendar.getInstance().time

        for (contact: Contact in selectedContacts)
        {
            expenses.add(Expense(title, description, contact.phone, amount, date))
        }

        db.addExpenses(expenses)
    }
}