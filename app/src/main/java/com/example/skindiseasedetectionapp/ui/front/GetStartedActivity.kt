package com.example.skindiseasedetectionapp.ui.front

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityGetStartedBinding
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterActivity

class GetStartedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this@GetStartedActivity,RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@GetStartedActivity,LoginActivity::class.java))
        }
    }
}