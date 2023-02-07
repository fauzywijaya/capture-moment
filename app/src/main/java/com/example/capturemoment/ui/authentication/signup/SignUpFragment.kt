package com.example.capturemoment.ui.authentication.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentSignUpBinding
import com.example.capturemoment.ui.customview.loadingVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding ?= null
    private val binding get() = _binding as FragmentSignUpBinding
    private var signUpJob: Job = Job()
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        binding.apply {
            btnSignIn.setOnClickListener {
                val action = SignUpFragmentDirections.actionRegisterFragmentToLoginFragment()
                it.findNavController().navigate(action)
            }
            btnSignUp.setOnClickListener {
                signUpAction()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signUpAction(){
        val name = binding.etFullName.text.toString().trim()
        val email = binding.myEmailEditText.text.toString().trim()
        val password = binding.myPasswordEditText.text.toString()
        setLoading(true)

        lifecycleScope.launchWhenResumed {
            if (signUpJob.isActive) signUpJob.cancel()


            signUpJob = launch {
                viewModel.postUserRegister(name, email, password).collect { result ->
                    result.onSuccess {

                    MotionToast.createToast(requireActivity(),
                        getString(R.string.success_title_sign_up),
                        getString(R.string.success_message_sign_up),
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.poppins))

                        val action = SignUpFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }

                    result.onFailure {
                        MotionToast.createToast(requireActivity(),
                            getString(R.string.failed_title_sign_up),
                            getString(R.string.failed_message_sign_up),
                            MotionToastStyle.SUCCESS,
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
        val name = ObjectAnimator.ofFloat(binding.etFullName, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.myPasswordEditText, View.ALPHA, 1f).setDuration(300)
        val signUp = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(300)
        val signIn = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title, message, email, name, password, signIn,signUp)
            startDelay = 300
        }.start()
    }

    private fun setLoading(state: Boolean){
        binding.apply {
            myEmailEditText.isEnabled = !state
            myPasswordEditText.isEnabled = !state
            etFullName.isEnabled = !state
            btnSignUp.isEnabled = !state
            btnSignIn.isEnabled = !state
            if (state) {
                viewLoading.loadingVisible(true)
            } else {
                viewLoading.loadingVisible(false)
            }
        }
    }

}