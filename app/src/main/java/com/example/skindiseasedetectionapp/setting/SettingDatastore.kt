package com.example.skindiseasedetectionapp.setting

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.skindiseasedetectionapp.model.InUserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class SettingDatastore private constructor(private val dataStore: DataStore<Preferences>) {

    private val ID = stringPreferencesKey("userId")
    private val NAME = stringPreferencesKey("name")
    private val EMAIL = stringPreferencesKey("email")
    private val JWT_TOKEN = stringPreferencesKey("jwt_token")
    private val PHOTO_PROFILE = stringPreferencesKey("photo_profile")
    private val DEFAULT_PROFILE = intPreferencesKey("default_profile")


    fun getData(): Flow<InUserModel> {
        return dataStore.data.map { preferences ->
            val id = preferences[ID] ?: ""
            val email = preferences[EMAIL] ?: ""
            val name = preferences[NAME] ?: ""
            val default = preferences[DEFAULT_PROFILE] ?: 1
            val photo = preferences[PHOTO_PROFILE] ?: ""
            val jwtToken = preferences[JWT_TOKEN] ?: ""
            InUserModel(id,email,name,default,photo,jwtToken,null)
        }
    }

    val readFromDataStore : Flow<InUserModel> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("error", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences ->
            val id = preferences[ID] ?: ""
            val email = preferences[EMAIL] ?: ""
            val name = preferences[NAME] ?: ""
            val default = preferences[DEFAULT_PROFILE] ?: 1
            val photo = preferences[PHOTO_PROFILE] ?: ""
            val jwtToken = preferences[JWT_TOKEN] ?: ""
            InUserModel(id,email,name,default,photo,jwtToken,null)
        }


    suspend fun deleteData() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun setData(inUserModel: InUserModel){
        dataStore.edit {
            it[ID] = inUserModel.userId!!
            it[NAME] = inUserModel.name!!
            it[JWT_TOKEN] = inUserModel.jwtToken!!
            it[DEFAULT_PROFILE] = inUserModel.default_profile!!
            it[EMAIL] = inUserModel.email!!
            it[PHOTO_PROFILE] = inUserModel.photo_profile!!
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: SettingDatastore? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingDatastore {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingDatastore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}