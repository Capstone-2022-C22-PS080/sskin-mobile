package com.example.skindiseasedetectionapp.ui.scan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.FragmentLoadingScanBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.home.ImagePreviewGalleryFragment
import com.example.skindiseasedetectionapp.ui.home.ImagePreviewGalleryViewModel
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import com.example.skindiseasedetectionapp.utill.createFile
import com.example.skindiseasedetectionapp.utill.uriToFile


private const val URI_PARAM = "uri_param"
class LoadingScanFragment : Fragment() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: FragmentLoadingScanBinding
    private var uriParam: String? = null
    private lateinit var viewModel: LoadingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            if (it != null) {
                uriParam = it.getString(URI_PARAM)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoadingScanBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pref = requireContext().dataStore
        val setting = SettingDatastore.getInstance(pref)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(setting)
        )[LoadingViewModel::class.java]

        val file = uriToFile(Uri.parse(uriParam),requireActivity().application)

        viewModel.getDataStore().observe(viewLifecycleOwner){
            if(it != null){
                viewModel.prediction(it!!,file)
                viewModel.success.observe(viewLifecycleOwner){ yes->
                        viewModel.predictionResponse.observe(viewLifecycleOwner) { predic ->
                            if (predic != null) {
                                val intent = Intent(requireActivity(), ResultScanActivity::class.java)
                                intent.putExtra("PREDICTION", predic)
                                intent.putExtra("URI", uriParam)
                                startActivity(intent)
                            }else{
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.container_scan_result,TryAgainFragment(),TryAgainFragment::class.java.simpleName)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                }

            }
        }



        binding.label1.setOnClickListener {
                    parentFragmentManager.beginTransaction()
            .replace(R.id.container_scan_result,ScanResultFragment(),ScanResultFragment::class.java.simpleName)
            .addToBackStack(null)
            .commit()
        }
    }


    companion object{
        const val TAG = "LoadingScanFragment"
        @JvmStatic fun newInstance(param1: String) =
            LoadingScanFragment().apply {
                arguments = Bundle().apply {
                    putString(URI_PARAM, param1)
                }
            }
    }

}