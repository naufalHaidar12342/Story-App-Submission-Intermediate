package xyz.heydarrn.storyappdicoding

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
        setHyperlinkForLogin()
    }

    private fun setHyperlinkForLogin() {
        val clickableText = object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(StoryRegisterFragmentDirections.actionStoryRegisterFragmentToStoryLoginFragment())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val span=SpannableString(resources.getString(R.string.hyperlink_to_login))
        span.setSpan(clickableText,18,23,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bindingSignUp.loginHyperlink.apply {
            text = span
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}