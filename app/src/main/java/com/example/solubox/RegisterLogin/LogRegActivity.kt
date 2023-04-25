package com.example.solubox.RegisterLogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.solubox.databinding.ActivityLogRegBinding


class LogRegActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogRegBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityLogRegBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btnToLogPage.setOnClickListener {
            startActivity(Intent(this@LogRegActivity, LoginActivity::class.java))
        }

        binding.btnToRegPage.setOnClickListener {
            val i = Intent(this@LogRegActivity, RegisterActivity::class.java)
            startActivity(i)
        }
    }

}