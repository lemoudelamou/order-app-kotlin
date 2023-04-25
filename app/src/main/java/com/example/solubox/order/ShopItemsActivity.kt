@file:Suppress("DEPRECATION")

package com.example.solubox.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.w
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solubox.R
import com.example.solubox.adapters.MenuListAdapter
import com.example.solubox.adapters.MenuListAdapter.MenuListClickListener
import com.example.solubox.databinding.ActivityShopItemsBinding
import com.example.solubox.model.Menu
import com.example.solubox.model.ShopModel
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@Suppress("UNCHECKED_CAST")
class ShopItemsActivity : AppCompatActivity(), MenuListClickListener {
    private var menuList: List<Menu?>? = null
    private var menuListAdapter: MenuListAdapter? = null
    private var itemsInCartList: MutableList<Menu>? = null
    private var totalItemInCart = 0
    private var buttonCheckout: TextView? = null
    private lateinit var binding: ActivityShopItemsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopItemsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val shopModel: ShopModel? = intent.getParcelableExtra("ShopModel")
        binding.nameSh.text =  shopModel?.shop_name
        binding.addressSh.text = shopModel?.address
        menuList = shopModel!!.menus
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val pos = sh.getInt("position", -1)

        val call3 = RetrofitBuilder.createService(ApiService::class.java).getProduct((pos+1).toString())
        call3.enqueue(object : Callback<List<Menu>> {
            override fun onResponse(
                call3: Call<List<Menu>>?,
                response: Response<List<Menu>>?
            ) {
                Log.w("productDetails", "onResponse: $response")
                if (response!!.isSuccessful) {
                    if(response.body() != null) {
                                initRecyclerView(response.body()!!)
                    } else {
                        Toast.makeText(applicationContext, "REQUEST IS EMPTY!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call3: Call<List<Menu>>, t: Throwable) {
                Log.w("productDetails", "onFailure: " + t.message)
            }
        })

        buttonCheckout = findViewById(R.id.buttonCheckout)
        buttonCheckout?.setOnClickListener(View.OnClickListener {
            if (itemsInCartList != null && itemsInCartList!!.size <= 0) {
                Toast.makeText(
                    this@ShopItemsActivity,
                    "Please add some items in cart.",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            shopModel.menus = itemsInCartList
            val i = Intent(this@ShopItemsActivity, PlaceYourOrderActivity::class.java)
            i.putExtra("ShopModel", shopModel)
            startActivityForResult(i, 1000)
        })
    }

    /**
     * Intialisierung der RecyclerView
     */
    private fun initRecyclerView(menuModelList: List<Menu>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL,false)
        menuListAdapter = MenuListAdapter(menuModelList, this)
        recyclerView.adapter = menuListAdapter
    }

    /**
     * Speichern der Elemente in einer Mutable Liste.
     */
    override fun onAddToCartClick(menu: Menu?) {
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        if (itemsInCartList == null) {
            itemsInCartList = ArrayList()

        }
        itemsInCartList!!.add(menu!!)
        totalItemInCart = 0
        for (m in itemsInCartList!!) {
            this.totalItemInCart += m.totalInCart
        }

        buttonCheckout!!.text = getString(R.string.artikel_zur_kasse, totalItemInCart.toString())
        myEdit.putString("totalItems", totalItemInCart.toString())
        myEdit.putString("itemList", itemsInCartList.toString())
        myEdit.apply()
    }

    /**
     * Aktualisierung der gespeicherten Einträge in der mutablen Liste
     */
    override fun onUpdateCartClick(menu: Menu?) {
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()

        if (itemsInCartList!!.contains(menu)) {
            val index = itemsInCartList!!.indexOf(menu)
            itemsInCartList!!.removeAt(index)
            itemsInCartList!!.add(index, menu!!)


            totalItemInCart = 0
            for (m in itemsInCartList!!) {
                totalItemInCart = totalItemInCart + m.totalInCart
            }
            if(itemsInCartList!!.isEmpty()) {
                buttonCheckout!!.text =
                    getString(R.string.zur_kasse)
            } else{
                buttonCheckout!!.text = getString(R.string.artikel_zur_kasse, totalItemInCart.toString())
            }
            myEdit.putString("totalItems", totalItemInCart.toString())
            myEdit.putString("itemList", itemsInCartList.toString())
            myEdit.apply()
        }
    }

    /**
     * Entfernen der gespeicherten Elemente aus der Mutable Liste
     */
    override fun onRemoveFromCartClick(menu: Menu?) {
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()

        if (itemsInCartList!!.contains(menu)) {
            itemsInCartList!!.remove(menu)
            totalItemInCart = 0
            for (m in itemsInCartList!!) {
                totalItemInCart = totalItemInCart + m.totalInCart
            }
            if(itemsInCartList!!.isEmpty()) {
                buttonCheckout!!.text =
                    getString(R.string.zur_kasse)
            } else{
                buttonCheckout!!.text = getString(R.string.artikel_zur_kasse, totalItemInCart.toString())
            }

            myEdit.putString("totalItems", totalItemInCart.toString())
            myEdit.putString("itemList", itemsInCartList.toString())
            myEdit.apply()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            finish()
        }
    }

}