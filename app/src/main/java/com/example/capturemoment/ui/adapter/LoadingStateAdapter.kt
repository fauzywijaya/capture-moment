package com.example.capturemoment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.capturemoment.R
import com.example.capturemoment.databinding.ItemLayoutLoadingBinding

class LoadingStateAdapter(private val retry : () -> Unit) : LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {
    class ViewHolder(
        private val binding: ItemLayoutLoadingBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.tvTimeout.text =
                    binding.root.context.getString(R.string.error_content)
            }

            binding.spinKit.isVisible = loadState is LoadState.Loading
            binding.btRetry.isVisible = loadState is LoadState.Error
            binding.tvTimeout.isVisible = loadState is LoadState.Error
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
       holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolder {
        val binding = ItemLayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, retry)
    }
}