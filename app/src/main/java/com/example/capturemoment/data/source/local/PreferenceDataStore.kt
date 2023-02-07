package com.example.capturemoment.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type
import javax.inject.Inject

class PreferenceDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    fun getUserToken(): Flow<String?> = dataStore.data.map {
            it[USER_TOKEN_KEY]
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit {
            it[USER_TOKEN_KEY] = token
        }
    }

    fun getNameUser(): Flow<String?> = dataStore.data.map {
        it[USER_NAME_KEY] ?: DEFAULT_VALUE
    }

    suspend fun saveNameUser(name: String) {
        dataStore.edit {
            it[USER_NAME_KEY] = name
        }
    }

    fun getTypeOfMap() : Flow<TypeMap> = dataStore.data.map {
        when(it[TYPE_MAP_KEY]){
            TypeMap.NORMAL.name -> TypeMap.NORMAL
            TypeMap.SATELLITE.name -> TypeMap.SATELLITE
            TypeMap.TERRAIN.name -> TypeMap.TERRAIN
            else -> TypeMap.NORMAL
        }
    }

    suspend fun saveTypeOfMap(typeMap: TypeMap){
        dataStore.edit {
            it[TYPE_MAP_KEY] = when(typeMap) {
                TypeMap.NORMAL -> TypeMap.NORMAL.name
                TypeMap.SATELLITE -> TypeMap.SATELLITE.name
                TypeMap.TERRAIN -> TypeMap.TERRAIN.name
            }
        }
    }

    fun getStyleOfMap() : Flow<StyleMap> = dataStore.data.map {
        when(it[STYLE_MAP_KEY]){
            StyleMap.NORMAL.name -> StyleMap.NORMAL
            StyleMap.NIGHT.name -> StyleMap.NIGHT
            StyleMap.SILVER.name -> StyleMap.SILVER
            else -> StyleMap.NORMAL
        }
    }

    suspend fun saveStyleOfMap(styleMap: StyleMap) {
        dataStore.edit {
            it[STYLE_MAP_KEY] = when(styleMap) {
                StyleMap.NORMAL -> StyleMap.NORMAL.name
                StyleMap.NIGHT -> StyleMap.NIGHT.name
                StyleMap.SILVER -> StyleMap.SILVER.name
            }
        }
    }



    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val TYPE_MAP_KEY = stringPreferencesKey("type_map")
        private val STYLE_MAP_KEY = stringPreferencesKey("style_map")
        const val DEFAULT_VALUE = "not_set"
    }

}