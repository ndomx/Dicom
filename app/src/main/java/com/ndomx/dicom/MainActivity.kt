package com.ndomx.dicom

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), LifecycleOwner
{
    companion object
    {
        private const val TAG = "MainActivity"
        private const val EXPENSE_DIALOG_TAG = "ExpenseCreatorTag"
        private const val READ_CONTACTS_REQUEST_CODE = 76

        const val PICK_CONTACT_CODE = 32
    }

    private var shouldOpenFragment = false

    private lateinit var vm: DicomViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        checkPermissions()

        vm = ViewModelProviders.of(this).get(DicomViewModel::class.java)

        val adapter = DicomAdapter(vm)
        contact_list.adapter = adapter
        contact_list.layoutManager = LinearLayoutManager(this)

        vm.contactList.observe(this, Observer { adapter.contacts = it })
        vm.totalAmount.observe(this, Observer { Log.i(TAG, "total amount = $it") })
        vm.selectedContact.observe(this, Observer { it?.apply { createExpense(this) } })

        fab.setOnClickListener { /*pickContacts()*/ displayContacts() }
    }

    private fun checkPermissions()
    {
        val permission = Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(permission), READ_CONTACTS_REQUEST_CODE)
        }
    }

    private fun pickContacts()
    {
        val contactIntent = Intent(Intent.ACTION_PICK)
        val data = ContactsContract.Contacts.CONTENT_URI
        val type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE

        contactIntent.setDataAndType(data, type)
        startActivityForResult(contactIntent, MainActivity.PICK_CONTACT_CODE)
    }

    private fun createExpense()
    {
        val expenseDialog = ExpenseDialog()
        expenseDialog.show(supportFragmentManager, EXPENSE_DIALOG_TAG)
    }

    private fun createExpense(contact: Contact)
    {
        val args = Bundle()
        args.putString("name", contact.name)
        args.putString("phone", contact.phone)

        val expenseDialog = ExpenseDialog()
        expenseDialog.arguments = args

        expenseDialog.show(supportFragmentManager, EXPENSE_DIALOG_TAG)
    }

    private fun addContact(data: Intent?)
    {
        if (data == null)
        {
            Log.e(TAG, "Data is null")
            return
        }

        val uri = data.data
        if (uri == null)
        {
            Log.e(TAG, "Data is empty")
            return
        }

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.apply {
            moveToFirst()

            val name = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            //vm.createExpense(name, number)

            close()

            Log.i(TAG, "setting selectedContact")
            vm.selectedContact.value = Contact(name, number)
            //createExpense()
        }
    }

    private fun displayContacts()
    {
        startActivity(Intent(this, ContactsActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (resultCode != Activity.RESULT_OK) Log.e(TAG, "Bad result code: $resultCode")
        else
        {
            addContact(data)
        }
    }

    // region MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion
}
