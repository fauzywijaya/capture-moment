package com.example.capturemoment.data

import com.example.capturemoment.data.source.remote.response.LogInResponse
import com.example.capturemoment.data.source.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface AuthenticationDataSource {
    suspend fun postUserLogin(
        email: String,
        password: String,
    ) : Flow<Result<LogInResponse>>

    suspend fun postUserRegister(
        name: String,
        email: String,
        password: String,
    ) : Flow<Result<RegisterResponse>>

    suspend fun saveUserToken(token: String)

    fun getUserToken(): Flow<String?>

    suspend fun saveNameUser(name: String)

    fun getNameUser(): Flow<String?>
}