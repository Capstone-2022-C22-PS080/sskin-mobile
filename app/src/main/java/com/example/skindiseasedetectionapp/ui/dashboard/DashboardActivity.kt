package com.example.skindiseasedetectionapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skindiseasedetectionapp.databinding.ActivityDashboardBinding
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity
import com.example.skindiseasedetectionapp.ui.home.CameraActivity
import com.example.skindiseasedetectionapp.ui.home.ProfilesActivity
import com.example.skindiseasedetectionapp.ui.home.ScanHistoriesActivity
import com.example.skindiseasedetectionapp.ui.home.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        auth = Firebase.auth

        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.btnProfiles.setOnClickListener {
            auth.signOut()
            finish()
            startActivity(Intent(this@DashboardActivity,LoginActivity::class.java))

        }

        binding.btnScan.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,CameraActivity::class.java))
        }

        binding.btnScanHistory.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,ScanHistoriesActivity::class.java))
        }

        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,SettingActivity::class.java))
        }


    }
}