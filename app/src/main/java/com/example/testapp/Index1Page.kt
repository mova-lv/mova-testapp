package com.example.testapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.blankj.utilcode.util.ImageUtils
import com.example.testapp.pages.NavCameraPage
import com.example.testapp.pages.NavCropImage
import com.example.testapp.pages.NavFakeCamera
import com.example.testapp.pages.NavGlideNativeImage
import kotlinx.serialization.Serializable
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data object Home : NavKey

@Composable
fun Index1Page(backStack: NavBackStack<NavKey>) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var rotationDegrees by remember { mutableStateOf(0) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            photoUri?.let { uri ->
                // 将URI转为Bitmap
                val bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }

                // 获取图片旋转角度
                val rotation: Int = context.contentResolver.openInputStream(uri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                } ?: 0

                // 保存到状态，供右下角预览使用
                capturedBitmap = bitmap
                rotationDegrees = rotation

                val bitmapInfo = if (bitmap != null) "${bitmap.width}x${bitmap.height}" else "null"
                "Photo: $bitmapInfo, rotation: ${rotation}°".d()
                Toast.makeText(
                    context,
                    "Photo: $bitmapInfo, rotation: ${rotation}°",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
            Button(
                onClick = {
                    val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                    val file = File(
                        dir,
                        "syscam_${
                            SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.US
                            ).format(Date())
                        }.jpg"
                    )
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    photoUri = uri
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(intent, "选择相机")
                    cameraLauncher.launch(chooser)
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("系统相机")
            }
        }

        // 右下角图片预览
        if (capturedBitmap != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 原图
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("原图", fontSize = 10.sp, color = Color.White)
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "原图",
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, Color.White),
                        contentScale = ContentScale.Fit
                    )
                }
                // 旋转后
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("旋转${rotationDegrees}°", fontSize = 10.sp, color = Color.White)
                    Image(
                        bitmap = rotateBitmap(capturedBitmap!!, rotationDegrees).asImageBitmap(),
                        contentDescription = "旋转后",
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, Color.White),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

/**
 * 根据 EXIF 旋转角度旋转 Bitmap
 */
private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    if (degrees == 0) return bitmap
    return ImageUtils.rotate(bitmap, degrees, 0f, 0f)
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
