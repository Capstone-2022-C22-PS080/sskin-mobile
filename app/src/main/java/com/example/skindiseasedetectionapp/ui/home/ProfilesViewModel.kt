package com.example.skindiseasedetectionapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ProfilesUser
import com.example.skindiseasedetectionapp.ui.dashboard.DashboardActivity
import com.example.skindiseasedetectionapp.utill.Event
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.util.*

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

    private val _profiles : MutableLiveData<List<ProfilesUser>> = MutableLiveData<List<ProfilesUser>>()
    val profile : MutableLiveData<List<ProfilesUser>> = _profiles

    fun addProfile(docId: String,name: String){
        _loadingAdd.value = true
        viewModelScope.launch {

            var id = 0
            val collections = db.collection("users")
            val doc = collections.document(docId)
            doc.get().addOnCompleteListener {
                if(it.isSuccessful){
                    val data = it.result.toObject(InUserModel::class.java)
                    if(data != null && data.profiles != null){

                        //add user
                        val profiles = data.profiles
                        val size = profiles?.size!!
                        id = profiles[size-1].id!! + 1

                        val collections2 = db.collection("users")
                        val doc2 = collections2.document(docId)
                        val newProfile = ProfilesUser(
                            fotoUrl = "",
                            id = id,
                            name = name,
                            Timestamp(Date())
                        )

                        doc2.update("profiles", FieldValue.arrayUnion(newProfile)).addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                _messageAdd.value = Event("Success add Data")
                                _loading.value = false
                                setProfiles(docId)
                            }else{
                                _messageAdd.value = Event(task.exception?.message!!)
                                task.exception!!.printStackTrace()
                            }

                        }

                        // end add user

                    }

                }else{
                    _loading.value = false

                }
            }.addOnFailureListener {ex ->
                ex.printStackTrace()
                _loading.value = false
            }



        }
    }

    fun getId(docId: String) {


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

    fun deleteProfileById(idProfile: Int,docId: String){
        _loadingAdd.value = true
        val coll = db.collection("users")
        if(docId != null){
            db.collection("users").document(docId)
                .get()
                .addOnFailureListener {  ex->
                    _inUserModel.value = null
                }
                .addOnSuccessListener {  doc ->
                    if(doc != null){
                        val user = doc.toObject(InUserModel::class.java)
                        if(user != null){
                            val listProfiles = user.profiles
                            if(listProfiles != null){
                                val index = -1;

                                val theProfile = listProfiles.filter {
                                    it.id == idProfile
                                } .single()

                                db.collection("users").document(docId)
                                    .update("profiles",FieldValue.arrayRemove(theProfile))
                                    .addOnFailureListener { ex2->
                                        _loading.value = false
                                        ex2.printStackTrace()
                                        _messageAdd.value = Event("Failed Deleting data - ${ex2.message}")
                                    }.addOnSuccessListener { it2 ->
                                        _messageAdd.value = Event("Success Deleting Data ")
                                        setProfiles(docId)
                                        _loading.value = false
                                    }


                            }
                            _inUserModel.value = user
                        }

                    }
                    _inUserModel.value = null
                }

        }
    }


    fun editProfile(idProfile: Int,docId: String,name: String){
        _loadingAdd.value = true
        val coll = db.collection("users")
        if(docId != null){
            db.collection("users").document(docId)
                .get()
                .addOnFailureListener {  ex->
                    _inUserModel.value = null
                }
                .addOnSuccessListener {  doc ->
                    if(doc != null){
                        val user = doc.toObject(InUserModel::class.java)
                        if(user != null){
                            val listProfiles = user.profiles
                            if(listProfiles != null){
                                val index = -1;

                                val theProfile = listProfiles.filter {
                                    it.id == idProfile
                                } .single()

                                db.collection("users").document(docId)
                                    .update("profiles",FieldValue.arrayRemove(theProfile))
                                    .addOnFailureListener { ex2->
                                        ex2.printStackTrace()
                                    }.addOnSuccessListener { it2 ->
                                        theProfile.name = name
                                        db.collection("users")
                                            .document(docId)
                                            .update("profiles",FieldValue.arrayUnion(theProfile))
                                            .addOnSuccessListener { success->
                                                _messageAdd.value = Event("Success Update Data $docId ")
                                                setProfiles(docId)
                                            }.addOnFailureListener { ex3->
                                                ex3.printStackTrace()
                                                _loading.value = false
                                                _messageAdd.value = Event("Failed Update Data - ${ex3.message}")
                                            }
                                    }


                            }
                            _inUserModel.value = user
                        }
                    }
                    _inUserModel.value = null
                }

        }
    }





}