package com.ndomx.dicom

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query("DELETE FROM contacts")
    fun deleteAll()
}