package com.example.skindiseasedetectionapp.ui.home

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityProfilesBinding

class ProfilesActivity : AppCompatActivity() {
    private lateinit var backBtn: AppCompatImageButton
    private lateinit var titleAction: TextView
    private lateinit var binding: ActivityProfilesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)

        val view = supportActionBar?.customView
        backBtn = view?.findViewById(R.id.btnBack)!!
        titleAction = view.findViewById(R.id.titleAction)!!
        titleAction.text = resources.getString(R.string.profiles)

        binding.fabAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this@ProfilesActivity)
            builder.setView(View.inflate(this@ProfilesActivity,R.layout.dialog_profiles,null))
            builder.setTitle("Add Profile")

            val dialog = builder.create()
            val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
            val btnSave = dialog.findViewById<AppCompatButton>(R.id.btnSave)

            btnCancel?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

    }
}