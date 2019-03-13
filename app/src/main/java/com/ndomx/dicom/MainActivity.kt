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

class MainActivity : AppCompatActivity(), LifecycleOwner
{
    companion object
    {
        private const val TAG = "MainActivity"
        private const val READ_CONTACTS_REQUEST_CODE = 76

        const val CREATE_EXPENSE_CODE = 32
    }

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

        fab.setOnClickListener { startExpenseCreator() }
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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion
}
