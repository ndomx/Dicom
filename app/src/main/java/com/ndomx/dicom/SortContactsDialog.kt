package com.ndomx.dicom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class SortContactsDialog: DialogFragment()
{
    private lateinit var listener: DialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Order contacts by")
                .setItems(R.array.sort_options) { _, which -> listener.optionSelected(which) }
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
            throw  ClassCastException("Activity must implement SortContactsDialog.DialogListener")
        }
    }

    interface DialogListener
    {
        fun optionSelected(index: Int)
    }
}