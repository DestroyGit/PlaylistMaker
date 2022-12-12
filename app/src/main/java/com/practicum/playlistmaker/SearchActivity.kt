package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val textSearch = findViewById<EditText>(R.id.inputSearch)
        val btnClearText = findViewById<ImageView>(R.id.clearTextSearch)

        btnClearText.setOnClickListener(){
            textSearch.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(textSearch.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btnClearText.visibility = visibilityView(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        textSearch.addTextChangedListener(searchTextWatcher)
    }

    fun visibilityView(s: CharSequence?): Int{
        return if (s.isNullOrEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
    }
}