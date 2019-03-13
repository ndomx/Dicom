package com.ndomx.dicom

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.*

class DicomViewModel(application: Application) : AndroidViewModel(application)
{
    companion object
    {
        private const val TAG = "DicomViewModel"
    }
    private val db = DicomDatabase.getInstance(getApplication())

    var selectedExpense: Expense? = null

    var selectedContact = MutableLiveData<Contact>()
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

    fun createExpense(contact: Contact, title: String, description: String, amount: Int, date: Date)
    {
        db.contactsDao.addContacts(contact)
        db.expensesDao.addExpenses(Expense(title, description, contact.phone, amount, date))
    }


}