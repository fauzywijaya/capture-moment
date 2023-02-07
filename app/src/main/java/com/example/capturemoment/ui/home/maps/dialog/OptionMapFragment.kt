package com.example.capturemoment.ui.home.maps.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentOptionMapBinding
import com.example.capturemoment.ui.home.maps.MapViewModel
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class OptionMapFragment : BottomSheetDialogFragment() {


    private var _binding : FragmentOptionMapBinding ? = null
    private val binding get() = _binding as FragmentOptionMapBinding
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentOptionMapBinding.inflate(layoutInflater, container, false )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListener()
        initObserver()
        hideMapStyleGroup(false)
    }

    private fun hideMapStyleGroup(isHide: Boolean) {
        if (isHide) {
            binding.mapStyleGroup.visibility = View.GONE
        } else {
            binding.mapStyleGroup.visibility = View.VISIBLE
        }
    }

    private fun initObserver() {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getMapStyle().collect { typeMap ->
                    when (typeMap) {
                        StyleMap.NORMAL -> {
                            highlightMapTypeSwitcher(TypeMap.NORMAL)
                            hideMapStyleGroup(false)
                        }
                        StyleMap.NIGHT -> highlightMapTypeSwitcher(TypeMap.NORMAL)
                        StyleMap.SILVER -> highlightMapTypeSwitcher(TypeMap.NORMAL)
                    }
                }

                viewModel.getMapType().collect { typeMap ->
                    when (typeMap) {
                        TypeMap.NORMAL -> highlightMapStyleSwitcher(StyleMap.NORMAL)
                        TypeMap.SATELLITE ->highlightMapStyleSwitcher(StyleMap.NORMAL)
                        TypeMap.TERRAIN -> highlightMapStyleSwitcher(StyleMap.NORMAL)
                    }
                }
            }
        }
    }

    private fun onClickListener(){
        binding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            ivMapDefault.setOnClickListener {
                viewModel.saveMapType(TypeMap.NORMAL)
                Toast.makeText(context, getString(R.string.map_normal), Toast.LENGTH_SHORT).show()
                dismiss()
            }
            ivMapSatellite.setOnClickListener {
                viewModel.saveMapType(TypeMap.SATELLITE)
                Toast.makeText(context, getString(R.string.map_satellite), Toast.LENGTH_SHORT).show()
                dismiss()
            }
            ivMapTerrain.setOnClickListener {
                viewModel.saveMapType(TypeMap.TERRAIN)
                Toast.makeText(context, getString(R.string.map_terrain), Toast.LENGTH_SHORT).show()
                dismiss()
            }


            ivMapStyleDefault.setOnClickListener {
                viewModel.saveMapStyle(StyleMap.NORMAL)
                Toast.makeText(context, getString(R.string.map_normal), Toast.LENGTH_SHORT).show()
                dismiss()
            }
            ivMapStyleNight.setOnClickListener {
                viewModel.saveMapStyle(StyleMap.NIGHT)
                Toast.makeText(context, getString(R.string.map_night), Toast.LENGTH_SHORT).show()
                dismiss()
            }
            ivMapStyleSilver.setOnClickListener {
                viewModel.saveMapStyle(StyleMap.SILVER)
                Toast.makeText(context, getString(R.string.map_silver), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun highlightMapTypeSwitcher(type: TypeMap) {
        when (type) {
            TypeMap.NORMAL -> {
                // Normal
                binding.ivMapDefault.setPadding(1, 1, 1, 1)
                binding.tvMapDefault.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))

                // Satellite
                binding.ivMapSatellite.setPadding(0, 0, 0, 0)
                binding.tvMapSatellite.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Terrain
                binding.ivMapTerrain.setPadding(0, 0, 0, 0)
                binding.tvMapTerrain.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))
            }
            TypeMap.SATELLITE -> {
                // Normal
                binding.ivMapDefault.setPadding(0, 0, 0, 0)
                binding.tvMapDefault.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Satellite
                binding.ivMapSatellite.setPadding(1, 1, 1, 1)
                binding.tvMapSatellite.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))

                // Terrain
                binding.ivMapTerrain.setPadding(0, 0, 0, 0)
                binding.tvMapTerrain.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))
            }
            TypeMap.TERRAIN -> {
                // Normal
                binding.ivMapDefault.setPadding(0, 0, 0, 0)
                binding.tvMapDefault.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Satellite
                binding.ivMapSatellite.setPadding(0, 0, 0, 0)
                binding.tvMapSatellite.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Terrain
                binding.ivMapTerrain.setPadding(1, 1, 1, 1)
                binding.tvMapTerrain.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))
            }
        }
    }

    private fun highlightMapStyleSwitcher(style: StyleMap) {
        when (style) {
            StyleMap.NORMAL -> {
                // Normal
                binding.ivMapStyleDefault.setPadding(1, 1, 1, 1)
                binding.tvMapStyleNormal.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))

                // Night
                binding.ivMapStyleNight.setPadding(0, 0, 0, 0)
                binding.tvMapStyleNight.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Silver
                binding.ivMapStyleSilver.setPadding(0, 0, 0, 0)
                binding.tvMapStyleSilver.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))
            }
            StyleMap.NIGHT -> {
                // Normal
                binding.ivMapStyleDefault.setPadding(0, 0, 0, 0)
                binding.tvMapStyleNormal.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Night
                binding.ivMapStyleNight.setPadding(1, 1, 1, 1)
                binding.tvMapStyleNight.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))

                // Silver
                binding.ivMapStyleSilver.setPadding(0, 0, 0, 0)
                binding.tvMapStyleSilver.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))
            }
            StyleMap.SILVER -> {
                // Normal
                binding.ivMapStyleDefault.setPadding(0, 0, 0, 0)
                binding.tvMapStyleNormal.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Night
                binding.ivMapStyleNight.setPadding(0, 0, 0, 0)
                binding.tvMapStyleNight.setTextColor(ContextCompat.getColor(requireContext() ,R.color.dark_blue_200))

                // Terrain
                binding.ivMapStyleSilver.setPadding(1, 1, 1, 1)
                binding.tvMapStyleSilver.setTextColor(ContextCompat.getColor(requireContext() ,R.color.blue_primary))
            }
        }
    }


}