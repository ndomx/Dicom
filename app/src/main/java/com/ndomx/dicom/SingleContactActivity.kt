package com.ndomx.dicom

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
    private lateinit var menu: Menu

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
        val menuItem = menu.findItem(R.id.action_delete)
        supportActionBar?.title = when (count) {
            0, null -> {
                menuItem.isVisible = false
                menuItem.isEnabled = false
                vm.contact.name
            }
            else -> {
                menuItem.isVisible = true
                menuItem.isEnabled = true
                "$count selected"
            }
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
            .setNegativeButton("No") { dialog, _ -> dialog.cancel(); vm.clearSelectedExpenses() }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_single, menu)
        this.menu = menu
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
}
