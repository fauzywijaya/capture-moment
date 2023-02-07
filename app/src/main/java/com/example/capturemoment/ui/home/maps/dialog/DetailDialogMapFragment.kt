package com.example.capturemoment.ui.home.maps.dialog

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.capturemoment.databinding.FragmentDetailDialogMapBinding
import com.example.capturemoment.utils.Helpers
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailDialogMapFragment : BottomSheetDialogFragment() {


    private var _binding : FragmentDetailDialogMapBinding? = null
    private val binding get() = _binding as FragmentDetailDialogMapBinding
    private val args: DetailDialogMapFragmentArgs by navArgs()
    private lateinit var name: String
    private lateinit var date: String
    private lateinit var image: String
    private lateinit var desc: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = args.name
        date = args.date
        image = args.image
        desc = args.desc
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDialogMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutDetail.apply {
            tvNameUploader.text = name
            tvDescription.text = desc
            tvdate.text = Helpers.setDate(date)
        }

        Glide.with(requireActivity())
            .load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.layoutDetail.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.layoutDetail.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.layoutDetail.ivPhoto)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        name = ""
        desc = ""
        image = ""
        date = ""
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
        name = ""
        desc = ""
        image = ""
        date = ""
    }

}