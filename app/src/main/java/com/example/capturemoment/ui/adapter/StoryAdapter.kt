package com.example.capturemoment.ui.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.databinding.ItemRowStoryBinding
import com.example.capturemoment.ui.detail.DetailActivity
import com.example.capturemoment.ui.detail.DetailActivity.Companion.EXTRA_DETAIL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter :
    PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryEntity ) {
            with(binding) {
                tvNameUploader.text = data.name
                tvDescription.text = data.description
                tvdate.text = setDate(data.createdAt)
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = GONE
                            return false
                        }
                    })
                    .into(ivPhoto)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivPhoto, "image_photo"),
                            Pair(tvNameUploader, "name_uploader"),
                            Pair(tvdate, "date_upload"),
                            Pair(tvDescription, "description")
                        )

                    Intent(itemView.context, DetailActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, data)
                        itemView.context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }


    private fun setDate(timestamp: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = sdf.parse(timestamp) as Date

        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}