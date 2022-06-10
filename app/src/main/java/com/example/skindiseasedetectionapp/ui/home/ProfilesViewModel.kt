package com.example.skindiseasedetectionapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ProfilesUser
import com.example.skindiseasedetectionapp.utill.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfilesViewModel : ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _loadingAdd: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loadingAdd: LiveData<Boolean> = _loadingAdd

    private val _inUserModel : MutableLiveData<InUserModel?> = MutableLiveData<InUserModel?>()
    val inUserModel: LiveData<InUserModel?> = _inUserModel


    private val _messageAdd: MutableLiveData<Event<String>> = MutableLiveData<Event<String>>()
    val messageAdd: LiveData<Event<String>> = _messageAdd

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _profiles : MutableLiveData<ProfilesUser> = MutableLiveData<ProfilesUser>()
    val profile : LiveData<ProfilesUser> = _profiles

    fun addProfile(docId: String,profilesUser: ProfilesUser){
        _loadingAdd.value = true
        viewModelScope.launch {

            val collections = db.collection("users")
            val doc = collections.document(docId)

            doc.update("profiles", FieldValue.arrayUnion(profilesUser)).addOnCompleteListener{ task ->
                if(task.isSuccessful){


                }else{
                    Log.d(ProfilesActivity.TAG, "No such document")
                    _messageAdd.value = Event(task.exception?.message!!)
                }
                _loadingAdd.value = false

            }.addOnSuccessListener {
                _loadingAdd.value = false
                _messageAdd.value = Event("Success add Data")

            }.addOnFailureListener { ex ->
                _loading.value = false
                _messageAdd.value = Event(ex.message!!)
                ex.printStackTrace()
            }

        }
    }

    fun getId(docId: String) : Int{
        var id = 0
        val collections = db.collection("users")
        val doc = collections.document(docId)
        doc.get().addOnCompleteListener {
            if(it.isSuccessful){
                val data = it.result.toObject(InUserModel::class.java)
                val profiles = data?.profiles
                id = profiles?.get(profiles.size-1)?.id!! + 1
            }else{
                id -1
            }
        }.addOnSuccessListener {
            val data = it.toObject(InUserModel::class.java)
            val profiles = data?.profiles
            id = profiles?.get(profiles.size-1)?.id!! + 1
        }.addOnFailureListener {ex ->
            id = -1
            ex.printStackTrace()
        }
        return id
    }

    fun setProfiles(docId: String){
        _loading.value = true
            val doc = db.collection("users").document(docId)
            doc.get().addOnCompleteListener { task ->
                if(task.isSuccessful && task.result != null){
                    val userNew =  task.result.toObject(InUserModel::class.java)
                    _inUserModel.value = userNew!!
                    Log.d(ProfilesActivity.TAG, "getProfiles: ${userNew.profiles?.size}")
                }else{
                    Log.d(ProfilesActivity.TAG, "No such document")
                    _inUserModel.value = null
                }
                _loading.value = false
            }.addOnSuccessListener {
                _loading.value = false

            }.addOnFailureListener { ex ->
                _inUserModel.value = null
                ex.printStackTrace()
                _loading.value = false
            }

    }






}