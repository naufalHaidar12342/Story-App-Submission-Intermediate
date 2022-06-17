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
import android.widget.Toast
import androidx.datastore.preferences.protobuf.Api
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import xyz.heydarrn.storyappdicoding.databinding.FragmentStoryLoginBinding
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.viewmodel.LoginPageViewModel
import xyz.heydarrn.storyappdicoding.viewmodel.UserAuthModelFactory

class StoryLoginFragment : Fragment() {
    private var _bindingLogin:FragmentStoryLoginBinding?=null
    private val bindingLogin get() = _bindingLogin!!
    private lateinit var viewModelLogin:LoginPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _bindingLogin= FragmentStoryLoginBinding.inflate(inflater,container,false)
        return bindingLogin.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPictureForImageView()
        setHyperlinkForRegisterAccount()
        bindingLogin.loginButton.setOnClickListener {
            performLogin()
        }
        setupViewModel()
    }

    private fun setPictureForImageView() {
        bindingLogin.apply {
            emailInputIcon.setImageResource(R.drawable.ic_email)
            passwordInputIcon.setImageResource(R.drawable.ic_key)
            logoDicoding.setImageResource(R.drawable.image_dicoding)
        }
    }
    
    private fun setHyperlinkForRegisterAccount() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(StoryLoginFragmentDirections.actionStoryLoginFragmentToStoryRegisterFragment())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val span=SpannableString(getString(R.string.hyperlink_to_register_account))
        span.setSpan(clickableSpan,23,35,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bindingLogin.registerHyperlink.text=span
        bindingLogin.registerHyperlink.movementMethod=LinkMovementMethod.getInstance()
        
    }


    private fun performLogin() {
        val inputEmail = bindingLogin.emailLogin.text.toString().trim()
        val inputPassword = bindingLogin.passwordLogin.text.toString().trim()

        when {
            inputEmail.isEmpty() -> {
                bindingLogin.emailLogin.error=resources.getString(R.string.email_empty)
            }

            inputPassword.isEmpty() -> {
                bindingLogin.passwordLogin.error=resources.getString(R.string.password_empty)
            }

            else -> {
                viewModelLogin.loggingIn(inputEmail,inputPassword).observe(viewLifecycleOwner) { loginResult ->
                    if (loginResult!=null) {
                        when(loginResult) {
                            is ApiResponseConfig.ResultIsLoading -> {
                                bindingLogin.progressBarLogin.visibility=View.VISIBLE
                            }
                            is ApiResponseConfig.ResponseSuccess -> {
                                val userData=loginResult.data
                                if (userData.error) {
                                    Toast.makeText(context,userData.message,Toast.LENGTH_SHORT).show()
                                } else {
                                    val userToken = userData.loginResult.token
                                    viewModelLogin.setToken(userToken,true)
                                }
                            }

                            is ApiResponseConfig.ResponseFail -> {
                                val errorMessage=resources.getString(R.string.login_failed)
                                Toast.makeText(context,errorMessage,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        val factoryViewModel:UserAuthModelFactory = UserAuthModelFactory.getUserAuthInstance(requireContext())
        viewModelLogin = ViewModelProvider(this,factoryViewModel)[LoginPageViewModel::class.java]

        viewModelLogin.grabToken().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                startActivity(
                    Intent(requireContext(),DicodingStoryActivity::class.java)
                )
            }
        }
    }
}