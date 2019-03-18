package com.ndomx.dicom

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_contacts.*
import kotlinx.android.synthetic.main.content_contacts.*
import java.lang.Exception
import java.util.*

class ContactsActivity : AppCompatActivity(), LifecycleOwner, ExpenseDialog.DialogListener
{
    companion object
    {
        private const val TAG = "ContactsActivity"
        private const val EXPENSE_DIALOG_TAG = "ExpenseCreatorTag"
    }

    private lateinit var adapter: ContactsAdapter
    private lateinit var vm: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Choose contacts"

        vm = ViewModelProviders.of(this).get(ContactsViewModel::class.java)

        adapter = ContactsAdapter(vm)
        contact_list.adapter = adapter
        contact_list.layoutManager = LinearLayoutManager(this)

        vm.count.observe(this, Observer { updateCount(it) })

        fab.setOnClickListener { view -> showExpenseDialog(view) }
    }

    override fun onStart()
    {
        super.onStart()
        getContacts()
    }

    private fun updateCount(count: Int?)
    {
        supportActionBar?.subtitle = "Selected ${count ?: 0}"
        adapter.notifyDataSetChanged()
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
                    try
                    {
                        number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        vm.contacts.add(Contact(name, number))
                    }
                    catch (e: Exception)
                    {
                        Log.e(TAG, "Error with $id: $name")
                    }

                    close()
                }
            }

            close()
        }

        adapter.notifyDataSetChanged()
    }

    private fun showExpenseDialog(view: View)
    {
        if (vm.selectedContacts.isEmpty())
        {
            Snackbar.make(view, "Select at least one contact", Snackbar.LENGTH_SHORT).show()
        }
        else
        {
            val expenseDialog = ExpenseDialog()
            expenseDialog.show(supportFragmentManager, EXPENSE_DIALOG_TAG)
        }
    }

    override fun saveExpense(title: String, description: String, amount: Int, date: Date)
    {
        vm.saveExpense(title, description, amount, date)
        setResult(Activity.RESULT_OK)
        finish()
    }
}
