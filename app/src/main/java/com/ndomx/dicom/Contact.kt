package com.ndomx.dicom

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "contacts")
data class Contact(
    var name: String,
    @PrimaryKey
    var phone: String
)

@Dao
interface ContactsDao
{
    @Query("SELECT * FROM contacts WHERE phone = :phone")
    fun getContact(phone: String): Contact

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT COUNT() FROM CONTACTS")
    fun listLenght(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addContacts(vararg contacts: Contact)

    @Delete
    fun deleteContacts(vararg contacts: Contact)
}

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
                INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext, DicomDatabase::class.java)
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