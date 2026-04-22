package com.example.testapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Base64
import com.blankj.utilcode.util.ImageUtils
import java.io.ByteArrayOutputStream
import java.io.File

class FileModule {

    fun convertFileToBase64(file: File): String? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
//        base64String.d()
        return base64String
    }

    fun convertBase64ToBitmap(base64ImageString: String): Bitmap {
        val cleanBase64 = base64ImageString.substringAfter(",")
        val decodedBytes: ByteArray = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap: Bitmap = ImageUtils.getBitmap(decodedBytes, 0)
        return bitmap
    }

    fun cropImageFromBase64(base64ImageString: String, l1: Int, t1: Int, w1: Int, h1: Int): Bitmap {
        val cleanBase64 = base64ImageString.substringAfter(",")
        val decodedBytes: ByteArray = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap: Bitmap = ImageUtils.getBitmap(decodedBytes, 0)

        return ImageUtils.clip(bitmap, l1, t1, w1, h1, true)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArray = ImageUtils.bitmap2Bytes(bitmap)
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


//fun screenShotInRect(imageName: String, l1: Int, t1: Int, w1: Int, h1: Int, callback: Callback) {
//    val context: Activity = getCurrentActivity()
//    if (isActivityValid()) {
//        var l: Float = DisplayUtil.dpToPx(context, l1)
//        val t: Float = DisplayUtil.dpToPx(context, t1)
//        val w: Float = DisplayUtil.dpToPx(context, w1)
//        val h: Float = DisplayUtil.dpToPx(context, h1)
//        if (l < 0) {
//            l = 0f
//        } else if (l + w > DisplayUtil.getScreenWidth(context)) {
//            l = DisplayUtil.getScreenWidth(context) - w
//        }
//        val r = l + w
//        val b = t + h
//        val targetPath: String =
//            android.dreame.module.rn.bridge.host.FileModule.CACHEDIR + imageName
//        ScreenShot.getInstance().generate(
//            context.getWindow().getDecorView().getRootView(),
//            Rect(l.toInt(), t.toInt(), r.toInt(), b.toInt()),
//            targetPath,
//            object : OnGenerateListener() {
//                public override fun onGenerateFinished(throwable: Throwable?) {
//                    //出错不处理继续返回图片地址
//                    callback.invoke(true, targetPath)
//                }
//            })
//    }
//}
}
