package com.example.testapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.example.testapp.pages.GlideNativeImagePage
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