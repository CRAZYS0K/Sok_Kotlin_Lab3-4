package com.recipebook.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.recipebook.logic.PhotoSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.UUID

class PhotoStorage(context: Context) {
    private val appContext = context.applicationContext
    private val dir = File(appContext.filesDir, "photos")

    suspend fun import(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = appContext.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

            val options = BitmapFactory.Options().apply {
                inSampleSize = PhotoSize.sampleSize(bounds.outWidth, bounds.outHeight, MAX_SIDE)
            }
            val decoded = resolver.openInputStream(uri)
                ?.use { BitmapFactory.decodeStream(it, null, options) }
                ?: return@runCatching null
            val rotation = resolver.openInputStream(uri)?.use { ExifInterface(it).rotationDegrees } ?: 0
            val photo = scaleAndRotate(decoded, rotation)

            dir.mkdirs()
            val fileName = "${UUID.randomUUID()}.jpg"
            File(dir, fileName).outputStream().use { photo.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            fileName
        }.getOrNull()
    }

    suspend fun load(fileName: String, maxSide: Int): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            open(fileName).use { BitmapFactory.decodeStream(it, null, bounds) }
            val options = BitmapFactory.Options().apply {
                inSampleSize = PhotoSize.sampleSize(bounds.outWidth, bounds.outHeight, maxSide)
            }
            open(fileName).use { BitmapFactory.decodeStream(it, null, options) }
        }.getOrNull()
    }

    fun delete(fileName: String) {
        if (fileName.startsWith(ASSET_PREFIX)) return
        File(dir, fileName).delete()
    }

    private fun open(fileName: String): InputStream =
        if (fileName.startsWith(ASSET_PREFIX)) {
            appContext.assets.open(fileName.removePrefix(ASSET_PREFIX))
        } else {
            File(dir, fileName).inputStream()
        }

    private fun scaleAndRotate(source: Bitmap, rotation: Int): Bitmap {
        val scale = minOf(1f, MAX_SIDE.toFloat() / maxOf(source.width, source.height))
        if (scale == 1f && rotation == 0) return source
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postRotate(rotation.toFloat())
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    companion object {
        const val ASSET_PREFIX = "asset:"
        private const val MAX_SIDE = 1280
    }
}
