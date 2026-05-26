package com.example.testapp.pages

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import coil.compose.rememberAsyncImagePainter
import com.example.testapp.funcs.FakeCameraManager
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data object NavCameraPage : NavKey

@Composable
fun CameraPage() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    val app = context.applicationContext as Application
    val viewModel: CameraViewModel = viewModel(factory = CameraViewModel.factory(app))

    LaunchedEffect(hasAudioPermission) {
        viewModel.hasAudioPermission.value = hasAudioPermission
    }

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission && !viewModel.isCameraReady.value) {
            viewModel.initCamera()
        }
    }

    LaunchedEffect(viewModel.isCameraReady.value, viewModel.lensFacing.value) {
        if (viewModel.isCameraReady.value) {
            viewModel.bindUseCases(lifecycleOwner)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.unbindUseCases()
        }
    }

    val surfaceRequest by viewModel.surfaceRequest.collectAsState()

    if (!hasCameraPermission) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission required", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                )
            }) {
                Text("Grant Permissions")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Preview area
            Box(modifier = Modifier.weight(1f)) {
                surfaceRequest?.let { request ->
                    CameraXViewfinder(
                        surfaceRequest = request,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Error overlay
                viewModel.errorMessage.value?.let { msg ->
                    Text(
                        text = msg,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(4.dp)
                    )
                }

                // Recording indicator
                if (viewModel.isRecording.value) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }

            // Thumbnail of last captured photo
            viewModel.capturedPhotoPath.value?.let { path ->
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            androidx.compose.foundation.Image(
                                painter = rememberAsyncImagePainter(File(path)),
                                contentDescription = "Last photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Button(onClick = {
                            val newPath = FakeCameraManager.setFakeImage(context, path)
                            viewModel.capturedPhotoPath.value = newPath
                        }) {
                            Text("Set as Fake Image")
                        }
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Switch lens
                IconButton(onClick = { viewModel.switchLens() }) {
                    Icon(
                        Icons.Default.SwitchCamera,
                        contentDescription = "Switch camera",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Capture photo
                IconButton(
                    onClick = { viewModel.takePhoto() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .padding(4.dp)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                // Record video
                IconButton(onClick = {
                    if (viewModel.isRecording.value) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                }) {
                    if (viewModel.isRecording.value) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red)
                        )
                    } else {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = "Record video",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}