package xyz.heydarrn.storyappdicoding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.heydarrn.storyappdicoding.databinding.ActivityUploadNewStoryBinding
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.model.CameraUtils
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingModelFactory
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingViewModel
import xyz.heydarrn.storyappdicoding.viewmodel.UploadOurStoryViewModel
import java.io.File

class UploadNewStoryActivity : AppCompatActivity() {
    private lateinit var bindingUpload:ActivityUploadNewStoryBinding
    private lateinit var viewModelUpload:UploadOurStoryViewModel
    private lateinit var token:String
    private var getPhotoFile:File? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingUpload = ActivityUploadNewStoryBinding.inflate(layoutInflater)
        setContentView(bindingUpload.root)
        supportActionBar?.title = getString(R.string.upload_story_page_title)
        initializePermission()

        setupViewModelUpload()
        bindingUpload.apply {
            photoGalleryUpload.setOnClickListener { openGallery() }
            snapPictureUpload.setOnClickListener { openCamera() }
            uploadButton.setOnClickListener { uploadSelectedPhoto() }
        }
    }

    private fun setupViewModelUpload() {
        val factoryUpload = StoriesDicodingModelFactory.getStoryModelFactoryInstance(this)
        viewModelUpload = ViewModelProvider(this, factoryUpload)[UploadOurStoryViewModel::class.java]

        viewModelUpload.getTokenForUploadStory().observe(this) { observeToken ->
            if (observeToken.isEmpty()) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            } else {
                this.token = observeToken
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!grantedPermissions()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Permission to use camera and gallery
    private fun grantedPermissions() : Boolean {
        return PERMISSION_REQUIREMENT.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initializePermission() {
        if (!grantedPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIREMENT, REQUEST_CODE_PERMISSION)
        }
    }

    // using camera
    private val launchCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == CAMERA_X) {
            val myFilePicture = activityResult.data?.getSerializableExtra("photo_taken") as File
            val doesBackCameraSelected = activityResult.data?.getBooleanExtra("backCameraChosen",true) as Boolean

            val rotatedPhoto = CameraUtils.rotateBitmap(
                BitmapFactory.decodeFile(myFilePicture.path),
                doesBackCameraSelected
            )

            val toFile = CameraUtils.bitmapToFile(this, rotatedPhoto)
            getPhotoFile = toFile
            bindingUpload.previewPhotoUpload.setImageBitmap(rotatedPhoto)
        }
    }

    private fun openCamera() {
        val intentCamera = Intent(this, CapturePhotoActivity::class.java)
        launchCameraX.launch(intentCamera)
    }

    //using gallery
    private fun openGallery() {
        val intentOpenGallery = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val imageChooser = Intent.createChooser(intentOpenGallery, getString(R.string.choose_image_hint))
        launchIntentGallery.launch(imageChooser)
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { galleryResult ->

        if (galleryResult.resultCode == RESULT_OK) {
            val selectedPicture : Uri = galleryResult.data?.data as Uri
            val galleryFile = CameraUtils.uriToFile(selectedPicture, this)
            getPhotoFile = galleryFile
            bindingUpload.previewPhotoUpload.setImageURI(selectedPicture)
        }
    }

    //uploading selected photo
    private fun uploadSelectedPhoto() {
        if (getPhotoFile != null) {
            val getImageDesc = bindingUpload.photoDescriptionUpload.text.toString().trim()
            if (getImageDesc.isEmpty()) {
                bindingUpload.photoDescriptionUpload.error = getString(R.string.image_desc_empty)
            } else {
                val fileToUpload = CameraUtils.reduceFileImage(getPhotoFile as File)
                val descriptionToUpload = getImageDesc.toRequestBody("text/plain".toMediaType())
                val imageToUpload = fileToUpload.asRequestBody("image/jpeg".toMediaTypeOrNull())

                //name parameter must be similar to key from documentation
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    fileToUpload.name,
                    imageToUpload
                )

                viewModelUpload.uploadStory(token, imageMultipart, descriptionToUpload).observe(this) { uploadResult ->
                    if (uploadResult != null) {
                        when (uploadResult) {
                            is ApiResponseConfig.ResponseSuccess -> {
                                Toast.makeText(
                                    this, uploadResult.data.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                // wait around 2 second, then move back to DicodingStory
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(
                                        Intent(this, DicodingStoryActivity::class.java)
                                    )
                                },2000)
                            }

                            is ApiResponseConfig.ResponseFail -> {
                                Toast.makeText(
                                    this,
                                    getString(R.string.upload_story_fail,uploadResult.errorMessage),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    this,
                                    getString(R.string.selected_picture_empty),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val PERMISSION_REQUIREMENT = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        const val CAMERA_X = 200
    }
}