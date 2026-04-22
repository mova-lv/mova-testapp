package com.example.testapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object About : NavKey

@Composable
fun Index3Page() {
    Column() {
        Text("Index3Page")
        GlideNativeImagePage()
    }
}