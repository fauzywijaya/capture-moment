package com.example.capturemoment.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.capturemoment.data.source.local.PreferenceDataStore
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.data.source.local.room.StoryDatabase
import com.example.capturemoment.data.source.remote.StoryRemoteMediator
import com.example.capturemoment.data.source.remote.network.ApiService
import com.example.capturemoment.data.source.remote.response.FileUploadResponse
import com.example.capturemoment.data.source.remote.response.StoryResponse
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferencesDataSource: PreferenceDataStore,
    private val storyDatabase: StoryDatabase
) : StoryDataSource {

    override fun getUserStories(
        token: String,
    ): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiService,
                "Bearer $token"
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getUserStory()
            }
        ).flow
    }


    override fun getUserStoriesWithLocation(
        token: String
    ): Flow<Result<StoryResponse>> = flow {
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.getUserStories(bearerToken, size = 30, location = 1)
            emit(Result.success(response))
        } catch (e : Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun postUserStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.postUserStory(bearerToken, file, description, lat, lon)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }



    override suspend fun saveTypeOfMap(typeMap: TypeMap) {
        preferencesDataSource.saveTypeOfMap(typeMap)
    }

    override fun getTypeOfMap(): Flow<TypeMap> {
        return preferencesDataSource.getTypeOfMap()
    }

    override suspend fun saveStyleOfMap(styleMap: StyleMap) {
        preferencesDataSource.saveStyleOfMap(styleMap)
    }

    override fun getStyleOfMap(): Flow<StyleMap> {
        return preferencesDataSource.getStyleOfMap()
    }

}