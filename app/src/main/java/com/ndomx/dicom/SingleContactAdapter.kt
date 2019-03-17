package com.ndomx.dicom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.expense_view.view.*
import kotlin.math.abs
import kotlin.math.exp

class SingleContactAdapter(private val vm: SingleContactViewModel) : RecyclerView.Adapter<SingleContactAdapter.ViewHolder>()
{
    var expenses = listOf<Expense>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        private val holder: CardView = view.expense_holder

        val title: TextView = view.expense_title
        val description: TextView = view.expense_description
        val amount: TextView = view.expense_amount
        val date: TextView = view.expense_date
        val image: ImageView = view.expense_image

        init
        {
            holder.setOnClickListener { vm.selectExpense(expenses[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val expense = expenses[position]

        holder.amount.text = abs(expense.amount).toString()
        holder.description.text = expense.description
        holder.image.setImageResource(when {
            vm.selectedExpenses.contains(expense) -> R.drawable.ic_contact_selected_round
            expense.amount > 0 -> R.drawable.ic_positive_expense_round
            else -> R.drawable.ic_negative_expense_round
        })
        holder.title.text = expense.title
        holder.date.text = vm.formatDate(expense.date)
    }
}