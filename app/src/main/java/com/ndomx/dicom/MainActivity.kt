package com.ndomx.dicom

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_SUBJECT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileOutputStream

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

        vm.shortClickContact.observe(this, Observer { startContactActivity(it) })
        vm.longClickContact.observe(this, Observer { deleteContactDialog(it) })

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

    private fun startContactActivity(contact: Contact?)
    {
        if (contact == null) return

        val intent = Intent(this, SingleContactActivity::class.java)
        intent.putExtra("name", contact.name)
        intent.putExtra("phone", contact.phone)

        startActivity(intent)
    }

    private fun deleteContactDialog(contact: Contact?)
    {
        if (contact == null) return

        AlertDialog.Builder(this)
            .setTitle("Remove contact")
            .setMessage("Remove ${contact.name} and all of his expenses?")
            .setPositiveButton("Yes") { _, _ -> vm.deleteContact(contact) }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun clearDbDialog()
    {
        AlertDialog.Builder(this)
            .setTitle("Clear all expenses")
            .setMessage("Mark all expenses as paid and remove them?")
            .setPositiveButton("Yes") { _, _ -> vm.clearDb() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun checkPermissions()
    {
        val readContactPermission = Manifest.permission.READ_CONTACTS
        val writeFilePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, readContactPermission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(readContactPermission), READ_CONTACTS_REQUEST_CODE)
        }
        if (ContextCompat.checkSelfPermission(this, writeFilePermission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(writeFilePermission), 78)
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

    private fun saveScreenshot()
    {
        fab.isVisible = false
        val b = Screenshot.takeScreenshot(fab.rootView)
        fab.isVisible = true

        val f = Screenshot.saveFile(b)
        shareScreenshot(f)
    }

    private fun shareScreenshot(file: File)
    {
        val uri = Uri.fromFile(file)
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"

            putExtra(Intent.EXTRA_SUBJECT, "")
            putExtra(Intent.EXTRA_TEXT, "")
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        try
        {
            startActivity(Intent.createChooser(intent, "Share expenses"))
        }
        catch (e: ActivityNotFoundException)
        {
            Snackbar.make(fab, "No app found for sharing", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun optionSelected(index: Int)
    {
        Log.i(TAG, "index = $index")

        adapter.contacts = when (index) {
            0 -> adapter.contacts.sortedBy { it.name }
            1 -> adapter.contacts.sortedByDescending { it.name }
            2 -> adapter.contacts.sortedByDescending { vm.newestExpense(it) }
            3 -> adapter.contacts.sortedBy { vm.oldestExpense(it) }
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
            R.id.action_clear_db -> clearDbDialog()
            R.id.action_share -> saveScreenshot()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }
}
