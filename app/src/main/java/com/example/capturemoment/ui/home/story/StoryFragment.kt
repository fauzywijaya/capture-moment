package com.example.capturemoment.ui.home.story

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capturemoment.R
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.data.source.remote.response.Story
import com.example.capturemoment.databinding.FragmentStoryBinding
import com.example.capturemoment.ui.adapter.LoadingStateAdapter
import com.example.capturemoment.ui.adapter.StoryAdapter
import com.example.capturemoment.ui.customview.loadingVisible
import com.example.capturemoment.ui.home.HomeActivity
import com.example.capturemoment.ui.home.HomeActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.util.*

@ExperimentalPagingApi
@AndroidEntryPoint
class StoryFragment : Fragment() {

    private var _binding : FragmentStoryBinding ? =null
    private val binding get() = _binding


    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryAdapter

    private var token: String = ""
    private var hideNavView = false
    private val viewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = requireActivity().intent.getStringExtra(EXTRA_TOKEN) ?: ""

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentStoryBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView = requireActivity().findViewById<View>(R.id.bottom_nav_view)
        if (navView != null) {
            hideShowBottomNavigation(navView)
        }

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getNameUser().collect { nameUser ->
                    if (!nameUser.isNullOrEmpty()) {
                        binding?.apply {
                            tvWelcome.text = greatingMessage()
                            tvWelcomeName.text = nameUser
                        }
                    }
                }
            }
        }

        initRefreshLayout()
        initRecyclerView()
        initObserveStories()




    }


    private fun hideShowBottomNavigation(navView: View) {
        binding?.rvStory?.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val height = (navView.height + 45).toFloat()
            if (!hideNavView && scrollY > oldScrollY) {
                hideNavView = true

                ObjectAnimator.ofFloat(navView, "translationY", 0f, height).apply {
                    duration = 200
                    start()
                }
            }

            if (hideNavView && scrollY < oldScrollY) {
                hideNavView = false
                ObjectAnimator.ofFloat(navView, "translationY", height, 0f).apply {
                    duration = 200
                    start()
                }
            }
        }
    }



    private fun initObserveStories() {
        viewModel.getUserStories(token).observe(viewLifecycleOwner) {
            updateRecyclerView(it)
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoryAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                // List empty or error
                binding?.apply {
                    viewError.loadingVisible(true)
                    rvStory.loadingVisible(false)
                }
            } else {
                // List not empty
                binding?.apply {
                    viewError.loadingVisible(false)
                    rvStory.loadingVisible(true)
                }
            }

            // SwipeRefresh status based on LoadState
            binding?.viewRefresh?.isRefreshing = loadState.source.refresh is LoadState.Loading
        }
        try {
            recyclerView = binding?.rvStory!!
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (ex: NullPointerException){
            ex.printStackTrace()
        }

    }



    private fun updateRecyclerView(stories: PagingData<StoryEntity>) {

        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        listAdapter.submitData(lifecycle, stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun initRefreshLayout() {
        binding?.viewRefresh?.setOnRefreshListener {
            initObserveStories()
        }
    }

    private fun greatingMessage(): String{

        val calendar = Calendar.getInstance()

        return when (calendar.get(Calendar.HOUR_OF_DAY)){
            in 0..11 -> getString(R.string.good_morning)
            in 12..18 -> getString(R.string.good_afternoon)
            in 18..23 -> getString(R.string.good_evening)
            else -> "Hello"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}