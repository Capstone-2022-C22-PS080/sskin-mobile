package com.example.skindiseasedetectionapp.ui.auth.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.skindiseasedetectionapp.MainActivity
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.databinding.ActivityLoginBinding
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterActivity
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterViewModel
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.example.skindiseasedetectionapp.ui.home.SettingActivity
import com.example.skindiseasedetectionapp.utill.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sign

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(SettingDatastore.getInstance(dataStore))
        )[LoginViewModel::class.java]
        viewModel.loading.observe(this){

            showLoading(it)
        }

        viewModel.messageLogin.observe(this) {
            it.getContentIfNotHandled().let { str ->
                toasting(str.toString())
                if(str!!.contains("Successfully")){
                    finish()
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                }

            }
        }

        viewModel.messageAddFirebaseUser.observe(this) {
            it.getContentIfNotHandled().let { str ->
                if (str!!.contains("success")) {
                    clearInput()
                }
                toasting(str.toString())
            }
        }

        viewModel.currentUser.observe(this){
            if(it != null){
                updateUI(it)
            }else{
                updateUI(null)
            }
        }

        auth = Firebase.auth

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener{
           // startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
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



    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    showLoading(false)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showLoading(false)
                    toasting(resources.getString(R.string.auth_failed))
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentuser : FirebaseUser?){
        if (currentuser != null){
            toasting(currentuser.uid)
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
            finish()
        }
    }

    private fun reload() {

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
            viewModel.currentUser.observe(this){

                updateUI(it)
            }
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }


    companion object{
        const val TAG = "LoginActivity"
    }
        // [EN
}