package com.ndomx.dicom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_view.view.*

class ContactsAdapter(private val vm: ContactsViewModel): RecyclerView.Adapter<ContactsAdapter.ViewHolder>()
{
    var contacts: List<Contact> = listOf()
    set(value) {
        notifyDataSetChanged()
        field = value
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val holder: CardView = view.contact_holder
        val name: TextView = view.contact_name
        val amount: TextView = view.contact_amount
        val image: ImageView = view.contact_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val contact = contacts[position]
        holder.name.text = contact.name
        holder.amount.text = vm.getAmount(contact).toString()
    }
}