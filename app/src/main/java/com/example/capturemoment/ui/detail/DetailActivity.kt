package com.example.capturemoment.ui.detail

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.capturemoment.R
import com.example.capturemoment.data.source.local.entity.StoryEntity
import com.example.capturemoment.databinding.ActivityDetailBinding
import com.example.capturemoment.ui.customview.loadingVisible
import com.example.capturemoment.utils.Helpers

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportPostponeEnterTransition()
        val data = intent.getParcelableExtra<StoryEntity>(EXTRA_DETAIL)

        if (data != null) {
            binding.apply {
                textView.text = getString(R.string.detail_title, data.name)
                tvNameUploader.text = data.name
                tvDateUpload.text = Helpers.setDate(data.createdAt)
                tvDetailDescription.text = data.description
                Glide
                    .with(this@DetailActivity)
                    .load(data.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }
                    })
                    .into(ivImage)
            }
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }


    companion object{
        const val EXTRA_DETAIL = "extra_detail"
    }
}