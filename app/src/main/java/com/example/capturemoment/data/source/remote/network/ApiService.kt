package com.example.capturemoment.data.source.remote.network

import com.example.capturemoment.data.source.remote.response.FileUploadResponse
import com.example.capturemoment.data.source.remote.response.LogInResponse
import com.example.capturemoment.data.source.remote.response.RegisterResponse
import com.example.capturemoment.data.source.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun postUserLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LogInResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun postUserRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @GET("stories")
    suspend fun getUserStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ) : StoryResponse


    @Multipart
    @POST("stories")
    suspend fun postUserStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): FileUploadResponse


}