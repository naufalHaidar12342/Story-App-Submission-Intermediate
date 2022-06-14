package xyz.heydarrn.storyappdicoding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import xyz.heydarrn.storyappdicoding.databinding.FragmentStoryLoginBinding

class StoryLoginFragment : Fragment() {
    private var _bindingLogin:FragmentStoryLoginBinding?=null
    private val bindingLogin get() = _bindingLogin!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _bindingLogin= FragmentStoryLoginBinding.inflate(inflater,container,false)
        return bindingLogin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingLogin.apply {
            emailInputIcon.setImageResource(R.drawable.ic_email)
            passwordInputIcon.setImageResource(R.drawable.ic_key)
            logoDicoding.setImageResource(R.drawable.image_dicoding)
        }
    }
}