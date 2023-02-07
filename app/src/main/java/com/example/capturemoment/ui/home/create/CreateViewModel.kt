package com.example.capturemoment.ui.home.create

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.data.AuthenticationRepository
import com.example.capturemoment.data.StoryRepository
import com.example.capturemoment.data.source.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {


    fun getUserToken(): Flow<String?> = authenticationRepository.getUserToken()

    suspend fun postUserStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        long: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepository.postUserStories(token, file, description, lat, long)
}