package com.example.skindiseasedetectionapp.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityScanResultBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.scan.LoadingScanFragment
import com.example.skindiseasedetectionapp.ui.scan.ResultScanActivity
import com.example.skindiseasedetectionapp.ui.scan.ScanResultFragment
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ScanResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanResultBinding
    private lateinit var backBtn: AppCompatImageButton
    private lateinit var titleAction: TextView
    private lateinit var viewModel: ScanResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)

        val view = supportActionBar?.customView
        backBtn = view?.findViewById(R.id.btnBack)!!
        titleAction = view.findViewById(R.id.titleAction)!!

        val mFragmentManager = supportFragmentManager

        val pref = SettingDatastore.getInstance(dataStore)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[ScanResultViewModel::class.java]


        // uri impement dari Parcelable
//        uriFromGallery = intent.getParcelableExtra(FROM_GALLERY)!! ?: null

        val uri = intent.getStringExtra(EXTRA_URI)
        if(uri == null){
            onBackPressed()
            return
        }
        val imagePreviewGalleryFragment = ImagePreviewGalleryFragment.newInstance(uri)
        val fragment = mFragmentManager.findFragmentByTag(ImagePreviewGalleryFragment::class.java.simpleName)

        if (fragment !is ImagePreviewGalleryFragment) {
            titleAction.text = "Result"
            Log.d("MyFlexibleFragment", "Fragment Name :" + ImagePreviewGalleryFragment::class.java.simpleName)
            mFragmentManager
                .beginTransaction()
                .add(R.id.container_scan_result, imagePreviewGalleryFragment, ImagePreviewGalleryFragment::class.java.simpleName)
                .commit()
        }



//
//        if(uriFromGallery != null){
//            titleAction.text = getString(R.string.image_preview)
//            val imagePreviewGalleryFragment = ImagePreviewGalleryFragment.newInstance(uriFromGallery.toString())
//            val fragment = mFragmentManager.findFragmentByTag(ImagePreviewGalleryFragment::class.java.simpleName)
//            if (fragment !is ImagePreviewGalleryFragment) {
//                Log.d("MyFlexibleFragment", "Fragment Name :" + ImagePreviewGalleryFragment::class.java.simpleName)
//                mFragmentManager
//                    .beginTransaction()
//                    .add(R.id.container_scan_result, imagePreviewGalleryFragment, ImagePreviewGalleryFragment::class.java.simpleName)
//                    .commit()
//            }
//        }else{
//            titleAction.text = resources.getString(R.string.scan_histories)
//
//
//
//            val loadingFragment = LoadingScanFragment()
//            val fragment = mFragmentManager.findFragmentByTag(LoadingScanFragment::class.java.simpleName)
//            if (fragment !is LoadingScanFragment) {
//                mFragmentManager
//                    .beginTransaction()
//                    .add(R.id.container_scan_result, loadingFragment, LoadingScanFragment::class.java.simpleName)
//                    .commit()
//            }
//
//            val file = intent.getSerializableExtra(FROM_CAMERA) as File
//            viewModel.getDataStore().observe(this){
//                viewModel.prediction(it.jwtToken!!,file)
//                viewModel.predictionResponse.observe(this){ predictResponse ->
//                    if(predictResponse != null){
//                        val intent = Intent(this, ResultScanActivity::class.java)
//                        intent.putExtra("PREDICTION",predictResponse)
//                        intent.putExtra("URI",uriFromGallery.toString())
//                        startActivity(intent)
//                    }
//                }
//            }
//
//
//        }



    }

    private fun toasting(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }


    companion object{
        const val TAG = "ScanResultActivity"
        const val EXTRA_URI = "uri"
        const val EXTRA_PREDICT = "predict"

    }
}