package com.example.capturemoment.data

import android.util.Log
import com.example.capturemoment.data.source.local.PreferenceDataStore
import com.example.capturemoment.data.source.remote.network.ApiService
import com.example.capturemoment.data.source.remote.response.LogInResponse
import com.example.capturemoment.data.source.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferenceDataStore: PreferenceDataStore
) : AuthenticationDataSource {
    override suspend fun postUserLogin(
        email: String,
        password: String
    ): Flow<Result<LogInResponse>> = flow {
        try {
            val response = apiService.postUserLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun postUserRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.postUserRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun saveUserToken(token: String) {
        preferenceDataStore.saveUserToken(token)
    }

    override fun getUserToken(): Flow<String?> {
        return preferenceDataStore.getUserToken()
    }

    override suspend fun saveNameUser(name: String) {
        preferenceDataStore.saveNameUser(name)
    }

    override fun getNameUser(): Flow<String?> {
        return preferenceDataStore.getNameUser()
    }
}