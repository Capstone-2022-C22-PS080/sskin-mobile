package com.example.skindiseasedetectionapp.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityDashboardBinding
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity
import com.example.skindiseasedetectionapp.ui.home.CameraActivity
import com.example.skindiseasedetectionapp.ui.home.ProfilesActivity
import com.example.skindiseasedetectionapp.ui.home.ScanHistoriesActivity
import com.example.skindiseasedetectionapp.ui.home.SettingActivity
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_loading)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()



        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[DashboardViewModel::class.java]
        viewModel.getDatastoreUser().observe(this){
            if(it != null && (it.userId == "" || it.userId != null)){
                updateUi(it)

            }else{
                finish()
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
            }

        }



        auth = Firebase.auth

        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.btnProfiles.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,ProfilesActivity::class.java))

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

        binding.imgProfile.setOnClickListener {
            viewModel.clearDatastore()
            finish()
            startActivity(Intent(this@DashboardActivity,LoginActivity::class.java))

        }


    }


    private fun updateUi(inUserModel: InUserModel){
        if(inUserModel.photo_profile == "" || inUserModel == null){
            dialog.dismiss()
        }
        binding.tvName.text = inUserModel.name
        Log.d(TAG, "updateUi2: ${inUserModel.jwtToken}")

    }
    
    companion object{
        val TAG = "DashboardActivity"
    }
}