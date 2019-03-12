package com.ndomx.dicom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import java.lang.IllegalStateException

class ExpenseDialog : DialogFragment()
{
    companion object
    {
        private const val TAG = "ExpenseDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val vm = ViewModelProviders.of(this).get(ContactsViewModel::class.java)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.expense_dialog, null)

            builder
                .setView(view)
                .setTitle("Create expense")
                .setPositiveButton("OK") { _, _ -> pickContacts() }
                .setNegativeButton("Cancel") {dialog, _ -> dialog.cancel() }
                .create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun pickContacts()
    {
        val contactIntent = Intent(Intent.ACTION_PICK)
        contactIntent.setDataAndType(ContactsContract.Contacts.CONTENT_URI, ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
        startActivityForResult(contactIntent, MainActivity.PICK_CONTACT_CODE)
    }
}