package com.crp.flowerduet.worker

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import coil.ImageLoader
import coil.request.GetRequest
import coil.request.SuccessResult
import com.crp.flowerduet.utils.IMAGE_2
import com.crp.flowerduet.utils.KEY_IMAGE_URI
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

// 2nd Worker that will fetch image and store it in files as Image-2
class FetchImageTwo(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {

        withContext(coroutineContext) {
            val loader = ImageLoader(appContext)
            val request = GetRequest.Builder(appContext)
                .data(IMAGE_2)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val resolver = applicationContext.contentResolver
            val imageUrl = MediaStore.Images.Media.insertImage(
                resolver, bitmap, "Image-2","")

            Result.success(workDataOf(KEY_IMAGE_URI to imageUrl))
        }
    }

}