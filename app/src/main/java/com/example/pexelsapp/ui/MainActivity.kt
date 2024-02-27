package com.example.pexelsapp.ui

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_PexelsApp)
        super.onCreate(savedInstanceState)
        setNightMode()
        setStatusBarTheme()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreenAnimation()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setBackPressed(navController)
        initBottomNavView(navController)
    }

    private fun setNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        installSplashScreen()
    }

    private fun setStatusBarTheme() {
        val decorView = window.decorView
        val flags = decorView.systemUiVisibility
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setBackPressed(navController: NavController) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }
        })
    }

    private fun initBottomNavView(navController: NavController) {
        binding.navView.apply {
            itemIconTintList = null
            itemActiveIndicatorColor = null

            setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_home -> {
                        navController.navigate(R.id.homeFragment)
                    }

                    R.id.navigation_bookmarks -> {
                        navController.navigate(R.id.bookmarksFragment)
                    }
                }
                true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun splashScreenAnimation() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                ZERO_VALUE,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = DURATION

            slideUp.doOnEnd { splashScreenView.remove() }

            slideUp.start()
        }
    }

    companion object{
        private const val ZERO_VALUE = 0f
        private const val DURATION = 300L
    }
}