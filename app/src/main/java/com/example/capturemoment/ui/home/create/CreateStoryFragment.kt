package com.example.capturemoment.ui.home.create

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentCreateStoryBinding
import com.example.capturemoment.ui.customview.loadingVisible
import com.example.capturemoment.utils.MediaUtils.reduceFileImage
import com.example.capturemoment.utils.MediaUtils.rotateBitmap
import com.example.capturemoment.utils.MediaUtils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.util.jar.Manifest

@ExperimentalPagingApi
@AndroidEntryPoint
class CreateStoryFragment : Fragment() {

    private var _binding : FragmentCreateStoryBinding ?= null
    private val binding get() =  _binding as FragmentCreateStoryBinding

    private lateinit var navView: View
    private var getFile: File? = null
    private lateinit var result: Bitmap
    private var myLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var token: String = ""
    private val viewModel: CreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navView = requireActivity().findViewById(R.id.bottom_nav_view)
        navView.visibility = View.GONE

        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(requireActivity())

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getUserToken().collect { userToken ->
                    if (!userToken.isNullOrEmpty()) token = userToken
                }
            }
        }

        binding.ivBack.setOnClickListener {
            val action = CreateStoryFragmentDirections.actionNavigationCreateToNavigationStory()
            it.findNavController().navigate(action)
        }

        binding.btnCamera.setOnClickListener { startIntentCameraX() }
        binding.btnGallery.setOnClickListener { startIntentGallery() }
        binding.btnUpload.setOnClickListener { uploadStoryAction() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            } else {
                this.myLocation = null
            }
        }

    }

    private fun uploadStoryAction(){
        setLoadingState(true)

        val etDescription = binding.etDescription
        var isValid = true

        if (etDescription.text.toString().isBlank()) {
            etDescription.error = getString(R.string.empty_description)
            isValid = false
        }

        if (getFile == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.empty_image),
                Toast.LENGTH_SHORT
            ).show()

            isValid = false
        }

        if (isValid) {

            val file = reduceFileImage(getFile as File)
            val description =
                etDescription.text.toString().toRequestBody("text/plain".toMediaType())

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (myLocation != null){
                lat = myLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon = myLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())

            }

            lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.postUserStories(token, imageMultipart, description,lat, lon).collect { response ->
                        response.onSuccess {
                            MotionToast.createToast(requireActivity(),
                                getString(R.string.success_info),
                                getString(R.string.success_message_story),
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.poppins))
                                activity?.onBackPressed()
                        }

                        response.onFailure {
                            setLoadingState(false)
                            MotionToast.createToast(requireActivity(),
                                getString(R.string.failed_info),
                                getString(R.string.failed_message_story),
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.poppins))
                        }
                    }
                }
            }
        } else setLoadingState(false)
    }

    private fun startIntentCameraX() {
        val intent = Intent(activity, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == CAMERA_X_RESULT) {
            val myFile = activityResult.data?.getSerializableExtra("picture") as File
            val isBackCamera = activityResult.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.btnUpload.isEnabled = true
            binding.imageView.setImageBitmap(result)
        }

    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getMyLocation(){
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                myLocation = location
                Log.d("TEST", "getLastLocation: ${location.latitude}, ${location.longitude}")
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getMyLocation()
            }
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        navView.visibility = View.VISIBLE
    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->

        if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = activityResult.data?.data as Uri

            val myFile = uriToFile(selectedImg, requireActivity())

            getFile = myFile

            binding.btnUpload.isEnabled = true
            binding.imageView.setImageURI(selectedImg)
        }

    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            etDescription.isEnabled = !isLoading

            viewLoading.loadingVisible(isLoading)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}