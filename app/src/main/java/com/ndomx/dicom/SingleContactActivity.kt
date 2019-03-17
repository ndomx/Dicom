package com.ndomx.dicom

import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_single_contact.*
import kotlinx.android.synthetic.main.content_single_contact.*

class SingleContactActivity : AppCompatActivity(), LifecycleOwner, ExpenseDialog.DialogListener
{
    companion object
    {
        private const val TAG = "SingleContactActivity"
        private const val EXPENSE_DIALOG_TAG = "ExpenseCreatorTag"
    }

    private lateinit var vm: SingleContactViewModel
    private lateinit var adapter: SingleContactAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_contact)
        setSupportActionBar(toolbar)

        val contact = with (intent) {
            val name = getStringExtra("name")
            val phone = getStringExtra("phone")

            Contact(name, phone)
        }

        fab.setOnClickListener { startExpenseCreator() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = contact.name

        vm = ViewModelProviders.of(this, SingleViewModelFactory(contact, application)).get(SingleContactViewModel::class.java)

        adapter = SingleContactAdapter(vm)
        expense_list.adapter = adapter
        expense_list.layoutManager = LinearLayoutManager(this)

        vm.expenseList.observe(this, Observer { adapter.expenses = it })
        vm.totalAmount.observe(this, Observer { changeBarSubtitle(it) })
        vm.selectedExpenseCount.observe(this, Observer { changeBarTitle(it) })
    }

    private fun startExpenseCreator()
    {
        val expenseDialog = ExpenseDialog()
        expenseDialog.show(supportFragmentManager, EXPENSE_DIALOG_TAG)
    }

    private fun changeBarSubtitle(amount: Int?)
    {
        supportActionBar?.subtitle = when (amount) {
            0, null -> "No expenses yet"
            else -> "Total amount: $amount"
        }
    }

    private fun changeBarTitle(count: Int?)
    {
        supportActionBar?.title = when (count) {
            0, null -> vm.contact.name
            else -> "$count expenses selected"
        }

        adapter.notifyDataSetChanged()
    }

    private fun deleteExpensesDialog()
    {
        val count = vm.selectedExpenseCount.value
        val msg = when (count) {
            0, null -> { Snackbar.make(coordinator, "No expenses selected", Snackbar.LENGTH_SHORT).show(); return }
            1 -> "Delete expense?"
            else -> "Delete $count expenses"
        }

        AlertDialog.Builder(this)
            .setTitle("Delete expenses")
            .setMessage(msg)
            .setPositiveButton("Yes") { _, _ -> vm.deleteExpenses() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun chooseDate()
    {
        TODO("not implemented")
    }

    override fun saveExpense(title: String, description: String, amount: Int)
    {
        vm.saveExpense(title, description, amount)
    }

    // region MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_single, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.action_delete -> deleteExpensesDialog()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }
    // endregion

}
