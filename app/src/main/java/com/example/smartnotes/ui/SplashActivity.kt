package com.example.smartnotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartnotes.MainActivity
import com.example.smartnotes.R
import com.example.smartnotes.utils.AuthManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check login status after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (AuthManager.isLoggedIn(this)) {
                // User is logged in → Go to main app
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User not logged in → Go to login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }
}