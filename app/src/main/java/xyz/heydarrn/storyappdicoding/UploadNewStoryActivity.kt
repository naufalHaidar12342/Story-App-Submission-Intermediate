package xyz.heydarrn.storyappdicoding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UploadNewStoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_new_story)
    }

    companion object {
        const val CAMERA_X = 200
    }
}