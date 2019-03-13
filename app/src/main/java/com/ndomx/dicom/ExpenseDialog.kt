package com.ndomx.dicom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.expense_dialog.view.*
import java.lang.IllegalStateException
import java.util.*

class ExpenseDialog : DialogFragment()
{
    companion object
    {
        private const val TAG = "ExpenseDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val name = arguments?.getString("name") ?: ""
        val phone = arguments?.getString("phone") ?: ""

        val contact = Contact(name, phone)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.expense_dialog, null)

            builder
                .setView(view)
                .setTitle("Create expense")
                .setPositiveButton("OK") { _, _ -> createExpense(view, contact) }
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

    /*
    private fun createExpense(view: View)
    {
        val netAmount = view.input_amount.text.toString().toInt() * (if (view.user_paid.isChecked) 1 else -1)

        val vm = ViewModelProviders.of(this).get(DicomViewModel::class.java)
        /*
        vm.selectedExpense = Expense(
            title = view.input_title.text.toString(),
            description = view.input_description.text.toString(),
            amount = netAmount,
            date = Calendar.getInstance().time,
            contactId = ""
        )

        pickContacts()
        */
        Log.i(TAG, "selectedContact is null: ${vm.selectedContact == null}")
        vm.createExpense(
            title = view.input_title.text.toString(),
            description = view.input_description.text.toString(),
            amount = netAmount,
            date = Calendar.getInstance().time
        )
    }
    */

    private fun createExpense(view: View, contact: Contact)
    {
        if (contact.name.isEmpty() || contact.phone.isEmpty())
        {
            Log.e(TAG, "empty contact")
        }

        val netAmount = view.input_amount.text.toString().toInt() * (if (view.user_paid.isChecked) 1 else -1)
        val vm = ViewModelProviders.of(this).get(DicomViewModel::class.java)

        vm.createExpense(
            title = view.input_title.text.toString(),
            description = view.input_description.text.toString(),
            amount = netAmount,
            date = Calendar.getInstance().time,
            contact = contact
        )
    }
}