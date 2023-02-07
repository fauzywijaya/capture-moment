package com.example.capturemoment.ui.home.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.data.source.remote.response.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel(){
    fun getNameUser() : Flow<String?> = authenticationRepository.getNameUser()
    fun getUserStories(token: String) : LiveData<PagingData<StoryEntity>> =
        storyRepository.getUserStories(token).cachedIn(viewModelScope).asLiveData()
}