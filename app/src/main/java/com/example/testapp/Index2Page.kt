package com.example.testapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Product(val id: String) : NavKey

@Preview(showBackground = true)
@Composable
fun Index2Page() {
    Column() {
        Text("Index2Page FileModule 测试")
        CropImagePage()
    }
}

@Composable
fun CropImagePage() {
    // 图片 URL
    var imageUrl by remember {
        mutableStateOf("https://pet-oss-sg.iot.mova-tech.com/cat-litter-server/pet-avatar/226d111b-8dbf-4070-955c-ed5fbc053363.jpeg")
    }

    var imageBitmap: ImageBitmap? by remember { mutableStateOf(null) }
    var resultBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    var resultBase64Image by remember { mutableStateOf("") }
    var base64ImageString by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fileModule = FileModule()

    // 加载：Glide 只返回文件
    fun loadImage(url: String) {
        scope.launch {
            val tempPath = withContext(Dispatchers.IO) {
                SDWebImageModule(context).loadImagePath(url) ?: ""
            }
            if (tempPath.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    base64ImageString = fileModule.convertFileToBase64(File(tempPath)) ?: ""
                    val bitmap = fileModule.convertBase64ToBitmap(base64ImageString)
                    imageBitmap = bitmap.asImageBitmap()
                }
            }
        }
    }

    LaunchedEffect(imageUrl) {
        loadImage(imageUrl)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!,
//                painter = rememberAsyncImagePainter(model = File(imageCachePath)),
                    contentDescription = "原始图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )
            }
        }

        Button(
            onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val cropbitmap = fileModule.cropImageFromBase64(base64ImageString,1500,400,600,600)
                        resultBitmap = cropbitmap.asImageBitmap()
                    }
                }
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("裁切图片")
        }
        val orchestrating = SDWebImageModule(context).getCacheSize() ?: "缓存为空"
        Text("当前缓存大小${orchestrating}")
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (resultBitmap != null) {
                Image(
                    bitmap = resultBitmap!!,
                    contentDescription = "结果图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )
            }
        }
    }
}