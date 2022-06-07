package com.example.skindiseasedetectionapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import com.example.skindiseasedetectionapp.R

class CustomEditTextPassword : AppCompatEditText , View.OnTouchListener {

    private lateinit var image : Drawable
    private lateinit var showPass : Drawable
    private lateinit var hidePass: Drawable
    private lateinit var lock: Drawable




    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun setButtonDrawable(
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

    private fun init(){
        showPass = ContextCompat.getDrawable(context,R.drawable.ic_visibility) as Drawable
        hidePass = ContextCompat.getDrawable(context,R.drawable.ic_visibility_off) as Drawable
        lock = ContextCompat.getDrawable(context,R.drawable.ic_padlock) as Drawable
        setButtonDrawable(endOfTheText = showPass)
        setOnTouchListener(this)


    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        if(compoundDrawables[2] != null ){
            if(event?.action == MotionEvent.ACTION_UP){
                if(event.rawX >= this.right - this.compoundDrawables[2].bounds.right){
                    val selection = this.selectionEnd
                    if(visiblePas == false){
                        setButtonDrawable(endOfTheText = showPass)
                        setCompoundDrawablesWithIntrinsicBounds(lock,null,hidePass,null)
                        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        visiblePas = true
                        return true
                    }else{
                        setButtonDrawable(endOfTheText = hidePass)
                        setCompoundDrawablesWithIntrinsicBounds(lock,null,showPass,null)
                        visiblePas  = false
                        this.transformationMethod = PasswordTransformationMethod.getInstance()
                        return true
                    }
                }
            }
        }

        return false
    }

    companion object{
        private var visiblePas : Boolean = false
    }

    private fun logger(str: String){
        Log.d("password",str)
    }



}