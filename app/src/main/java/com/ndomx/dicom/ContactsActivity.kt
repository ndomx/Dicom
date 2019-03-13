package com.ndomx.dicom

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.content_contacts.*

class ContactsActivity : AppCompatActivity()
{
    companion object
    {
        private const val TAG = "ContactsActivity"
    }

    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = ContactsAdapter()
        contact_list.adapter = adapter
        contact_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart()
    {
        super.onStart()
        getContacts()
    }

    private fun getContacts()
    {
        var id: String
        var name: String
        var number: String

        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val contactQuery = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"

        val contactCursor = contentResolver.query(contactsUri, null, null, null, null)
        contactCursor?.apply {
            while (moveToNext())
            {
                id = getString(getColumnIndex(ContactsContract.Contacts._ID))
                name = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                val phoneCursor = contentResolver.query(phoneUri, null, contactQuery, arrayOf(id), null)
                phoneCursor?.apply {
                    moveToFirst()
                    number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    adapter.contacts.add(Contact(name, number))

                    close()
                }
            }

            close()
        }

        adapter.notifyDataSetChanged()
    }

    private fun showExpenseDialog()
    {
        Log.i(TAG, "Create dialog")
    }

    // region MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_contacts, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId) {
            R.id.action_next -> { showExpenseDialog(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

}
