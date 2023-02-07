package com.example.capturemoment.ui.home.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import com.example.capturemoment.data.source.remote.response.StoryResponse
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authenticationRepository: AuthenticationRepository
)  : ViewModel(){
    
    fun getUserStoriesWithLocation(token: String) : Flow<Result<StoryResponse>> =
        storyRepository.getUserStoriesWithLocation(token)


    fun getMapType() : Flow<TypeMap> = storyRepository.getTypeOfMap()

    fun saveMapType(TypeMap: TypeMap) {
        viewModelScope.launch {
            storyRepository.saveTypeOfMap(TypeMap)
        }
    }


    fun getUserToken() : Flow<String?> = authenticationRepository.getUserToken()

    fun getMapStyle() : Flow<StyleMap> = storyRepository.getStyleOfMap()

    fun saveMapStyle(mapStyle: StyleMap) {
        viewModelScope.launch {
            storyRepository.saveStyleOfMap(mapStyle)
        }
    }
}