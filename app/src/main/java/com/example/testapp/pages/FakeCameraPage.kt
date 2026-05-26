package com.example.testapp.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil.compose.rememberAsyncImagePainter
import com.example.testapp.funcs.FakeCameraManager
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data object NavFakeCamera : NavKey

@Composable
fun FakeCameraPage() {
    val context = LocalContext.current
    var fakeImagePath by remember {
        mutableStateOf(FakeCameraManager.getFakeImageFile(context)?.absolutePath)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Fake Camera", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        if (fakeImagePath != null) {
            Card(modifier = Modifier.size(200.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(File(fakeImagePath!!)),
                    contentDescription = "Fake camera image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Text("Fake image is set. Other apps calling camera will get this image.")
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                FakeCameraManager.clearFakeImage(context)
                fakeImagePath = null
            }) {
                Text("Clear Fake Image")
            }
        } else {
            Text("No fake image set yet.")
            Text("Take a photo in CameraX page, then use 'Set as Fake Image' button.")
        }
    }
}