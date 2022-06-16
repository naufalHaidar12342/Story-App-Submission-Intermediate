package xyz.heydarrn.storyappdicoding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.datastore.preferences.protobuf.Api
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import xyz.heydarrn.storyappdicoding.databinding.ActivityDicodingStoryBinding
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.model.adapter.StoryListAdapter
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingModelFactory
import xyz.heydarrn.storyappdicoding.viewmodel.StoriesDicodingViewModel

class DicodingStoryActivity : AppCompatActivity() {
    private lateinit var bindingStory:ActivityDicodingStoryBinding
    private lateinit var viewModelStory:StoriesDicodingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingStory= ActivityDicodingStoryBinding.inflate(layoutInflater)
        setContentView(bindingStory.root)

        supportActionBar?.title="Dicoding's Friend Story"
        setIconForTitle()

        setupViewModelStory()
    }

    private fun setIconForTitle() {
        bindingStory.imageIconStory.setImageResource(R.drawable.ic_baseline_collections_24)
    }
    private fun setupViewModelStory() {
        val storyFactory:StoriesDicodingModelFactory = StoriesDicodingModelFactory.getStoryModelFactoryInstance(this)
        viewModelStory = ViewModelProvider(this,storyFactory)[StoriesDicodingViewModel::class.java]

        viewModelStory.isUserLoggedInStory().observe(this) { storyLoggedIn ->
            if (!storyLoggedIn) {
                startActivity(
                    Intent(this, StoryLoginFragment::class.java)
                )
            }
        }

        viewModelStory.getTokenForStory().observe(this) { tokenOfUser ->
            if (tokenOfUser.isNotEmpty()) {
                viewModelStory.grabStories(tokenOfUser).observe(this) { storyResult->
                    if (storyResult!=null) {
                        when (storyResult) {
                            is ApiResponseConfig.ResultIsLoading -> {
                                bindingStory.progressBarStory.visibility=View.VISIBLE
                            }

                            is ApiResponseConfig.ResponseSuccess -> {
                                bindingStory.progressBarStory.visibility=View.GONE

                                val receiveStory = storyResult.data.listStory
                                val adapterStory by lazy { StoryListAdapter() }
                                adapterStory.submitList(receiveStory)

                                bindingStory.recyclerViewStories.adapter=adapterStory
                                bindingStory.recyclerViewStories.layoutManager=LinearLayoutManager(this)
                                adapterStory.setThisStoryForDetailView(object : StoryListAdapter.OpenThisStory {
                                    override fun chooseThisStory(
                                        id: String,
                                        username: String,
                                        postedDate: String,
                                        image: String,
                                        imageDesc: String,
                                        longitude: String?,
                                        latitude: String?
                                    ) {
                                        val intentToDetail =Intent(this@DicodingStoryActivity,DetailOfStoryActivity::class.java)
                                        intentToDetail.apply {
                                            putExtra("username",username)
                                            putExtra("image_date",postedDate)
                                            putExtra("image_url",image)
                                            putExtra("image_desc",imageDesc)
                                            putExtra("latitude",latitude)
                                            putExtra("longitude",longitude)
                                        }
                                        startActivity(intentToDetail)
                                    }
                                })
                            }

                            is ApiResponseConfig.ResponseFail -> {
                                bindingStory.progressBarStory.visibility=View.GONE
                                Toast.makeText(this,storyResult.errorMessage,Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_story_button -> { return true }
            R.id.log_out_button -> {
                viewModelStory.logoutFromStoryPage()
                return true
            }
        }
        return false
    }
}