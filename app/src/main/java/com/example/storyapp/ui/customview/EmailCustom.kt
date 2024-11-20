package com.example.storyapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.ViewParent
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmailCustom @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    TextInputEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val email = s.toString()
                val parentLayout = findParentTextInputLayout()

                when {
                    email.isBlank() -> parentLayout?.error = resources.getString(R.string.error_empty_email)
                    !Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches() -> parentLayout?.error = resources.getString(R.string.validationEmail)

                    else -> {
                        parentLayout?.error = null
                        parentLayout?.isErrorEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun findParentTextInputLayout(): TextInputLayout? {
        var parentView: ViewParent? = parent
        while (parentView != null) {
            if (parentView is TextInputLayout) {
                return parentView
            }
            parentView = (parentView as? ViewParent)?.parent
        }
        return null
    }
}