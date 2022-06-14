package xyz.heydarrn.storyappdicoding.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import xyz.heydarrn.storyappdicoding.R

class MyCustomEmail : AppCompatEditText,View.OnTouchListener {
    private lateinit var clearText:Drawable

    constructor(context: Context) : super(context){ initializeCustomEmail() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initializeCustomEmail() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initializeCustomEmail() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint=resources.getString(R.string.email_hint)
        textAlignment=View.TEXT_ALIGNMENT_TEXT_START
    }

    private fun initializeCustomEmail(){
        clearText=ContextCompat.getDrawable(context, R.drawable.ic_clear_text) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //if string is empty, will hide the (x) button for clearing text
                //if string is not empty, will show the (x) button
                if (p0.toString().isNotEmpty()){
                    showButtonForClearingText()

                }else{
                    hideButtonForClearingText()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!validateEmailAddressInputted(text.toString())){
                    error=resources.getString(R.string.invalid_email_input)
                }
            }

        })
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            //if user has right-to-left text orientation (arabic, etc),
            // flip the button position to left
            //if user has left-to-right text orientation, flip button will stay normal
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearText.intrinsicWidth + paddingStart).toFloat()
                when {
                    p1!!.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearText.intrinsicWidth).toFloat()
                when {
                    p1!!.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearText = ContextCompat.getDrawable(context, R.drawable.ic_clear_text) as Drawable
                        showButtonForClearingText()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearText = ContextCompat.getDrawable(context, R.drawable.ic_clear_text) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideButtonForClearingText()
                        return true
                    }
                    else -> return false
                }
            } else { return false }
        }
        return false
    }

    private fun showButtonForClearingText() {
        setButtonDrawables(endOfTheText = clearText)
    }

    private fun hideButtonForClearingText() {
        setButtonDrawables()
    }

    private fun validateEmailAddressInputted(email:String) : Boolean{
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }


}