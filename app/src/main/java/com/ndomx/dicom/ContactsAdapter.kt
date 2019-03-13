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
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val holder: CardView = view.contact_holder
        val name: TextView = view.contact_name
        val phone: TextView = view.contact_second_text
        val image: ImageView = view.contact_image

        init
        {
            holder.setOnClickListener { vm.contactOnClickListener(adapterPosition) }
        }
    }

    override fun getItemCount(): Int
    {
        return vm.contacts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val contact = vm.contacts[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        holder.image.setImageResource(when (vm.selectedContacts.contains(contact)) {
            true -> R.drawable.ic_contact_selected_round
            false -> R.drawable.ic_contact_round
        })
    }
}