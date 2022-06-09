package com.example.skindiseasedetectionapp.ui.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityScanResultBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.scan.LoadingScanFragment
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ScanResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanResultBinding
    private lateinit var backBtn: AppCompatImageButton
    private lateinit var titleAction: TextView
    private lateinit var viewModel: ScanResultViewModel
    private var uriFromGallery: Uri? = null

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
        uriFromGallery = intent.getParcelableExtra(FROM_GALLERY)!! ?: null

        if(uriFromGallery != null){
            titleAction.text = getString(R.string.image_preview)
            val imagePreviewGalleryFragment = ImagePreviewGalleryFragment.newInstance(uriFromGallery.toString())
            val fragment = mFragmentManager.findFragmentByTag(ImagePreviewGalleryFragment::class.java.simpleName)
            if (fragment !is ImagePreviewGalleryFragment) {
                Log.d("MyFlexibleFragment", "Fragment Name :" + ImagePreviewGalleryFragment::class.java.simpleName)
                mFragmentManager
                    .beginTransaction()
                    .add(R.id.container_scan_result, imagePreviewGalleryFragment, ImagePreviewGalleryFragment::class.java.simpleName)
                    .commit()
            }
        }else{
            titleAction.text = resources.getString(R.string.scan_histories)



            val loadingFragment = LoadingScanFragment()
            val fragment = mFragmentManager.findFragmentByTag(LoadingScanFragment::class.java.simpleName)
            if (fragment !is LoadingScanFragment) {
                mFragmentManager
                    .beginTransaction()
                    .add(R.id.container_scan_result, loadingFragment, LoadingScanFragment::class.java.simpleName)
                    .commit()
            }

            val file = intent.getSerializableExtra(FILE) as File
            viewModel.getDataStore().observe(this){
                it.jwtToken?.let { it1 -> viewModel.prediction(it1,file) }
            }


        }



    }

    private fun toasting(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }


    companion object{
        const val TAG = "ScanResultActivity"
        const val FILE = "FILE"
        const val FROM_GALLERY = "from_gallery"
    }
}