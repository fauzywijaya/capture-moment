package com.example.capturemoment.ui.authentication.signup

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel(){

    suspend fun postUserRegister(name: String, email: String, password: String) =
        authenticationRepository.postUserRegister(name, email, password)

}