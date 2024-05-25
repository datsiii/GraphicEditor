package com.example.drawappcompose

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawappcompose.detail.DetailViewModel
import com.example.drawappcompose.ui.BottomPanel
import com.example.drawappcompose.models.PathData
import com.example.drawappcompose.login.LoginViewModel
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.drawappcompose.detail.DrawCanvas
import com.example.drawappcompose.home.HomeViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            val detailViewModel = viewModel(modelClass = DetailViewModel::class.java)
            val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)

            val scope = rememberCoroutineScope()

            DrawAppComposeTheme {
                Column {
                    Navigation(
                        loginViewModel = loginViewModel,
                        detailViewModel = detailViewModel,
                        homeViewModel = homeViewModel
                    )

                }
            }
        }
    }

}



