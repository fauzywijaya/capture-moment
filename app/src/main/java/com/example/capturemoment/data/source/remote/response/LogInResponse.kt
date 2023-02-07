package com.example.capturemoment.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LogInResponse (
    @field:SerializedName("loginResult")
    val login: LogIn? = null,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
        )