package com.example.testapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey
import com.example.testapp.pages.NavCropImage
import com.example.testapp.pages.NavGlideNativeImage
import com.example.testapp.pages.NavCameraPage
import com.example.testapp.pages.NavFakeCamera

@Serializable
data object Home : NavKey

@Composable
fun Index1Page(backStack: NavBackStack<NavKey>) {
    Column() {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "网络图片",
//                    modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            modifier = Modifier.background(Color.Black)
        )
        Button(
            onClick = { backStack.add(NavGlideNativeImage) },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("NavGlideNativeImage")
        }
        Button(
            onClick = { backStack.add(NavCropImage) },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("图片转Base64和裁切")
        }
        Button(
            onClick = { backStack.add(NavCameraPage) },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("CameraX")
        }
        Button(
            onClick = { backStack.add(NavFakeCamera) },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("Fake Camera")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Index1PagePreview() {
    Column() {
        Button(
            onClick = { },
            modifier = Modifier.padding(top = 20.dp),
//            style = {}
        ) {
            Text("NavGlideNativeImage")
        }
        Button(
            onClick = { },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("图片转Base64和裁切")
        }
        Button(
            onClick = { },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("CameraX")
        }
    }
}
