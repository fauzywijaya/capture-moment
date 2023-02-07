package com.example.capturemoment.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.capturemoment.data.source.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg story: StoryEntity)

    @Query("SELECT * FROM tb_story")
    fun getUserStory() : PagingSource<Int, StoryEntity>

    @Query("DELETE FROM tb_story")
    fun deleteAll()
}