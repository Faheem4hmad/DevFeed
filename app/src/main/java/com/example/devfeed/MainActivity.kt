package com.example.devfeed

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.devfeed.ui.home.HomeScreen
import com.example.devfeed.ui.home.HomeViewModel
import com.example.devfeed.ui.splash.SplashScreen
import com.example.devfeed.ui.theme.DevFeedTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            DevFeedTheme {
                var isSplashVisible by remember { mutableStateOf(true) }

                AnimatedContent(
                    targetState = isSplashVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith
                                fadeOut(animationSpec = tween(400))
                    },
                    label = "SplashToHome"
                ) { showSplash ->
                    if (showSplash) {
                        SplashScreen(
                            onSplashFinished = {
                                isSplashVisible = false
                            }
                        )
                    } else {
                        HomeScreen(viewModel = homeViewModel)
                    }
                }
            }
        }
    }
}