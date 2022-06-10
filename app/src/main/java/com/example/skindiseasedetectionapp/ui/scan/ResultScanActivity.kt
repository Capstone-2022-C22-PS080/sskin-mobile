package com.example.skindiseasedetectionapp.ui.scan

import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityResultScanBinding
import com.example.skindiseasedetectionapp.databinding.ActivityScanResultBinding
import com.example.skindiseasedetectionapp.model.PredictionResponse

class ResultScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBinding
    private var uri: String? = null
    private var prediction: PredictionResponse? = null

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

    }

    companion object{
        const val EXTRA_DATA ="extra_data"
    }

}