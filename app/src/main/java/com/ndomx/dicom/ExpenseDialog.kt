package com.ndomx.dicom

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
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
        private const val DATE_PICK_TAG = "DatePickFragment_TAG"
    }

    private lateinit var listener: DialogListener
    private val dateListener = DatePickListener()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = LayoutInflater.from(it).inflate(R.layout.expense_dialog, null)

            view.input_date.setOnClickListener { openDatePickerDialog() }

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

    private fun openDatePickerDialog()
    {
        context?.apply {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, dateListener, year, month, day).show()
        }
    }

    private fun passInputData(view: View)
    {
        val netAmount = view.input_amount.text.toString().toInt() * (if (view.user_paid.isChecked) 1 else -1)
        listener.saveExpense(
            title = view.input_title.text.toString(),
            description = view.input_description.text.toString(),
            amount = netAmount,
            date = dateListener.date
        )
    }

    interface DialogListener
    {
        fun saveExpense(title: String, description: String, amount: Int, date: Date)
    }
}