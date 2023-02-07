package com.example.capturemoment.ui.splash

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    fun getAuthToken(): Flow<String?> = authenticationRepository.getUserToken()

    fun getNameUser() : Flow<String?> = authenticationRepository.getNameUser()
}