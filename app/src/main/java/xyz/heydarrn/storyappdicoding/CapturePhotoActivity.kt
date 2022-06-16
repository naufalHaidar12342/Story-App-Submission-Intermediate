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
            val previewResult = Preview.Builder()
            previewResult.build().also {
                it.setSurfaceProvider(bindingCamera.cameraPreview.surfaceProvider)
            }

            captureImage = ImageCapture.Builder().build()

            try {
                provideCamera.unbindAll()
                provideCamera.bindToLifecycle(
                    this,
                    chooseCamera,
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
                            "backCameraChoosen",
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                // Default behavior is that if navigation bar is hidden, the system will "steal" touches
                // and show it again upon user's touch. We just want the user to be able to show the
                // navigation bar by swipe, touches are handled by custom code -> change system bar behavior.
                // Alternative to deprecated SYSTEM_UI_FLAG_IMMERSIVE.
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                // make navigation bar translucent (alternative to deprecated
                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                // - do this already in hideSystemUI() so that the bar
                // is translucent if user swipes it up
                window.navigationBarColor = getColor(R.color.purple_500)
                // Finally, hide the system bars, alternative to View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and SYSTEM_UI_FLAG_FULLSCREEN.
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    // Do not let system steal touches for showing the navigation bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Hide the nav bar and status bar
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Keep the app content behind the bars even if user swipes them up
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // make navbar translucent - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
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