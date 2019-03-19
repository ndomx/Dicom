package com.ndomx.dicom

import android.app.Application
import androidx.lifecycle.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SingleContactViewModel(val contact: Contact, val app: Application) : ViewModel()
{
    companion object
    {
        private const val TAG = "SingleContactViewModel"
    }

    private val db = DicomDatabase.getInstance(app)

    val totalAmount = MediatorLiveData<Int>()
    val expenseList: LiveData<List<Expense>> = db.contactExpenses(contact)

    val selectedExpenseCount = MutableLiveData<Int>()
    val selectedExpenses = mutableListOf<Expense>()

    private val selectedExpensesAmount: Int get() = selectedExpenses.sumBy { it.amount }

    init
    {
        totalAmount.addSource(db.contactAmount(contact)) { amount -> totalAmount.value = amount }
        totalAmount.addSource(selectedExpenseCount) { count ->
            totalAmount.value = if (count > 0) selectedExpensesAmount else db.getAmountByContact(contact)
        }
    }

    fun saveExpense(title: String, description: String, amount: Int, date: Date)
    {
        db.contactsDao.addContacts(contact)
        db.expensesDao.addExpenses(Expense(title, description, contact.phone, amount, date))
    }

    fun selectExpense(expense: Expense)
    {
        when (selectedExpenses.contains(expense))
        {
            true -> selectedExpenses.remove(expense)
            false -> selectedExpenses.add(expense)
        }

        selectedExpenseCount.value = selectedExpenses.size
    }

    fun clearSelectedExpenses()
    {
        selectedExpenses.clear()
        selectedExpenseCount.value = 0
    }

    fun deleteExpenses()
    {
        db.deleteExpenses(selectedExpenses)
        clearSelectedExpenses()
    }

    fun formatDate(date: Date): String
    {
        val df = SimpleDateFormat("dd-MMM-yy")
        val now = Calendar.getInstance().timeInMillis
        return when {
            now < date.time -> "In the future..."
            now - date.time < 60_000 -> "Just now"
            now - date.time < 3_600_000 -> "${(now - date.time)/60_000} minutes ago"
            now - date.time < 21_600_000 -> "${(now - date.time)/3_600_000} hours ago"
            else -> df.format(date)
        }
    }
}