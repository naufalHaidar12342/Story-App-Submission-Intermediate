package xyz.heydarrn.storyappdicoding

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import xyz.heydarrn.storyappdicoding.databinding.ActivityCapturePhotoBinding
import xyz.heydarrn.storyappdicoding.model.CameraUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CapturePhotoActivity : AppCompatActivity() {
    private lateinit var bindingCamera:ActivityCapturePhotoBinding
    private lateinit var executeCamera:ExecutorService
    private var chooseCamera:CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var  captureImage:ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCamera = ActivityCapturePhotoBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)


        executeCamera = Executors.newSingleThreadExecutor()
        bindingCamera.apply {
            snapPicture.setOnClickListener {
                takeThePhoto()
            }
            flipCameraButton.setOnClickListener {
                chooseCamera = when (chooseCamera) {
                    CameraSelector.DEFAULT_BACK_CAMERA -> {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                    else -> {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                }
                initializeCamera()
            }
        }
    }

    private fun initializeCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this)
        cameraProvider.addListener({
            val provideCamera : ProcessCameraProvider = cameraProvider.get()
            val previewResult = Preview
                .Builder()
                .build()
                .also {
                    it.setSurfaceProvider(bindingCamera.cameraPreview.surfaceProvider)
                }

            captureImage = ImageCapture.Builder().build()

            try {
                provideCamera.unbindAll()
                provideCamera.bindToLifecycle(
                    this,
                    chooseCamera,
                    previewResult,
                    captureImage
                )
            }catch (exceptionCamera : Exception) {
                Toast.makeText(this,"Gagal memunculkan camera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takeThePhoto() {
        val filePhoto = CameraUtils.createFile(application)
        val pictureOutputOptions = ImageCapture.OutputFileOptions.Builder(filePhoto).build()
        val capturesImage = captureImage ?: return
        capturesImage.takePicture(
            pictureOutputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intentForUpload = Intent()
                    intentForUpload.apply {
                        putExtra("photo_taken",filePhoto)
                        putExtra(
                            "backCameraChosen",
                            chooseCamera == CameraSelector.DEFAULT_BACK_CAMERA
                        )
                        setResult(UploadNewStoryActivity.CAMERA_X,intentForUpload)
                        finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    private fun setToFullscreen () {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        setToFullscreen()
        initializeCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        executeCamera.shutdown()
    }
}