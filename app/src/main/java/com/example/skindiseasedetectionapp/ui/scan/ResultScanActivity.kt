package com.example.skindiseasedetectionapp.ui.scan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.databinding.ActivityResultScanBinding
import com.example.skindiseasedetectionapp.model.PredictionResponse
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.example.skindiseasedetectionapp.utill.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ResultScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBinding
    private var uri: String? = null
    private var prediction: PredictionResponse? = null
    private lateinit var viewModel: ResultScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uri = intent.getStringExtra("URI")
        prediction = intent.getParcelableExtra<PredictionResponse>("PREDICTION") as PredictionResponse

        binding.imgResult.setImageURI(Uri.parse(uri))
        binding.tvDiagnose.text = prediction!!.disease_name
        binding.tvDesc.text = prediction!!.disease_description
        binding.tvFirstAid.text = prediction!!.first_aid_description
        binding.label4.visibility = android.view.View.GONE

        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[ResultScanViewModel::class.java]

        binding.btnFinish.setOnClickListener {
            viewModel.getDatastore().observe(this){
                if(it != null){
                    val intent = Intent(this@ResultScanActivity,DashboardActivity::class.java)
                    intent.putExtra(DashboardActivity.EXTRA_USER,it)
                    startActivity(intent)
                    finish()
                }
            }
        }

    }

    companion object{
        const val EXTRA_DATA ="extra_data"
    }

}