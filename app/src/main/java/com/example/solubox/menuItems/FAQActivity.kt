package com.example.solubox.menuItems

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.solubox.MainActivity
import com.example.solubox.R
import com.example.solubox.databinding.ActivityFaqBinding

class FAQActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.getBack.setOnClickListener {
            val i = Intent(this@FAQActivity, MainActivity::class.java)
            startActivity(i)
        }


        binding.firstBtn.setOnClickListener {
            if (binding.firstTxt.visibility == GONE){
                binding.firstBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_arrow_down, 0)
                binding.firstTxt.visibility = VISIBLE
            } else
            {
                binding.firstBtn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_navigate_next, 0)
                binding.firstTxt.visibility = GONE
            }

        }

    }
}