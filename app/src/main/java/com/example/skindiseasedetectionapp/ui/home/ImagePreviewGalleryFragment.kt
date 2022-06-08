package com.example.skindiseasedetectionapp.ui.home

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.FragmentImagePreviewGalleryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val URI_PARAM = "uri_param"

/**
 * A simple [Fragment] subclass.
 * Use the [ImagePreviewGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImagePreviewGalleryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uriParam: String? = null
    private lateinit var binding: FragmentImagePreviewGalleryBinding


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
        val uri = Uri.parse(uriParam)
        binding.photoView.setImageURI(uri)
        binding.btnScan.z = 2f

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ImagePreviewGalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String) =
                ImagePreviewGalleryFragment().apply {
                    arguments = Bundle().apply {
                        putString(URI_PARAM, param1)
                    }
                }
    }
}