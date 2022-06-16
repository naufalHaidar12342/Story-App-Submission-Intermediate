package xyz.heydarrn.storyappdicoding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.heydarrn.storyappdicoding.databinding.FragmentStoryRegisterBinding

class StoryRegisterFragment : Fragment() {
    private var _bindingSignUp:FragmentStoryRegisterBinding ? = null
    private val bindingSignUp get() = _bindingSignUp!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _bindingSignUp= FragmentStoryRegisterBinding.inflate(inflater,container,false)
        return bindingSignUp.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}