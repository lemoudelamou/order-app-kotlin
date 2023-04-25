package com.example.solubox.RegisterLogin

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.solubox.Utils.CustomAlert
import com.example.solubox.databinding.ActivityChangePasswordBinding



class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)




        binding.btnSubmit.setOnClickListener {

            if (TextUtils.isEmpty(binding.tilEmail.text.toString())) {
                 binding.tilEmail.error
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Fehlende Angabe, Bitte das E-Mail Feld Ausfüllen",
                    Toast.LENGTH_LONG
                ).show()
            } else if(binding.tilEmail.text.toString().contains(" ")){
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "E-Mail Adresse nicht erkannt",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                val alert = CustomAlert()
                alert.showDialog(
                    this,
                    "Bitte wende dich an den Solubox Support wenn du dein Passwort vergessen hast.",
                    "Passwort vergessen?"
                )
            }
        }

    }




}