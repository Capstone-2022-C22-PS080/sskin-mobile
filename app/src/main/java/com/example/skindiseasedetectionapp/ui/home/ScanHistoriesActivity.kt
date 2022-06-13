package com.example.skindiseasedetectionapp.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityScanHistoriesBinding
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.adapters.DetectionsAdapter
import com.example.skindiseasedetectionapp.utill.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ScanHistoriesActivity : AppCompatActivity() {
    private lateinit var backBtn: AppCompatImageButton
    private lateinit var titleAction: TextView
    private lateinit var binding: ActivityScanHistoriesBinding
    private lateinit var viewModel: ScanHistoriesVieModel
    private var user : InUserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanHistoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)

        val view = supportActionBar?.customView
        backBtn = view?.findViewById(R.id.btnBack)!!
        titleAction = view.findViewById(R.id.titleAction)!!
        titleAction.text = resources.getString(R.string.scan_histories)

        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[ScanHistoriesVieModel::class.java]

        viewModel.getDatastore().observe(this){
            if(it != null){
                viewModel.getData(it.userId!!)
            }
        }

        val adapterScan = DetectionsAdapter{

        }

        viewModel.loading.observe(this){
            showLoading(it)
        }

        viewModel.detectionHistories.observe(this){
            if(it != null){
                adapterScan.submitList(it)
            }
        }
        binding.listScan.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterScan
        }

    }

    private fun showLoading(b: Boolean) {
        if (b) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    companion object{
        const val TAG = "ScanHistories"
    }
}