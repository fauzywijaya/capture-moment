package com.example.capturemoment.ui.home.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentMapsBinding
import com.example.capturemoment.ui.home.HomeActivity
import com.example.capturemoment.ui.home.HomeActivity.Companion.EXTRA_TOKEN
import com.example.capturemoment.ui.maps.StyleMap
import com.example.capturemoment.ui.maps.TypeMap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding as FragmentMapsBinding

    private var token: String = ""
    private val viewModel: MapViewModel by viewModels()

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getMyDeviceLocation()
            }
        }

    private val callback = OnMapReadyCallback { googleMap ->

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mGoogleMap = googleMap
        mGoogleMap.setPadding(0, 160, 0, 0)
        mGoogleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isIndoorLevelPickerEnabled = true
        }
        getMyDeviceLocation()
        getStyleMap()
        getTypeMap()
        markStoryLocation()
        mGoogleMap.setOnMarkerClickListener {
            markerClickListener(it)
            true
        }
    }

    private fun getMyDeviceLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        token = requireActivity().intent.getStringExtra(EXTRA_TOKEN) ?: ""
        binding.apply {
            toolbar.inflateMenu(R.menu.style_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.style_map -> {
                        findNavController().navigate(R.id.action_navigation_maps_to_optionMapFragment2)
                    }
                }
                true
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun setTypeMap(mMap: GoogleMap, mapType: TypeMap) {
        when (mapType) {
            TypeMap.NORMAL -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            TypeMap.SATELLITE -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            TypeMap.TERRAIN -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
        }
    }

    private fun setMapStyle(mMap: GoogleMap, mapStyle: StyleMap) {
        try {
            val success = when (mapStyle) {
                StyleMap.NORMAL -> mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style_normal
                    )
                )
                StyleMap.NIGHT -> mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style_night
                    )
                )
                StyleMap.SILVER -> mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style_silver
                    )
                )
            }
            if (!success) {
                Log.e("MapsFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsFragment", "Can't find style. Error: ", e)
        }
    }

    private fun getTypeMap() {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getMapType().collect { typeMap ->
                    when (typeMap) {
                        TypeMap.NORMAL -> setTypeMap(mGoogleMap, TypeMap.NORMAL)
                        TypeMap.SATELLITE -> setTypeMap(mGoogleMap, TypeMap.SATELLITE)
                        TypeMap.TERRAIN -> setTypeMap(mGoogleMap, TypeMap.TERRAIN)
                    }
                }
            }
        }
    }

    private fun getStyleMap() {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getMapStyle().collect { typeMap ->
                    when (typeMap) {
                        StyleMap.NORMAL -> setMapStyle(mGoogleMap, StyleMap.NORMAL)
                        StyleMap.NIGHT -> setMapStyle(mGoogleMap, StyleMap.NIGHT)
                        StyleMap.SILVER -> setMapStyle(mGoogleMap, StyleMap.SILVER)
                    }
                }
            }
        }
    }

    private fun markStoryLocation() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getUserStoriesWithLocation(token).collect { result ->
                    result.onSuccess { response ->
                        response.stories.forEach { story ->

                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)
                                mGoogleMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet("Lat: ${story.lat}, Lon: ${story.lon}")
                                )

                            }
                        }
                    }
                }
            }
        }
    }

    private fun markerClickListener(marker: Marker) {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getUserStoriesWithLocation(token).collect { result ->
                    result.onSuccess { response ->
                        response.stories.find { find ->
                            val latLng = LatLng(find.lat!!, find.lon!!)
                            latLng == marker.position
                        }?.let { story ->
                            val latLng = LatLng(story.lat!!, story.lon!!)
                            val detailDialog =
                                MapsFragmentDirections.actionNavigationMapsToDetailDialogMapFragment2(
                                    story.name,
                                    story.createdAt,
                                    story.photoUrl,
                                    story.description
                                )
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                            findNavController().apply {
                                navigate(detailDialog)
                            }
                        }
                    }
                }
            }
        }
    }

}
