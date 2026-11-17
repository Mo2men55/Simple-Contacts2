

package com.example.myapplication2.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication2.R
import com.example.myapplication2.data.Contact

/**
 * Custom ArrayAdapter for ListView.
 * Binds list of Contact objects to item_contact.xml layout views.
 */
class ContactAdapter(
    context: Context,
    private val contacts: MutableList<Contact>
) : ArrayAdapter<Contact>(context, R.layout.item_contact, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_contact, parent, false)

        val contact = getItem(position)

        val tvInitials = view.findViewById<TextView>(R.id.tv_initials)
        val tvName = view.findViewById<TextView>(R.id.tv_contact_name)
        val tvPhone = view.findViewById<TextView>(R.id.tv_contact_phone)
        val tvStar = view.findViewById<TextView>(R.id.tv_favorite_star)

        if (contact != null) {
            tvInitials.text = contact.initials
            tvName.text = contact.name
            tvPhone.text = contact.phone
            tvStar.visibility = if (contact.isFavorite) View.VISIBLE else View.GONE
        }

        return view
    }

    fun updateData(newContacts: List<Contact>) {
        clear()
        addAll(newContacts)
        notifyDataSetChanged()
    }
}
