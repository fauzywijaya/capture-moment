package com.example.capturemoment.ui.authentication.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    suspend fun postUserLogin(email: String, password: String)=
        authenticationRepository.postUserLogin(email, password)


    fun saveUserToken(token: String) {
        viewModelScope.launch {
            authenticationRepository.saveUserToken(token)
        }
    }

    fun saveNameUser(name: String){
        viewModelScope.launch {
            authenticationRepository.saveNameUser(name)
        }
    }
}