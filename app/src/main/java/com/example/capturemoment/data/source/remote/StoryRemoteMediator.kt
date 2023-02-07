package com.example.capturemoment.data.source.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.capturemoment.data.source.local.entity.RemoteKeysEntity
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.data.source.local.room.StoryDatabase
import com.example.capturemoment.data.source.remote.network.ApiService
import com.example.capturemoment.utils.wrapEspressoIdlingResource
import java.lang.Exception

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryEntity>(){


    override suspend fun load(loadType: LoadType, state: PagingState<Int, StoryEntity>): MediatorResult {

        // Determine page value based on LoadType
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                remoteKey?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKey?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeysForLastItem(state)
                val nextKey = remoteKey?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKey != null
                )
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val responseData = apiService.getUserStories(token, page, state.config.pageSize)
                val endOfPaginationReached = responseData.stories.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.stories.map {
                        RemoteKeysEntity(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    // Save RemoteKeysEntity information to database
                    database.remoteKeysDao().insertAll(keys)

                    responseData.stories.forEach { stories ->
                        val story = StoryEntity(
                            stories.id,
                            stories.name,
                            stories.description,
                            stories.createdAt,
                            stories.photoUrl,
                            stories.lon,
                            stories.lat
                        )

                        // Save StoryEntity to the local database
                        database.storyDao().insertStory(story)
                    }
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    /**
     * Get RemoteKeysEntity for last item from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    /**
     * Get RemoteKeysEntity for first item from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    /**
     * Get RemoteKeysEntity for closest to current position from local database
     *
     * @param state PagingState
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}