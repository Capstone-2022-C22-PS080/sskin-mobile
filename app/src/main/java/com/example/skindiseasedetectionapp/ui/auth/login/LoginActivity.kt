package com.example.skindiseasedetectionapp.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityLoginBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterActivity
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        val pref = SettingDatastore.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[LoginViewModel::class.java]
        viewModel.loading.observe(this){

            showLoading(it)
        }

        viewModel.messageLogin.observe(this) {
            it.getContentIfNotHandled().let { str ->
                toasting(str.toString())
            }
        }


        viewModel.messageAddFirebaseUser.observe(this) {
            it.getContentIfNotHandled().let { str ->
                    toasting(str.toString())
                    if(str.toString().contains("Success")){
                        clearInput()
                        finish()
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))

                    }
            }
        }


        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener{
           //startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
            logIn()
        }


    }

    private fun logIn(){
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()

        if(email.isEmpty()){
            binding.inputEmail.error = resources.getString(R.string.null_email)
            return
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.inputEmail.error = resources.getString(R.string.not_valid_email)
                return
            }
        }

        if(password.isEmpty() ){
            binding.inputPassword.error = resources.getString(R.string.null_password)
        }else{
            if(password.length < 6 ){
                binding.inputPassword.error = resources.getString(R.string.should_6_password)
                return
            }

        }

        showLoading(true)
        viewModel.logIn(email,password)
    }

    private fun toasting(message: String){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(b: Boolean) {
        if (b) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun clearInput(){
        binding.inputEmail.setText("")
        binding.inputPassword.setText("")
    }






    companion object{
        const val TAG = "LoginActivity"
    }
        // [EN
}