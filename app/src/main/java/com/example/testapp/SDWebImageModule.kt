package com.example.testapp

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.Placeholder
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class SDWebImageModule(private val reactContext: Context) {
    fun loadImagePath(imageUrl: String): String? {
        // Glide 原生API：提交下载并返回缓存文件
        // 需要在后台线程执行此方法
        val file = Glide.with(reactContext)
            .downloadOnly()
            .load(imageUrl)
            .submit()
            .get() //阻塞获取缓存文件（IO线程安全）

        val path = file?.absolutePath
        return path
    }

    fun getCachedImagePath(imageUrl: String) {}

    fun clearCache() {
        Glide.get(reactContext).clearDiskCache()
    }

    fun getCacheSize(): String? {
        return FileUtils.getSize(Glide.getPhotoCacheDir(reactContext))
    }
}