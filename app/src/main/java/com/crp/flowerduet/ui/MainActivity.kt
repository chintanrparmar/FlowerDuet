package com.crp.flowerduet.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.crp.flowerduet.databinding.ActivityMainBinding
import com.crp.flowerduet.viewmodel.WorkerViewModel


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: WorkerViewModel
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        requestAppPermissions()
        viewModel = ViewModelProvider(this).get(WorkerViewModel::class.java)

        mainBinding.button1.setOnClickListener {
            fetchImageAPI()//We can have 2 buttons but this single button will do the job as we have described the condition in our viewmodel
        }

    }

    private fun fetchImageAPI() {
        viewModel.fetchImages()

        viewModel.outputWorkInfos.observe(this, Observer { listOfWorkInfo ->


            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }//handling empty state

            val workInfo = listOfWorkInfo[0]


            if (workInfo.state.isFinished) {
                //when work is completed will display images
                Toast.makeText(applicationContext, "Work Done", Toast.LENGTH_SHORT).show()

                if (hasReadPermissions()) {
                    setImages()
                }

            } else {
                Toast.makeText(applicationContext, "Running...", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private fun setImages() {

        //get Bitmap and load them in imageview
        val bitmap1 =
            MediaStore.Images.Media.getBitmap(
                contentResolver,
                Uri.parse("file:///storage/emulated/0/Pictures/Image-1.jpg")
            )
        val bitmap2 =
            MediaStore.Images.Media.getBitmap(
                contentResolver,
                Uri.parse("file:///storage/emulated/0/Pictures/Image-2.jpg")
            )

        mainBinding.image1.load(bitmap1)
        mainBinding.image2.load(bitmap2)
    }


    private fun requestAppPermissions() {

        if (hasReadPermissions()) {
            return
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 101
        ) // your request code
    }

    private fun hasReadPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

}
