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

    val totalAmount = db.expensesDao.getTotalAmount()
    val contactList = db.contactsDao.getAllContacts()

    val selectedContact = MutableLiveData<Contact>()

    fun getAmount(contact: Contact): Int
    {
        return db.getAmountByContact(contact)
    }

    fun oldestExpense(contact: Contact): Long
    {
        return db.getOldestExpenseDate(contact).time
    }

    fun newestExpense(contact: Contact): Long
    {
        return db.getNewestExpenseDate(contact).time
    }


}