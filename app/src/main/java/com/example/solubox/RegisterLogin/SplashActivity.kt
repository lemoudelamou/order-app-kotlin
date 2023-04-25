package com.example.solubox.RegisterLogin

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.solubox.MainActivity
import com.example.solubox.R.anim
import com.example.solubox.Utils.TokenManager
import com.example.solubox.databinding.ActivitySplashBinding


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME: Long = 3000
    private var tokenManager: TokenManager? = null
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        val view = binding.root
        setContentView(view)
        val progressBar = binding.loader


        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )

        val backgroundImage: ImageView = binding.imageView
        val slideAnimation = AnimationUtils.loadAnimation(this, anim.side_slide)
        backgroundImage.startAnimation(slideAnimation)

        val mobileNwInfo: Boolean
        val conxMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        mobileNwInfo = try {
            conxMgr.activeNetworkInfo!!.isConnected
        } catch (e: NullPointerException) {
            false
        }

    Handler(Looper.getMainLooper()).postDelayed({
        progressBar.isIndeterminate = true

        if (mobileNwInfo) {
            if (tokenManager?.token?.accessToken != null) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } else {
                val intent = Intent(this@SplashActivity, LogRegActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
        }

    }, SPLASH_TIME)

    }

}
