package com.example.testapp

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GlideNativeImagePage() {
    // 图片 URL
    var imageUrl by remember {
        mutableStateOf("https://mmbiz.qpic.cn/mmbiz_jpg/AYbHGia3GVC2Xpb4ybk6gviawxCZHg2FzQoImbnpxyE8QyxVEZpcStYSvCCv1G8k4ZYd1Je1QWXFNXWM1uic7wHBwRGcNS1Jgzx7ho54shZAa8/640?wx_fmt=jpeg&from=appmsg&tp=webp&wxfrom=5&wx_lazy=1#imgIndex=0")
    }

    // 加载状态
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf(false) }
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    // 加载图片逻辑
    LaunchedEffect(imageUrl) {
        isLoading = true
        loadError = false

        // Glide 原生方式加载 Bitmap
        withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource.asImageBitmap()
                        isLoading = false
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                    override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                        isLoading = false
                        loadError = true
                    }
                })
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // 加载中
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
            // 加载失败
            else if (loadError || bitmap == null) {
                Text("加载失败")
            }
            // 显示图片
            else {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = "网络图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Button(
            onClick = {
                imageUrl =
                    "https://pet-oss-sg.iot.mova-tech.com/cat-litter-server/pet-avatar/226d111b-8dbf-4070-955c-ed5fbc053363.jpeg"
            },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("刷新图片")
        }
    }
}