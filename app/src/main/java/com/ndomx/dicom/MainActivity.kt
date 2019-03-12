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
import androidx.lifecycle.ViewModelProviders

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    companion object
    {
        private const val TAG = "MainActivity"
        private const val EXPENSE_DIALOG_TAG = "ExpenseCreatorTag"
        private const val READ_CONTACTS_REQUEST_CODE = 76

        const val PICK_CONTACT_CODE = 32
    }

    private lateinit var vm: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        checkPermissions()

        vm = ViewModelProviders.of(this).get(ContactsViewModel::class.java)

        fab.setOnClickListener { createExpense() }
    }

    private fun checkPermissions()
    {
        val permission = Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(permission), READ_CONTACTS_REQUEST_CODE)
        }
    }

    private fun createExpense()
    {
        val expenseDialog = ExpenseDialog()
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

            vm.addContact(name, number)

            close()
        }
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
