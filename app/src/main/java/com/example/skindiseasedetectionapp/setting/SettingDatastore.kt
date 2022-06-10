package com.example.skindiseasedetectionapp.setting

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.skindiseasedetectionapp.model.InUserModel
import com.example.skindiseasedetectionapp.model.ProfilesUser
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.DEFAULT
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.EMAIL
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.NAME
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.PHOTO_URL
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.TOKEN
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.UID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.Serializable


class SettingDatastore private constructor(private val dataStore: DataStore<Preferences>) {

    val readUserFromDataStore : Flow<InUserModel> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("error", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences ->
            val email = preferences[EMAIL] ?: ""
            val name = preferences[NAME] ?: ""
            val userId = preferences[UID] ?: ""
            val default = preferences[DEFAULT] ?: 0
            val jwtToken = preferences[TOKEN] ?: ""
            val photo = preferences[PHOTO_URL] ?: ""
            InUserModel(
                userId = userId,
                email = email,
                name = name,
                default_profile = default,
                photo,
                jwtToken,
                null
            )

        }

    fun getDataToken() : Flow<String>{
        return dataStore.data.map {
            it[TOKEN] ?: "0"
        }
    }

    suspend fun saveData(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun deleteData() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun setData(
        userId: String,
        email: String,
        name: String,
        default_profile: Int,
        photo_profile: String,
        jwtToken: String
    ){

        dataStore.edit {
            it[TOKEN] = jwtToken
            it[EMAIL] = email
            it[DEFAULT] = default_profile
            it[NAME] = name
            it[PHOTO_URL] = photo_profile
            it[UID] = userId


        }
    }

    private object  PreferencesKeys{
        val TOKEN = stringPreferencesKey("token")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val DEFAULT = intPreferencesKey("default")
        val PHOTO_URL = stringPreferencesKey("photoUrl")
        val UID = stringPreferencesKey("uid")
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