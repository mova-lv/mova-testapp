package com.example.testapp.pages

import android.app.Application
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recording
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LifecycleOwner
import com.example.testapp.d
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.content.ContextCompat

class CameraViewModel(val app: Application) : ViewModel() {

    var lensFacing = mutableStateOf(CameraSelector.LENS_FACING_BACK)
    var isRecording = mutableStateOf(false)
    var isCameraReady = mutableStateOf(false)
    var capturedPhotoPath = mutableStateOf<String?>(null)
    var capturedVideoPath = mutableStateOf<String?>(null)
    var errorMessage = mutableStateOf<String?>(null)
    var hasAudioPermission = mutableStateOf(false)

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val mainExecutor = ContextCompat.getMainExecutor(app)
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun initCamera() {
        ProcessCameraProvider.getInstance(app).addListener({
            try {
                cameraProvider = ProcessCameraProvider.getInstance(app).get()
                preview = Preview.Builder().build()
                preview?.setSurfaceProvider { request ->
                    _surfaceRequest.value = request
                }
                imageCapture = ImageCapture.Builder().build()
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)
                isCameraReady.value = true
                "CameraX initialized".d()
            } catch (e: Exception) {
                errorMessage.value = "Camera init failed: ${e.message}"
                e.printStackTrace()
            }
        }, mainExecutor)
    }

    fun bindUseCases(lifecycleOwner: LifecycleOwner) {
        val provider = cameraProvider ?: return
        val p = preview ?: return
        val ic = imageCapture ?: return
        val vc = videoCapture ?: return

        try {
            provider.unbindAll()
            val selector = CameraSelector.Builder()
                .requireLensFacing(lensFacing.value)
                .build()
            provider.bindToLifecycle(lifecycleOwner, selector, p, ic, vc)
            errorMessage.value = null
            "Use cases bound, lens=${lensFacing.value}".d()
        } catch (e: Exception) {
            errorMessage.value = "Camera bind failed: ${e.message}"
            e.printStackTrace()
        }
    }

    fun unbindUseCases() {
        cameraProvider?.unbindAll()
    }

    fun switchLens() {
        if (isRecording.value) return
        lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun takePhoto() {
        val ic = imageCapture ?: return
        val dir = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return
        val file = File(dir, "photo_${dateFormat.format(System.currentTimeMillis())}.jpg")

        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        ic.takePicture(options, mainExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                capturedPhotoPath.value = file.absolutePath
                "Photo saved: ${file.absolutePath}".d()
            }
            override fun onError(exc: ImageCaptureException) {
                errorMessage.value = "Photo capture failed: ${exc.message}"
            }
        })
    }

    fun startRecording() {
        if (isRecording.value) return
        val vc = videoCapture ?: return
        val dir = app.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: return
        val file = File(dir, "video_${dateFormat.format(System.currentTimeMillis())}.mp4")

        val outputOptions = FileOutputOptions.Builder(file).build()
        val pendingRecording = vc.output
            .prepareRecording(app, outputOptions)
        val recording = if (hasAudioPermission.value) {
            pendingRecording.withAudioEnabled()
        } else {
            pendingRecording
        }.start(mainExecutor) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    isRecording.value = true
                    "Recording started".d()
                }
                is VideoRecordEvent.Finalize -> {
                    isRecording.value = false
                    if (event.hasError()) {
                        errorMessage.value = "Recording failed: ${event.cause?.message}"
                    } else {
                        capturedVideoPath.value = file.absolutePath
                        "Video saved: ${file.absolutePath}".d()
                    }
                    activeRecording = null
                }
            }
        }
        activeRecording = recording
    }

    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    override fun onCleared() {
        super.onCleared()
        activeRecording?.stop()
        cameraProvider?.unbindAll()
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CameraViewModel(app) as T
                }
            }
        }
    }
}