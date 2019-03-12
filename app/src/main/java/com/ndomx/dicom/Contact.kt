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
    fun addContact(contact: Contact)
}

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class DicomDatabase : RoomDatabase()
{
    abstract val contactsDao: ContactsDao

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
}