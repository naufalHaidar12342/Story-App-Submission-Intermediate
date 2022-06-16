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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import xyz.heydarrn.storyappdicoding.databinding.FragmentStoryRegisterBinding
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.viewmodel.RegisterPageViewModel
import xyz.heydarrn.storyappdicoding.viewmodel.UserAuthModelFactory

class StoryRegisterFragment : Fragment() {
    private var _bindingSignUp:FragmentStoryRegisterBinding? = null
    private val bindingSignUp get() = _bindingSignUp!!
    private lateinit var viewModelRegister:RegisterPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _bindingSignUp= FragmentStoryRegisterBinding.inflate(inflater,container,false)
        return bindingSignUp.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHyperlinkForLogin()
        setIconForInput()
        setupRegisterViewModel()

        bindingSignUp.signUpButton.setOnClickListener {
            registerUserToAPI()
            findNavController().navigate(StoryRegisterFragmentDirections.actionStoryRegisterFragmentToStoryLoginFragment())
        }

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

    private fun setupRegisterViewModel() {
        val registerFactory:UserAuthModelFactory = UserAuthModelFactory.getUserAuthInstance(requireContext())
        viewModelRegister = ViewModelProvider(this,registerFactory)[RegisterPageViewModel::class.java]

    }

    private fun setIconForInput() {
        bindingSignUp.apply {
            emailIconRegister.setImageResource(R.drawable.ic_email)
            passwordIconRegister.setImageResource(R.drawable.ic_key)
            usernameIconRegister.setImageResource(R.drawable.ic_baseline_people_24)
        }
    }
    private fun registerUserToAPI() {
        bindingSignUp.progressBarRegister.visibility=View.VISIBLE

        val getName = bindingSignUp.customUsernameRegister.text.toString().trim()
        val getEmail = bindingSignUp.customEmailRegister.text.toString().trim()
        val getPass = bindingSignUp.customPasswordRegister.text.toString().trim()

        when {
            getName.isEmpty() -> {
                bindingSignUp.customUsernameRegister.error = resources.getString(R.string.username_invalid)
            }

            getEmail.isEmpty() -> {
                bindingSignUp.customEmailRegister.error = resources.getString(R.string.email_empty)
            }

            getPass.isEmpty() -> {
                bindingSignUp.customPasswordRegister.error = resources.getString(R.string.password_empty)
            }

            else -> {
                viewModelRegister.registerThisUser(getName, getEmail, getPass).observe(viewLifecycleOwner) { registerResult ->
                    if (registerResult!=null) {
                        when (registerResult) {
                            is ApiResponseConfig.ResultIsLoading -> {
                                bindingSignUp.progressBarRegister.visibility = View.VISIBLE
                            }

                            is  ApiResponseConfig.ResponseSuccess -> {
                                bindingSignUp.progressBarRegister.visibility = View.INVISIBLE

                                val storeUser = registerResult.data
                                if (storeUser.error) {
                                    bindingSignUp.progressBarRegister.visibility = View.INVISIBLE
                                    Toast.makeText(context, storeUser.message, Toast.LENGTH_SHORT).show()

                                    Toast.makeText(context, getString(R.string.email_registered_error), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context,getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                                }
                            }

                            is ApiResponseConfig.ResponseFail -> {
                                bindingSignUp.progressBarRegister.visibility = View.INVISIBLE
                                Toast.makeText(context, getString(R.string.email_registered_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}