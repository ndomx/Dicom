package com.ndomx.dicom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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

    private lateinit var listener: DialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.expense_dialog, null)

            builder
                .setView(view)
                .setTitle("Create expense")
                .setPositiveButton("OK") { _, _ -> passInputData(view) }
                .setNegativeButton("Cancel") {dialog, _ -> dialog.cancel() }
                .create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        try
        {
            listener = context as DialogListener
        }
        catch (e: ClassCastException)
        {
            throw ClassCastException("Activity must implement interface")
        }
    }

    private fun passInputData(view: View)
    {
        val netAmount = view.input_amount.text.toString().toInt() * (if (view.user_paid.isChecked) 1 else -1)
        listener.saveExpense(
            title = view.input_title.text.toString(),
            description = view.input_description.text.toString(),
            amount = netAmount
        )
    }

    interface DialogListener
    {
        fun saveExpense(title: String, description: String, amount: Int)
        fun chooseDate()
    }
}