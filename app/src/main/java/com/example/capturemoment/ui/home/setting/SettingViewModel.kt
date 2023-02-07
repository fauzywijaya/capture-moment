package com.example.capturemoment.ui.home.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
)  : ViewModel(){


    fun getNameUser() : Flow<String?> = authenticationRepository.getNameUser()
    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authenticationRepository.saveUserToken(token)
        }
    }
    fun saveNameUser(name: String) {
        viewModelScope.launch {
            authenticationRepository.saveNameUser(name)
        }
    }
}