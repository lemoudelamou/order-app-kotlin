package com.example.solubox.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.solubox.databinding.ActivityOrderSucceessBinding
import com.example.solubox.model.ShopModel

class OrderSucceessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSucceessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSucceessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val buttonDone = binding.buttonDone
        buttonDone.setOnClickListener { finish() }
    }
}