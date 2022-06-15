package xyz.heydarrn.storyappdicoding

import android.content.Intent
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
import android.view.textclassifier.TextLinks
import androidx.navigation.fragment.findNavController
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
        
        setPictureForImageView()
        setHyperlinkForRegisterAccount()
        openStoryPage()

    }

    private fun setPictureForImageView() {
        bindingLogin.apply {
            emailInputIcon.setImageResource(R.drawable.ic_email)
            passwordInputIcon.setImageResource(R.drawable.ic_key)
            logoDicoding.setImageResource(R.drawable.image_dicoding)
        }
    }
    
    private fun setHyperlinkForRegisterAccount() {
        val clickableSpan= object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(StoryLoginFragmentDirections.actionStoryLoginFragmentToStoryRegisterFragment())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
            }
        }
        val span=SpannableString(getString(R.string.hyperlink_to_register_account))
        span.setSpan(clickableSpan,23,34,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bindingLogin.registerHyperlink.text=span
        bindingLogin.registerHyperlink.movementMethod=LinkMovementMethod.getInstance()
        
    }
    
    private fun openStoryPage() {
        bindingLogin.loginButton.setOnClickListener {
            startActivity(Intent(context,DicodingStoryActivity::class.java))
        }
    }
}