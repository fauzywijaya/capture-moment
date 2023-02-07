package com.example.capturemoment.data

import androidx.paging.PagingData
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.data.source.remote.response.*
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryDataSource {


    fun getUserStories(
        token: String,
    ) : Flow<PagingData<StoryEntity>>

    fun getUserStoriesWithLocation(
        token: String
    ) : Flow<Result<StoryResponse>>

    suspend fun postUserStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) : Flow<Result<FileUploadResponse>>



    suspend fun saveTypeOfMap(typeMap: TypeMap)

    fun getTypeOfMap() : Flow<TypeMap>

    suspend fun saveStyleOfMap(styleMap: StyleMap)

    fun getStyleOfMap() : Flow<StyleMap>



}