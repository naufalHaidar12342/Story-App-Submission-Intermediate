package xyz.heydarrn.storyappdicoding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import xyz.heydarrn.storyappdicoding.databinding.ActivityDetailOfStoryBinding
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailOfStoryActivity : AppCompatActivity() {
    private lateinit var bindingDetail:ActivityDetailOfStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingDetail= ActivityDetailOfStoryBinding.inflate(layoutInflater)
        setContentView(bindingDetail.root)

        setDetailedStory()
    }

    private fun setDetailedStory () {
        bindingDetail.apply {
            val receivedDate=intent.getStringExtra("image_date")
            val receivedUsername=intent.getStringExtra("username")
            val receivedImage=intent.getStringExtra("image_url")
            val receivedImageDesc=intent.getStringExtra("image_desc")



            postedAtDetail.text=resources.getString(R.string.created_at_template, receivedDate)
            usernameDetail.text= receivedUsername

            Glide.with(this@DetailOfStoryActivity)
                .load(receivedImage)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(photoDetail)

            descriptionDetail.text= receivedImageDesc
        }
    }
}