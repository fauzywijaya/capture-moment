package com.example.capturemoment.ui.authentication.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentSignInBinding
import com.example.capturemoment.ui.customview.loadingVisible
import com.example.capturemoment.ui.home.HomeActivity
import com.example.capturemoment.ui.home.HomeActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding as FragmentSignInBinding


    private var signInJob: Job = Job()
    private val viewModel: SignInViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        with(binding){
            btnSignIn.setOnClickListener {
                signInAction()
            }
           signUp.setOnClickListener {
               val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
               it.findNavController().navigate(action)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInAction(){
        val email = binding.myEmailEditText.text.toString().trim()
        val password = binding.myPasswordEditText.text.toString()
        setLoading(true)
        lifecycleScope.launchWhenResumed {
            if (signInJob.isActive) signInJob.cancel()

            signInJob = launch {
                viewModel.postUserLogin(email, password).collect { result ->
                    result.onSuccess { credentials ->
                        credentials.login?.apply {
                            token?.let {
                                viewModel.saveUserToken(it)
                            }
                            name?.let {
                                viewModel.saveNameUser(it)
                            }.also {
                                Intent(requireContext(), HomeActivity::class.java).also { intent ->
                                    intent.putExtra(EXTRA_TOKEN, token)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
//                        credentials.login?.token?.let { token ->
//                                viewModel.saveUserToken(token)
//                            }
//
//                        credentials.login?.name?.let { name ->
//                                viewModel.saveNameUser(name)
//                                Log.e("VM", name)
//                            }




                        MotionToast.createToast(requireActivity(),
                            getString(R.string.success_title_sign_in),
                            getString(R.string.success_message_sign_in),
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.poppins))
                    }

                    result.onFailure {
                        MotionToast.createToast(requireActivity(),
                            getString(R.string.failed_title_sign_in),
                            getString(R.string.failed_message_sign_in),
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.poppins))

                        setLoading(false)
                    }
                }

            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(300)
        val message = ObjectAnimator.ofFloat(binding.textView3, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.myEmailEditText, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.myPasswordEditText, View.ALPHA, 1f).setDuration(300)
        val signIn = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(300)
        val signUp = ObjectAnimator.ofFloat(binding.signUp, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title, message, email, password, signIn,signUp)
            startDelay = 300
        }.start()
    }

    private fun setLoading(state: Boolean){
        binding.apply {
            myEmailEditText.isEnabled = !state
            myPasswordEditText.isEnabled = !state
            btnSignIn.isEnabled = !state

            if (state) {
                viewLoading.loadingVisible(true)
            } else {
                viewLoading.loadingVisible(false)
            }
        }
    }
}