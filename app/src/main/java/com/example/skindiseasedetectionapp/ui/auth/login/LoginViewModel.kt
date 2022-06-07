package com.example.skindiseasedetectionapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseasedetectionapp.R
import com.example.skindiseasedetectionapp.api.ApiConfig
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.JwtToken
import com.example.skindiseasedetectionapp.model.Uid
import com.example.skindiseasedetectionapp.setting.SettingDatastore
import com.example.skindiseasedetectionapp.ui.auth.login.LoginActivity.Companion.TAG
import com.example.skindiseasedetectionapp.utill.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginViewModel(private val datastore: SettingDatastore) : ViewModel() {
    private val _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _inUserModel: MutableLiveData<InUserModel?> = MutableLiveData<InUserModel?>()
    val inUserModel: LiveData<InUserModel?> = _inUserModel

    private val _messageLogin: MutableLiveData<Event<String>> = MutableLiveData<Event<String>>()
    val messageLogin: LiveData<Event<String>> = _messageLogin

    private val _messageAddFirebaseUser: MutableLiveData<Event<String>> = MutableLiveData<Event<String>>()
    val messageAddFirebaseUser: LiveData<Event<String>> = _messageAddFirebaseUser

    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    val currentUser: LiveData<FirebaseUser> = _currentUser



    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    init {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    // [START sign_in_with_email]
    fun logIn(email: String, password: String){
        _loading.value = true
        var user : FirebaseUser? = null
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(LoginActivity.TAG, "signInWithEmail:success")
                        _messageLogin.postValue(Event("Login Successfully!"))
                         user = auth.currentUser
                        if(user != null) _currentUser.value= user!!
                        else return@addOnCompleteListener
                        getProfiles(_currentUser.value)


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LoginActivity.TAG, "signInWithEmail:failure", task.exception)
                        _messageLogin.postValue(Event(task.exception!!.message.toString()))
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    _messageLogin.postValue(Event(it.message!!))
                    _loading.postValue(false)
                }
                .addOnSuccessListener {
                    _messageLogin.postValue(Event("Login Successfully!"))
                    _currentUser.value = it.user
                }


        }

    }

    private fun getProfiles(user: FirebaseUser?){
        Log.d(LoginActivity.TAG, "getProfiles: ${user?.uid} ")
        val docRef = user?.uid?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener {
            if (it != null) {
                Log.d(TAG, "DocumentSnapshot data: ${it.data}")
                _inUserModel.value = it.toObject(InUserModel::class.java)
                Log.d(TAG, "getProfiles: ${_inUserModel.value?.email}")

                getTokenFromApi(user)
                _messageAddFirebaseUser.value = Event("Success")
            } else {
                Log.d(TAG, "No such document")
                _inUserModel.value = null

                _messageAddFirebaseUser.value = Event("No Such Document")
            }
        }?.addOnFailureListener {
            Log.d(TAG, "get failed with ", it)
            _messageAddFirebaseUser.value = Event(it.message.toString())
            it.printStackTrace()
        }

    }

    private fun getTokenFromApi(firebaseUser: FirebaseUser){
        val client = ApiConfig.getApiServce().getToken(Uid(firebaseUser.uid))
        client.enqueue(object : Callback<JwtToken>{
            override fun onResponse(
                call: Call<JwtToken>,
                response: Response<JwtToken>
            ) {
                _loading.postValue(false)
                if(response.isSuccessful && response.body() != null){
                    val body = response.body()
                    Log.d(TAG, "onResponse: successful make jwtToken ${body?.jwtToken}")
                    _inUserModel.value?.jwtToken = body?.jwtToken
                    _loading.value = false
                    saveToDataStore(
                        _inUserModel.value!!
                    )
                  //  _response.value = body
                }else{
                    _loading.value = false
                    val stringJson = response.errorBody()!!.string()
                    try{
                        val json = JSONObject(stringJson)
                        Log.d(TAG, "onResponse: failed make jwtToken ${json.getString("message")}")
                       // _message.value = Event(json.getString("message"))
                        //_response.value = Response(error = true, message = json.getString("message"))
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailure(call: Call<JwtToken>, t: Throwable) {
                _loading.postValue(false)
                t.printStackTrace()
            }

        })

    }

    private fun saveToDataStore(inUserModel: InUserModel){
        viewModelScope.launch {
            datastore.setData(inUserModel)
        }
    }

    fun getCurrentUser(): FirebaseUser?{
        return currentUser.value
    }


}