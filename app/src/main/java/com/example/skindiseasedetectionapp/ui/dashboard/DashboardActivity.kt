package com.example.skindiseasedetectionapp.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityDashboardBinding
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ProfilesUser
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

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var dialog: Dialog
    private var inUserModel : InUserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
        bindViewModel()
        bindEvent()

    }

    private fun setUp(){
        supportActionBar?.hide()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_loading)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }


    private fun updateUi(user: InUserModel){
        if(user.profiles != null){
            user.profiles?.forEach {
               if( it.id == inUserModel?.default_profile ) {
                   binding.tvName.text = it.name
                   binding.imgProfile.setImageDrawable(getDrawable(R.drawable.ic_baseline_account_circle_24))
                   dialog.dismiss()
                   return@forEach
               }
            }
        }


    }

    private fun bindViewModel(){
        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[DashboardViewModel::class.java]

        inUserModel = intent.getParcelableExtra<InUserModel>(EXTRA_USER)!! as InUserModel

        if(inUserModel == null){
            finish()
            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
            return
        }

        viewModel.getData(inUserModel?.userId!!)
        viewModel.inUserModel.observe(this){
            if(it != null){
                updateUi(it)

            }
        }
    }

    private fun bindEvent(){


        binding.btnProfiles.setOnClickListener {
            val intent = Intent(this@DashboardActivity,ProfilesActivity::class.java)
            intent.putExtra(ProfilesActivity.EXTRA_USER,inUserModel)
            startActivity(intent)
            finish()
        }

        binding.btnScan.setOnClickListener {
            startActivity(Intent(this@DashboardActivity,CameraActivity::class.java))
            finish()
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
    
    companion object{
        val TAG = "DashboardActivity"
        const val EXTRA_USER = "extra_user"
    }
}