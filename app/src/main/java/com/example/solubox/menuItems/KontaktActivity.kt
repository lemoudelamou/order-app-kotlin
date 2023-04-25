package com.example.solubox.menuItems

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.solubox.databinding.ActivityKontaktBinding


class KontaktActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKontaktBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKontaktBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.sms.setOnClickListener { sendSMS() }

        binding.mail.setOnClickListener { sendEmail() }

        binding.call.setOnClickListener { callSupport() }

        binding.whatsapp.setOnClickListener { sendwhatsApp() }
    }


    fun sendSMS()
    {
        val uri = Uri.parse("smsto:+4917687955267")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", "")
        startActivity(intent)
    }


    fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:" + "recipient@example.com")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My email's subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "My email's body")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun callSupport(){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+4917687955267")
        startActivity(intent)
    }


    fun sendwhatsApp(){
        try {
            // Check if whatsapp is installed
            application.packageManager?.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+4917687955267"))
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(application, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
        }
    }


}