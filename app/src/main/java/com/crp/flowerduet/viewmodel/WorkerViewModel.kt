package com.crp.flowerduet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.crp.flowerduet.utils.IMAGE_WORK_NAME
import com.crp.flowerduet.utils.TAG_OUTPUT
import com.crp.flowerduet.worker.*

// Worker ViewModel that will help us to keep track status of work
class WorkerViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    init {
        // this live data will give us update when work is completed
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    internal fun fetchImages() {
        // Create network constraint
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Continuation request that will run 1st and 2nd worker respectively one after other.
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(FetchImageOne::class.java)
            )


        val secondWorker = OneTimeWorkRequestBuilder<FetchImageTwo>()
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()


        continuation = continuation.then(secondWorker)//2nd worker will run after 1st work done

        // Actually start the work
        continuation.enqueue()
    }


}