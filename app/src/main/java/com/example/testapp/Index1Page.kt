package com.example.testapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
data object Home : NavKey

@Composable
fun Index1Page() {
    Column() {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "网络图片",
//                    modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            modifier = Modifier.background(Color.Black)
        )
        GlideNativeImagePage()
    }
}