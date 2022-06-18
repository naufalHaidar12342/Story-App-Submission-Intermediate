package xyz.heydarrn.storyappdicoding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.heydarrn.storyappdicoding.databinding.ActivityStoryWithPagingBinding
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.model.adapter.StoryListPagingAdapter
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingModelFactory
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingViewModel

class StoryWithPagingActivity : AppCompatActivity() {
    private lateinit var bindingPagingStory:ActivityStoryWithPagingBinding
    private lateinit var viewModelStory:StoriesDicodingViewModel
    private val adapterStoryPaging by lazy { StoryListPagingAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingPagingStory= ActivityStoryWithPagingBinding.inflate(layoutInflater)
        setContentView(bindingPagingStory.root)
        supportActionBar?.title=getString(R.string.story_paging_title)

        setupViewModelStory()
    }

    private fun setupViewModelStory() {
        val storyFactory:StoriesDicodingModelFactory = StoriesDicodingModelFactory.getStoryModelFactoryInstance(this)
        viewModelStory = ViewModelProvider(this, storyFactory)[StoriesDicodingViewModel::class.java]

        viewModelStory.isUserLoggedInStory().observe(this) { storyPagingLoggedIn ->
            if (!storyPagingLoggedIn) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }
        }

        viewModelStory.getTokenForStory().observe(this) { tokenOfUser ->
            if (tokenOfUser.isNotEmpty()) {
                bindingPagingStory.recyclerViewStoryPaging.apply {
                    adapter = adapterStoryPaging
                    layoutManager = LinearLayoutManager(this@StoryWithPagingActivity)
                }
                viewModelStory.grabStoriesWithLocation("Bearer $tokenOfUser").observe(this) { getStoryResult ->
                    adapterStoryPaging.submitData(lifecycle, getStoryResult)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_story_button -> {
                startActivity(
                    Intent(this, UploadNewStoryActivity::class.java)
                )
                return true
            }

            R.id.log_out_button -> {
                viewModelStory.logoutFromStoryPage()
                return true
            }

            R.id.open_map_button -> {
                startActivity(
                    Intent(this, StoryDicodingMapsActivity::class.java)
                )
                return true
            }
        }
        return false
    }
}