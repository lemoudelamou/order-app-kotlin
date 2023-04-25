package com.example.solubox.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.solubox.model.Menu
import com.bumptech.glide.Glide
import com.example.solubox.R


class MenuListAdapter(
    private var menuList: List<Menu>,
    private val clickListener: MenuListClickListener
) : RecyclerView.Adapter<MenuListAdapter.MyViewHolder>() {
    fun updateData(menuList: List<Menu>) {
        this.menuList = menuList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.menu_recycler_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.menuName.text = menuList[position].product_name
        holder.menuPrice.text = holder.itemView.context.getString(R.string.preis, menuList[position].price.toString())
        holder.menuDescription.text = menuList[position].description
        holder.addToCartButton.setOnClickListener {
            Log.w("AddToCart",  getItemId(position).toString())
            val menu = menuList[position]
            holder.cardView.setCardBackgroundColor(Color.parseColor("#318AA3"))
            menu.totalInCart = 1
            clickListener.onAddToCartClick(menu)
            holder.addMoreLayout.visibility = View.VISIBLE
            holder.addToCartButton.visibility = View.GONE
            holder.tvCount.text = holder.itemView.context.getString(R.string.leerzeichen, menu.totalInCart.toString())
        }
        holder.imageMinus.setOnClickListener {
            Log.w("AddToCartMinus",  getItemId(position).toString())

            val menu = menuList[position]
            var total = menu.totalInCart
            total--
            if (total > 0) {
                menu.totalInCart = total
                clickListener.onUpdateCartClick(menu)
                holder.tvCount.text = holder.itemView.context.getString(R.string.leerzeichen, total.toString())
            } else {
                holder.addMoreLayout.visibility = View.GONE
                holder.addToCartButton.visibility = View.VISIBLE
                menu.totalInCart = total
                holder.cardView.setCardBackgroundColor(Color.parseColor("#0D2A32"))
                clickListener.onRemoveFromCartClick(menu)
            }
        }
        holder.imageAddOne.setOnClickListener {
            Log.w("AddToCartPlus",  getItemId(position).toString())

            val menu = menuList[position]
            var total = menu.totalInCart
            total++
            if (total <= 14) {
                menu.totalInCart = total
                clickListener.onUpdateCartClick(menu)
                holder.tvCount.text = holder.itemView.context.getString(R.string.leerzeichen, total.toString())
            }
        }
        Glide.with(holder.thumbImage)
            .load(menuList[position].image)
            .into(holder.thumbImage)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardView: CardView
        var menuName: TextView
        var menuPrice: TextView
        var menuDescription: TextView
        var addToCartButton: TextView
        var thumbImage: ImageView
        var imageMinus: ImageView
        var imageAddOne: ImageView
        var tvCount: TextView
        var addMoreLayout: LinearLayout

        init {
            cardView = view.findViewById(R.id.cardView)
            menuName = view.findViewById(R.id.menuName)
            menuPrice = view.findViewById(R.id.menuPrice)
            menuDescription = view.findViewById(R.id.menuDescription)
            addToCartButton = view.findViewById(R.id.addToCartButton)
            thumbImage = view.findViewById(R.id.thumbImage)
            imageMinus = view.findViewById(R.id.imageMinus)
            imageAddOne = view.findViewById(R.id.imageAddOne)
            tvCount = view.findViewById(R.id.tvCount)
            addMoreLayout = view.findViewById(R.id.addMoreLayout)
        }
    }

    interface MenuListClickListener {
        fun onAddToCartClick(menu: Menu?)
        fun onUpdateCartClick(menu: Menu?)
        fun onRemoveFromCartClick(menu: Menu?)
    }
}