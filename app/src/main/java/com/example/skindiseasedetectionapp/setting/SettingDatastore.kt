package com.example.skindiseasedetectionapp.setting

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.skindiseasedetectionapp.setting.SettingDatastore.PreferencesKeys.TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class SettingDatastore private constructor(private val dataStore: DataStore<Preferences>) {

    private val JWT_TOKEN = stringPreferencesKey("jwt_token")



    val readFromDataStore : Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("error", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences ->

            val jwtToken = preferences[TOKEN] ?: "-token-"
            jwtToken
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

    suspend fun setData(token: String){
            dataStore.edit {
                it[TOKEN] = token
            }


    }

    private object  PreferencesKeys{
        val TOKEN = stringPreferencesKey("token")
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