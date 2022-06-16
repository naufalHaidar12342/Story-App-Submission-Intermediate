package xyz.heydarrn.storyappdicoding.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import xyz.heydarrn.storyappdicoding.R

class MyCustomUsernameRegister : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearUsernameText:Drawable

    constructor(context: Context) : super(context) { initializeCustomUsername()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initializeCustomUsername()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initializeCustomUsername()}

    private fun initializeCustomUsername() {
        clearUsernameText=ContextCompat.getDrawable(context,R.drawable.ic_clear_text) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.toString().isNotEmpty()) {
                    showClearTextForUsername()
                }else {
                    hideClearTextForUsername()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (text?.length in 1..4) {
                    error=resources.getString(R.string.username_invalid)
                }
            }

        })
    }
    
    private fun showClearTextForUsername() {
        setButtonDrawables(endOfTheText = clearUsernameText )
    }

    private fun hideClearTextForUsername() {
        setButtonDrawables()

    }



    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
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
                clearButtonEnd = (clearUsernameText.intrinsicWidth + paddingStart).toFloat()
                when {
                    p1!!.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearUsernameText.intrinsicWidth).toFloat()
                when {
                    p1!!.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearUsernameText = ContextCompat.getDrawable(context, R.drawable.ic_clear_text) as Drawable
                        showClearTextForUsername()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearUsernameText = ContextCompat.getDrawable(context, R.drawable.ic_clear_text) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearTextForUsername()
                        return true
                    }
                    else -> return false
                }
            } else { return false }
        }
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint=resources.getString(R.string.username_hint)
        textAlignment=View.TEXT_ALIGNMENT_TEXT_START
    }
}