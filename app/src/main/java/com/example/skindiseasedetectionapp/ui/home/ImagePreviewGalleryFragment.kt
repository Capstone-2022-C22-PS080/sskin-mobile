package com.example.skindiseasedetectionapp.ui.home

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.databinding.FragmentImagePreviewGalleryBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import com.example.skindiseasedetectionapp.utill.uriToFile
import java.io.File
import java.io.IOException
import java.net.URI

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val URI_PARAM = "uri_param"


/**
 * A simple [Fragment] subclass.
 * Use the [ImagePreviewGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ImagePreviewGalleryFragment : Fragment() {

    private var uriParam: String? = null
    private var dataStoreParam: SettingDatastore? = null
    private lateinit var binding: FragmentImagePreviewGalleryBinding
    private lateinit var viewModel: ImagePreviewGalleryViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            uriParam = it.getString(URI_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentImagePreviewGalleryBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemUI()

        val pref = requireContext().dataStore
        val setting = SettingDatastore.getInstance(pref)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(setting)
        )[ImagePreviewGalleryViewModel::class.java]

        val uri = Uri.parse(uriParam)

        binding.photoView.setImageURI(uri)
        binding.btnScan.setOnClickListener {
            if(uri != null){
                val myFile = uriToFile(uri, application = requireActivity().application)
                viewModel.getDataStore().observe(viewLifecycleOwner){
                    if(it.jwtToken != null){
                        viewModel.prediction(it.jwtToken!!,myFile)
                    }
                }
            }
        }

    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun toasting(msg: String){
        Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ImagePreviewGalleryFragment.
         */
        const val TAG = "imagePreviewGallery"

        @JvmStatic fun newInstance(param1: String) =
                ImagePreviewGalleryFragment().apply {
                    arguments = Bundle().apply {
                        putString(URI_PARAM, param1)
                    }
                }
    }
}