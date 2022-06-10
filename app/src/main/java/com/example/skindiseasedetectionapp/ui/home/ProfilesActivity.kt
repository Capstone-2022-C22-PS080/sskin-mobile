package com.example.skindiseasedetectionapp.ui.home

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityProfilesBinding
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ProfilesUser
import com.example.skindiseasedetectionapp.ui.adapters.ProfilesAdapter
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

class ProfilesActivity : AppCompatActivity() {
    private lateinit var backBtn: AppCompatImageButton
    private lateinit var titleAction: TextView
    private lateinit var binding: ActivityProfilesBinding
    private var users : InUserModel? = null
    private lateinit var viewModel : ProfilesViewModel

    private lateinit var dialog: Dialog
    private lateinit var btnCancel : AppCompatButton
    private lateinit var btnSave : AppCompatButton
    private lateinit var btnAddImage: AppCompatImageButton
    private lateinit var inputNameDialog: TextInputEditText
    private lateinit var progressbarDialog: ProgressBar
    private lateinit var checkBoxDialog: CheckBox
    private lateinit var photoProfileDialog: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
        bindEvent()

        viewModel = ViewModelProvider(this)[ProfilesViewModel::class.java]

        viewModel.loading.observe(this){
            showLoading(it)
        }

        viewModel.loadingAdd.observe(this){
            showLoadingDialog(it)
        }




        val adapterProfile = ProfilesAdapter{

        }



        if(users != null ){
            Log.d(TAG, "onCreate: users ga kosong")
            viewModel.setProfiles(users?.userId!!)
            viewModel.inUserModel.observe(this){
                if (it != null) {
                    users!!.profiles = it.profiles
                    adapterProfile.submitList(it.profiles)
                }
            }

        }

        binding.listProfile.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterProfile
        }
    }

    private fun setUp(){
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)

        val view = supportActionBar?.customView
        backBtn = view?.findViewById(R.id.btnBack)!!
        titleAction = view.findViewById(R.id.titleAction)!!
        titleAction.text = resources.getString(R.string.profiles)

        users = intent.getParcelableExtra<InUserModel>(EXTRA_USER)!! as InUserModel


        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_profiles)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)
        btnSave = dialog.findViewById<AppCompatButton>(R.id.btnSave)
        btnAddImage = dialog.findViewById(R.id.uploadButton)
        progressbarDialog = dialog.findViewById(R.id.progressBarDialog)
        inputNameDialog = dialog.findViewById(R.id.inputName)
        checkBoxDialog = dialog.findViewById(R.id.cbSetProfile)
        photoProfileDialog = dialog.findViewById(R.id.imgProfile)


    }

    private fun bindEvent(){

        binding.fabAdd.setOnClickListener {

            dialog.show()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val name = inputNameDialog.text?.toString()?.trim()
            val id = viewModel.getId(docId = users?.userId!!)
            val createAt = FieldValue.serverTimestamp()

            if(name?.isEmpty()!!){
                toasting("please Fill the form first")
                return@setOnClickListener
            }

            val newProfile = ProfilesUser(fotoUrl = "",id = id,name = name, Timestamp(java.util.Date()))

            if(dialog.isShowing){
                viewModel.addProfile(users?.userId!!,newProfile)
                viewModel.messageAdd.observe(this) {
                    it.getContentIfNotHandled().let { str ->
                        toasting(str.toString())
                        dialog.dismiss()
                    }
                }

            }



        }
    }

    private fun toasting(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(b: Boolean) {
        if (b) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun showLoadingDialog(b: Boolean) {
        if (b) progressbarDialog.visibility = View.VISIBLE
        else progressbarDialog.visibility = View.GONE
    }

    companion object{
        const val EXTRA_USER = "extra_user"
        const val TAG = "ProfileActivity"
    }
}