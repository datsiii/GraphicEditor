package com.example.draw_app

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.draw_app.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private lateinit var backgroundImage: ImageView
    private lateinit var slideAnimation: Animation
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backgroundImage = binding.splashFragmentImage
        slideAnimation =  AnimationUtils.loadAnimation(requireContext(), R.anim.side_slide)
        backgroundImage.startAnimation(slideAnimation)

        Handler(requireActivity().mainLooper).postDelayed({
            if (true) {
                findNavController().navigate(R.id.action_splashFragment_to_registrationFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_startFragment)
            }
        }, 3000L)

    }

}