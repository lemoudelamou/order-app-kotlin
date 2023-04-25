package com.example.solubox.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.solubox.*
import com.example.solubox.menuItems.InviteActivity
import com.example.solubox.menuItems.PromoCodeActivity
import com.example.solubox.databinding.FragmentBottomNavigationDrawerBinding
import com.example.solubox.menuItems.FAQActivity
import com.example.solubox.menuItems.KontaktActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView



@Suppress("DEPRECATION")
open class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {


    private lateinit var binding: FragmentBottomNavigationDrawerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentBottomNavigationDrawerBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            when (menuItem.itemId) {
                R.id.nav1 -> {(activity as MainActivity).showProfile()
                }

                R.id.nav2 -> {
                    val i = Intent(activity, PromoCodeActivity::class.java)
                    startActivity(i)
                }

                R.id.nav3 -> {
                                val i = Intent(activity, InviteActivity::class.java)
                                startActivity(i)
                            }

                R.id.nav4 -> (activity as MainActivity).logout()

                R.id.nav5 -> {
                                val i = Intent(activity, FAQActivity::class.java)
                                startActivity(i)
                            }
                R.id.nav6 -> {
                                val i = Intent(activity, KontaktActivity::class.java)
                                startActivity(i)
                            }

            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            true
        }
        disableNavigationViewScrollbars(binding.navigationView)
    }


    private fun disableNavigationViewScrollbars(navigationView: NavigationView?) {
        val navigationMenuView = navigationView?.getChildAt(0) as NavigationMenuView
        navigationMenuView.isVerticalScrollBarEnabled = false
    }

}