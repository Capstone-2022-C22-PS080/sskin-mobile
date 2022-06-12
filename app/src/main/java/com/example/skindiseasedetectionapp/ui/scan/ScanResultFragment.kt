package com.example.skindiseasedetectionapp.ui.scan

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.FragmentScanResultBinding
import com.example.skindiseasedetectionapp.model.PredictionResponse
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.home.ImagePreviewGalleryFragment

private const val URI_PARAM = "uri_param"
private const val PREDICT_PARAM ="predic_param"

class ScanResultFragment : Fragment() {
    private var uriParam: String? = null

    private lateinit var binding: FragmentScanResultBinding
    private var predictionResponse: PredictionResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            uriParam = it.getString(URI_PARAM)
            predictionResponse = it.getParcelable(PREDICT_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.popBackStack()
//        predictionResponse = arguments?.getParcelable<PredictionResponse>(EXTRA_DATA)!! as PredictionResponse
//        val img = arguments?.getString("URI") as String
//        binding.tvDiagnose.text = predictionResponse!!.disease_name
//        binding.tvDesc.text = predictionResponse!!.disease_description
  //      binding.imgResult.setImageURI(Uri.parse(img))
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
        const val EXTRA_DATA = "extra_data"

        @JvmStatic fun newInstance(param1: String,predictionResponse: PredictionResponse) =
            ScanResultFragment().apply {
                arguments = Bundle().apply {
                    putString(URI_PARAM, param1)
                    putParcelable(PREDICT_PARAM,predictionResponse)
                }
            }
    }
}