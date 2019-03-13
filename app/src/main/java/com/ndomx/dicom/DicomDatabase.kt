package com.ndomx.dicom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
                /*
                INSTANCE = Room.databaseBuilder(context.applicationContext, DicomDatabase::class.java, "sb")
                    .allowMainThreadQueries()
                    .build()
                    */
                INSTANCE = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    DicomDatabase::class.java
                )
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE!!
        }
    }

    fun getExpensesByContact(contact: Contact): List<Expense>
    {
        return expensesDao.getAllExpenses(contact.phone)
    }

    fun getAmountByContact(contact: Contact): Int
    {
        return expensesDao.getAmount(contact.phone)
    }

    fun deleteContact(contact: Contact)
    {
        val expenses = getExpensesByContact(contact)
        expensesDao.deleteExpenses(*expenses.toTypedArray())
        contactsDao.deleteContacts(contact)
    }
}