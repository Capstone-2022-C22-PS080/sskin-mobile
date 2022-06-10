package com.example.skindiseasedetectionapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.*
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
import java.lang.Exception
import java.util.*
import kotlin.math.log

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



    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()


    // [START sign_in_with_email]
    fun logIn(email: String, password: String){
        _loading.value = true
        var user : FirebaseUser? = null
        var userNew : InUserModel? = null
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        userNew = InUserModel(
                            userId = it.result.user?.uid,
                            )


                        //get token from api
                        val client = ApiConfig.getApiServce().getToken(Uid(userNew?.userId))
                        client.enqueue(object: Callback<JwtToken>{
                            override fun onResponse(
                                call: Call<JwtToken>,
                                response: Response<JwtToken>
                            ) {
                                val body = response.body()
                                if(body != null && response.isSuccessful){
                                    val token = body.jwtToken
                                    //read from fireStore
                                    val doc = db.collection("users").document(it.result.user!!.uid)
                                    doc.get().addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            userNew = task.result.toObject(InUserModel::class.java)
                                            userNew?.jwtToken = token
                                           // userNew?.profiles = null
//                                            saveToDataStore(body.jwtToken!!)
                                            Log.d(TAG, "onResponse: ${userNew?.profiles?.get(0)?.name}")
                                            saveUserToDataStore(userNew!!)
                                            _inUserModel.value = userNew
                                            Log.d(TAG, "getProfiles: ${userNew?.email}")
                                        }else{
                                            Log.d(TAG, "No such document")
                                            _inUserModel.value = null
                                            _messageAddFirebaseUser.value = Event("No Such Document")
                                        }
                                        _loading.value = false

                                    }.addOnSuccessListener {
                                        _loading.value = false
                                        _messageAddFirebaseUser.value = Event("Success get Data")

                                    }.addOnFailureListener { ex ->
                                        _loading.value = false
                                        _messageAddFirebaseUser.value = Event(ex.message!!)
                                        ex.printStackTrace()
                                    }

                                    //end read fireStore

                                }else{
                                    val stringJson = response.errorBody()!!.string()
                                    try{
                                        val json = JSONObject(stringJson)
                                        Log.d(TAG, "onResponse: failed make jwtToken ${json.getString("message")}")
                                    }catch (e: JSONException){
                                        e.printStackTrace()
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<JwtToken>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                            }

                        })
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LoginActivity.TAG, "signInWithEmail:failure", it.exception)
                        _messageLogin.setValue(Event(it.exception!!.message.toString()))
                    }
                    _loading.setValue(false)
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    _inUserModel.value = null
                    _messageLogin.value = Event(it.message!!)
                    _loading.setValue(false)
                }
                .addOnSuccessListener {
                    if(it != null){
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(LoginActivity.TAG, "signInWithEmail:success")
                        _messageLogin.value = Event("Login Successfully!")
                        user = it.user
                        _currentUser.value = user!!
                    }

                }

        }

    }



    fun saveUserToDataStore(inUserModel: InUserModel){
        if(inUserModel.userId != null){
            viewModelScope.launch{
                datastore.setData(userId = inUserModel.userId!!, email = inUserModel.email!!, name = inUserModel.name!!,inUserModel.default_profile!!,inUserModel.photo_profile!!,inUserModel.jwtToken!!)
            }
        }

    }


}