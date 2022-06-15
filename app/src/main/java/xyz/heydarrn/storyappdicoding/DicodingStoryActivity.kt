package xyz.heydarrn.storyappdicoding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import xyz.heydarrn.storyappdicoding.databinding.ActivityDicodingStoryBinding

class DicodingStoryActivity : AppCompatActivity() {
    private lateinit var bindingStory:ActivityDicodingStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingStory= ActivityDicodingStoryBinding.inflate(layoutInflater)
        setContentView(bindingStory.root)

        setIconForTitle()
    }

    private fun setIconForTitle() {
        bindingStory.imageIconStory.setImageResource(R.drawable.ic_baseline_collections_24)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_navigation_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_story_button -> { return true }
            R.id.log_out_button -> { return true }
        }
        return false
    }
}