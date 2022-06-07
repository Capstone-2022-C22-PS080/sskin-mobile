package com.example.skindiseasedetectionapp.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.FragmentLoadingScanBinding


class LoadingScanFragment : Fragment() {

    private lateinit var binding: FragmentLoadingScanBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoadingScanBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {
    }
}