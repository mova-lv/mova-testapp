package com.example.testapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.example.testapp.pages.CropImagePage
import kotlinx.serialization.Serializable

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