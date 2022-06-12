package com.example.skindiseasedetectionapp.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityProfilesBinding
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.ui.adapters.ProfilesAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText

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
    private var flag = "add"
    private var indexForEdit = -1
    private var idForedit = 0

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

        viewModel.messageAdd.observe(this) { str ->
            str.getContentIfNotHandled().let { str2 ->
                toasting(str2.toString())
            }
        }








        val adapterProfile = ProfilesAdapter({
            if( it!= null){
                dialog.setTitle("Edit Profile")
                dialog.show()
                flag = "edit"
                idForedit = it.id!!
                inputNameDialog.setText(it.name!!)
            }
        },{
            if( it!= null){
                viewModel.deleteProfileById(it.id!!, users?.userId!!)
            }
        })

        viewModel.inUserModel.observe(this){
            if (it != null) {
                users!!.profiles = it.profiles
                adapterProfile.submitList(it.profiles)
            }
        }

        viewModel.setProfiles(users?.userId!!)

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
        dialog.setTitle("Add Profile")

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
            if(name?.isEmpty()!!){
                toasting("please Fill the form first")
                return@setOnClickListener
            }



            if(dialog.isShowing){
                if(flag == "add"){
                    val size = users?.profiles?.size ?: 0
                    if(size > 0){
                        viewModel.addProfile(users?.userId!!,name)

                    }
                }else{
                    viewModel.editProfile(idForedit, users?.userId!!,name)
                    flag="add"
                    dialog.setTitle("Add Profile")
                    inputNameDialog.setText("")
                }

                dialog.dismiss()


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