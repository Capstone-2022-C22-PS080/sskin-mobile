package com.example.skindiseasedetectionapp.ui.auth.register

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.JwtToken
import com.example.skindiseasedetectionapp.model.ProfilesUser
import com.example.skindiseasedetectionapp.ui.auth.register.RegisterActivity.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.skindiseasedetectionapp.utill.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.Collections.list

class RegisterViewModel : ViewModel() {
    private val _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _inUserModel: MutableLiveData<InUserModel> = MutableLiveData<InUserModel>()
    val inUserModel: LiveData<InUserModel> = _inUserModel

    private val _messageRegis: MutableLiveData<Event<String>> = MutableLiveData<Event<String>>()
    val messageRegis: LiveData<Event<String>> = _messageRegis

    private val _messageAddFirebaseUser: MutableLiveData<Event<String>> = MutableLiveData<Event<String>>()
    val messageAddFirebaseUser: LiveData<Event<String>> = _messageAddFirebaseUser

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    init {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun signUp(email: String,password: String,name: String,date: Date){
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _loading.postValue(false)
                    if (task.isSuccessful) {
                        Log.d(RegisterActivity.TAG, "createUserWithEmail:success")
                        val user = auth.currentUser

                        _messageRegis.postValue(Event("Register Successfully!"))
                        addUserInAdd(user,name,date)

                    } else {
                        Log.w(RegisterActivity.TAG, "createUserWithEmail:failure", task.exception)
                        _messageRegis.postValue(Event(task.exception!!.message.toString()))

                    }

                }
                .addOnSuccessListener {
                    _messageRegis.postValue(Event("Register Successfully!"))
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    _messageRegis.postValue(Event(it.message!!))
                }
            }
        }


    private fun addUserInAdd(user: FirebaseUser?,name: String,date: Date){
        Log.d(TAG, "addUserInAdd: ${date.toString()}")
        val array = arrayOf( ProfilesUser("", id = 1, name = name, Timestamp(date)) )

        db.collection("users")
            .document(user?.uid!!)
            .set(
                InUserModel
                    (default_profile = 1,
                    email = user?.email,
                    name = name,
                    photo_profile = "",
                    userId = user?.uid,
                    profiles = array.asList()
                )
            )
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _messageAddFirebaseUser.postValue(Event("success"))
                }else{
                    _messageAddFirebaseUser.postValue(Event(it.exception?.message!!))
                }
                _loading.postValue(false)
            }
            .addOnSuccessListener {
                Log.d(RegisterActivity.TAG, "add user berhasil ")
                _messageAddFirebaseUser.postValue(Event("success"))
                _loading.postValue(false)
            }
            .addOnFailureListener {
                Log.d(RegisterActivity.TAG, "add user gagal ${it.message} ")
                _messageAddFirebaseUser.postValue(Event(it.message!!))
                it.printStackTrace()
                _loading.postValue(false)
            }
    }


}