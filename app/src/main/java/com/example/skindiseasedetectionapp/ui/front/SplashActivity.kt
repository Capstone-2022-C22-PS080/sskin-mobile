package com.example.skindiseasedetectionapp.ui.front

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            val intent = Intent(this, GetStartedActivity::class.java)
            startActivity(intent)
        },2500)
    }

}