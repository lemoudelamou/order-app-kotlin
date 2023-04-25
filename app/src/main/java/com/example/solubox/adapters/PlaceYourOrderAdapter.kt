package com.example.solubox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.solubox.R
import com.example.solubox.model.Menu
import com.bumptech.glide.Glide

class PlaceYourOrderAdapter(private var menuList: List<Menu?>?) :
    RecyclerView.Adapter<PlaceYourOrderAdapter.MyViewHolder>() {
    fun updateData(menuList: List<Menu>) {
        this.menuList = menuList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_order_recycler_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.menuName.text = menuList?.get(position)!!.product_name
        holder.menuPrice.text = holder.itemView.context.getString(R.string.preis, (menuList!![position]!!.price * menuList!![position]!!.totalInCart).toString())
        holder.menuQty.text = holder.itemView.context.getString(R.string.quantitaet, menuList!![position]!!.totalInCart.toString())
        Glide.with(holder.thumbImage)
            .load(menuList!![position]!!.image)
            .into(holder.thumbImage)
    }

    override fun getItemCount(): Int {
        return menuList!!.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var menuName: TextView
        var menuPrice: TextView
        var menuQty: TextView
        var thumbImage: ImageView

        init {
            menuName = view.findViewById(R.id.menuName)
            menuPrice = view.findViewById(R.id.menuPrice)
            menuQty = view.findViewById(R.id.menuQty)
            thumbImage = view.findViewById(R.id.thumbImage)
        }
    }
}