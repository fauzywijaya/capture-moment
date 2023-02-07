package com.example.capturemoment.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tb_story")
data class StoryEntity (
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String,

    @ColumnInfo("created_at")
    val createdAt: String,

    @ColumnInfo("photo_url")
    val photoUrl: String,

    val lon: Double?,

    val lat: Double?
 ) : Parcelable