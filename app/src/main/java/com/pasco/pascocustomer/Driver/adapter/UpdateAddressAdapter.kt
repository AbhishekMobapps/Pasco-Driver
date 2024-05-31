package com.pasco.pascocustomer.Driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import com.pasco.pascocustomer.commonpage.login.signup.UpdateCity.UpdateCityResponse

class UpdateAddressAdapter(
    private val context: Context,
    private var updateCList: List<UpdateCityResponse.updateCityList>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<UpdateAddressAdapter.ViewHolder>(), Filterable {

    private var filteredItemList = updateCList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItemList[position]
        holder.textView.text = item.cityname // Assuming UpdateCityResponse has a property `cityName`
        holder.itemView.setOnClickListener {
            item.cityname?.let { it1 -> itemClickListener(it1) } // Passing city name to the click listener
        }
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredItemList = if (charString.isEmpty()) {
                    updateCList
                } else {
                    val filteredList = ArrayList<UpdateCityResponse.updateCityList>()
                /*    for (item in updateCList) {
                        if (item.cityName.lowercase().contains(charString.lowercase())) {
                            filteredList.add(item)
                        }
                    }*/
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredItemList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as List<UpdateCityResponse.updateCityList>
                notifyDataSetChanged()
            }
        }
    }
}
