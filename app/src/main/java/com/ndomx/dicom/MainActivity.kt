package com.ndomx.dicom

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), LifecycleOwner, SortContactsDialog.DialogListener
{
    companion object
    {
        private const val TAG = "MainActivity"
        private const val READ_CONTACTS_REQUEST_CODE = 76
        private const val SHOW_SORTING_OPTIONS = "sorting-options"

        const val CREATE_EXPENSE_CODE = 32
    }

    private lateinit var vm: DicomViewModel
    private lateinit var adapter: DicomAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "DICOM"

        checkPermissions()

        vm = ViewModelProviders.of(this).get(DicomViewModel::class.java)

        adapter = DicomAdapter(vm)
        contact_list.adapter = adapter
        contact_list.layoutManager = LinearLayoutManager(this)

        vm.contactList.observe(this, Observer { adapter.contacts = it })
        vm.totalAmount.observe(this, Observer { updateActionBar(it) })

        fab.setOnClickListener { startExpenseCreator() }
    }

    private fun updateActionBar(amount: Int?)
    {
        supportActionBar?.subtitle = when (amount) {
            0 -> "No expenses yet"
            null -> "No expenses yet"
            else -> "Total amount: $amount"
        }
    }

    private fun checkPermissions()
    {
        val permission = Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(permission), READ_CONTACTS_REQUEST_CODE)
        }
    }

    private fun startExpenseCreator()
    {
        startActivityForResult(Intent(this, ContactsActivity::class.java), CREATE_EXPENSE_CODE)
    }

    private fun showSortListMenu()
    {
        val sortContactsDialog = SortContactsDialog()
        sortContactsDialog.show(supportFragmentManager, SHOW_SORTING_OPTIONS)
    }

    override fun optionSelected(index: Int)
    {
        Log.i(TAG, "index = $index")

        adapter.contacts = when (index) {
            0 -> adapter.contacts.sortedBy { it.name }
            1 -> adapter.contacts.sortedByDescending { it.name }
            2 -> adapter.contacts.sortedBy { vm.newestExpense(it) }
            3 -> adapter.contacts.sortedByDescending { vm.oldestExpense(it) }
            4 -> adapter.contacts.sortedBy { vm.getAmount(it) }
            else -> adapter.contacts.sortedByDescending { vm.getAmount(it) }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == CREATE_EXPENSE_CODE)
        {
            val msg = when (resultCode) {
                Activity.RESULT_OK -> "Expense created successfully"
                Activity.RESULT_CANCELED -> "Expense canceled"
                else -> "Bad result"
            }

            Snackbar.make(contact_list, msg, Snackbar.LENGTH_SHORT).show()
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
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.action_sort -> showSortListMenu()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }
    // endregion
}
