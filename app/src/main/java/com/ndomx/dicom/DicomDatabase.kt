package com.ndomx.dicom

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.util.*

@Database(entities = [Contact::class, Expense::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DicomDatabase : RoomDatabase()
{
    abstract val contactsDao: ContactsDao
    abstract val expensesDao: ExpensesDao

    companion object
    {
        private var INSTANCE: DicomDatabase? = null
        fun getInstance(context: Context): DicomDatabase
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room.databaseBuilder(context.applicationContext, DicomDatabase::class.java, DatabaseName)
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE!!
        }

        private val DatabaseName = "dicom.db"
    }

    private fun expensesByContact(contact: Contact): List<Expense>
    {
        return expensesDao.getAllExpenses(contact.phone)
    }

    fun contactExpenses(contact: Contact): LiveData<List<Expense>>
    {
        return expensesDao.getExpenses(contact.phone)
    }

    fun contactAmount(contact: Contact): LiveData<Int>
    {
        return expensesDao.getTotalAmount(contact.phone)
    }

    fun getAmountByContact(contact: Contact): Int
    {
        return expensesDao.getAmount(contact.phone)
    }

    fun getOldestExpenseDate(contact: Contact): Date
    {
        return expensesDao.getOldestExpenseDate(contact.phone)
    }

    fun getNewestExpenseDate(contact: Contact): Date
    {
        return expensesDao.getNewestExpenseDate(contact.phone)
    }

    fun addContacts(contacts: List<Contact>)
    {
        contactsDao.addContacts(*contacts.toTypedArray())
    }

    fun addExpenses(expenses: List<Expense>)
    {
        expensesDao.addExpenses(*expenses.toTypedArray())
    }

    fun deleteContact(contact: Contact)
    {
        val expenses = expensesByContact(contact)
        expensesDao.deleteExpenses(*expenses.toTypedArray())
        contactsDao.deleteContacts(contact)
    }

    fun deleteExpenses(expenses: List<Expense>)
    {
        expensesDao.deleteExpenses(*expenses.toTypedArray())
    }

    fun deleteAll()
    {
        contactsDao.deleteAll()
        expensesDao.deleteAll()
    }
}