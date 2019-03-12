package com.ndomx.dicom

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ContactsViewModel(application: Application) : AndroidViewModel(application)
{
    companion object
    {
        private const val TAG = "ContactsViewModel"
    }
    private val db = DicomDatabase.getInstance(getApplication())

    var selectedExpense: Expense? = null

    val totalAmount = db.expensesDao.getTotalAmount()
    val contactList = db.contactsDao.getAllContacts()

    fun getAmount(contact: Contact): Int
    {
        return db.getAmountByContact(contact)
    }

    fun createExpense(name: String, phone: String)
    {
        when (selectedExpense)
        {
            null -> {
                Log.e(TAG, "selectedExpense is null")
                return
            }
            else -> Log.i(TAG, "Creating valid expense")
        }

        db.contactsDao.addContacts(Contact(name, phone))

        selectedExpense?.apply {
            contactId = phone
            db.expensesDao.addExpenses(this)
        }
    }


}