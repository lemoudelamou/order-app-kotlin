package com.example.solubox.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.solubox.R
import com.example.solubox.model.ShopModel
import com.bumptech.glide.Glide

class ShopListAdapter(
    var shopModelList: List<ShopModel>,
    val clickListener: ShopListClickListener
) : RecyclerView.Adapter<ShopListAdapter.MyViewHolder>(), Filterable {
    var mFilteredList: List<ShopModel> = listOf()
    var tableList: MutableList<ShopModel> = mutableListOf()


    fun updateData(shopModelList: ArrayList<ShopModel>) {
        this.shopModelList = shopModelList
        notifyDataSetChanged()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("MySharedPrefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val myEdit = sharedPreferences.edit()

        holder.shopName.text = shopModelList.get(position).shop_name
        holder.shopAddress.text = holder.itemView.context.getString(R.string.adresse_1, shopModelList[position].address.toString())
        holder.shopHours.text = holder.itemView.context.getString(R.string.oeffnungszeiten,
            shopModelList[position].hours
        )
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(
                shopModelList[position]
            )
            myEdit.putInt("position", position)
            myEdit.apply()
        }
        Glide.with(holder.thumbImage)
            .load(shopModelList[position].image)
            .into(holder.thumbImage)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val queryString = charSequence.toString()

                val filterResults = FilterResults()
                filterResults.values =
                    if (queryString.isEmpty()) {
                        tableList
                    } else {
                        tableList.filter {
                            it.shop_name!!.contains(queryString, ignoreCase = true) || it.shop_name!!.contains(charSequence)
                        }
                    }
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                mFilteredList = filterResults.values as List<ShopModel>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return shopModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var shopName: TextView
        var shopAddress: TextView
        var shopHours: TextView
        var thumbImage: ImageView

        init {
            shopName = view.findViewById(R.id.shopName)
            shopAddress = view.findViewById(R.id.shopAddress)
            shopHours = view.findViewById(R.id.shopHours)
            thumbImage = view.findViewById(R.id.thumbImage)
        }
    }

    interface ShopListClickListener {
        fun onItemClick(shopModel: ShopModel?)
    }

}