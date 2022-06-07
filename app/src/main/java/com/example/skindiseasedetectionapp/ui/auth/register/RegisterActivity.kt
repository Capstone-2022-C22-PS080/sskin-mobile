package com.example.skindiseasedetectionapp.ui.auth.register

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
//    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var btnBack : ImageButton
    private lateinit var titleAction : TextView
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)

        val view = supportActionBar?.customView
        btnBack = view?.findViewById(R.id.btnBack)!!
        titleAction = view.findViewById(R.id.titleAction)!!

        btnBack.setOnClickListener {
            onBackPressed()
            finish()
        }



        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        viewModel.loading.observe(this){

            showLoading(it)
        }

        viewModel.messageRegis.observe(this) {
            it.getContentIfNotHandled().let { str ->
                toasting(str.toString())
            }
        }

        viewModel.messageAddFirebaseUser.observe(this) {
            it.getContentIfNotHandled().let { str ->
                toasting(str.toString())
                if (str!!.contains("Successfully")) {
                    clearInput()
                }

            }
        }



        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            signup()
        }

    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            reload();
//        }
//    }

    private fun signup(){
        val email = binding.inputEmail.text.toString().trim()
        val name =  binding.inputName.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()
        val password2 =  binding.inputPassword2.text.toString().trim()

        if(name.isEmpty()){
            binding.inputName.error = resources.getString(R.string.null_email)
            return
        }

        if(email.isEmpty()){
            binding.inputEmail.error = resources.getString(R.string.null_email)
            return
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.inputEmail.error = resources.getString(R.string.not_valid_email)
                binding.inputEmail.requestFocus()
                binding.inputEmail.background = ContextCompat.getDrawable(this,R.drawable.rounded_text_red)
                return
            }
        }

        if(password.isEmpty()){
            binding.inputPassword.error = resources.getString(R.string.null_password)
            binding.inputPassword.requestFocus()
            binding.inputPassword.background = ContextCompat.getDrawable(this,R.drawable.rounded_text_red)
            return
        }
        if(password2.isEmpty()){
            binding.inputPassword2.error = resources.getString(R.string.null_password)
            binding.inputPassword2.requestFocus()
            binding.inputPassword2.background = ContextCompat.getDrawable(this,R.drawable.rounded_text_red)
            return
        }


        if(password.isNotEmpty() && password2.isNotEmpty()){
            if(password.length < 6){
                binding.inputPassword.error = resources.getString(R.string.should_6_password)
                binding.inputPassword.requestFocus()
                binding.inputPassword.background = ContextCompat.getDrawable(this,R.drawable.rounded_text_red)

                return
            }
            if(password2.length < 6){
                binding.inputPassword2.error = resources.getString(R.string.should_6_password)
                binding.inputPassword2.requestFocus()
                binding.inputPassword2.background = ContextCompat.getDrawable(this,R.drawable.rounded_text_red)
                return
            }
            if(password2 != password){
                binding.inputPassword2.error = resources.getString(R.string.pass_not_same)
                return
            }
        }


        showLoading(true)
        viewModel.signUp(email, password,name,Date());
    }

    private fun toasting(message: String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    private fun showLoading(b: Boolean) {
        if (b) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun clearInput(){
       binding.inputEmail.setText("")
        binding.inputName.setText("")
        binding.inputPassword.setText("")
        binding.inputPassword2.setText("")
    }

    companion object{
         val TAG = "RegisterActivity"
    }

}