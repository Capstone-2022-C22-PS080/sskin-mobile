package com.example.skindiseasedetectionapp.ui.front

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.databinding.ActivityGetStartedBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterActivity
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.example.skindiseasedetectionapp.utill.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class GetStartedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetStartedBinding
    private lateinit var viewModel: GetStartedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[GetStartedViewModel::class.java]

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this@GetStartedActivity,RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            viewModel.getDatastore().observe(this){
               if(it.jwtToken != ""){
                    val intent = Intent(this@GetStartedActivity, DashboardActivity::class.java)
                    intent.putExtra(DashboardActivity.EXTRA_USER,it)
                    startActivity(intent)
                }else{
                   startActivity(Intent(this@GetStartedActivity,LoginActivity::class.java))
               }
                finish()
            }

        }
    }
}