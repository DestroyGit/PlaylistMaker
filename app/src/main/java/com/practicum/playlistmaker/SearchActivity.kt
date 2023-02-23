package com.practicum.playlistmaker

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object{
        const val SEARCH_LINE = "SEARCH_LINE"
        const val TRACK_NOT_FOUND = "NOT FOUND"
        const val SERVER_NOT_ANSWER = "SERVER ERROR"
    }

    private lateinit var textSearch: EditText
    private lateinit var placeHolderMessage: LinearLayout
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderMessageImage: ImageView
    private lateinit var placeholderMessageButtonRefresh: Button
    private var searchTextString = ""

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        textSearch = findViewById(R.id.inputSearch)
        placeHolderMessage = findViewById(R.id.placeholderMessage)
        placeholderMessageText = findViewById(R.id.placeholderMessageText)
        placeholderMessageImage = findViewById(R.id.placeholderMessageImage)
        placeholderMessageButtonRefresh = findViewById(R.id.placeholderMessageButtonRefresh)

        val btnClearText = findViewById<ImageView>(R.id.clearTextSearch)
        val trackRecycler = findViewById<RecyclerView>(R.id.trackList)

        adapter.tracks = tracks

        textSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        trackRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecycler.adapter = adapter

        btnClearText.setOnClickListener(){
            textSearch.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(textSearch.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        placeholderMessageButtonRefresh.setOnClickListener(){
            searchTrack()
        }

        val searchTextWatcher = object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchTextString = textSearch.text.toString()
                btnClearText.visibility = visibilityView(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        textSearch.addTextChangedListener(searchTextWatcher)
    }

    fun showInfo(text: String) {
        tracks.clear()
        adapter.notifyDataSetChanged()
        placeHolderMessage.visibility = View.VISIBLE
        when(text){
            TRACK_NOT_FOUND -> {
                placeholderMessageButtonRefresh.visibility = View.GONE
                placeholderMessageText.text = getString(R.string.search_not_found)
                placeholderMessageImage.setImageResource(R.drawable.ic_search_not_found)
            }
            else -> { // пока что для else ошибка сервера. Если появятся еще ошибки, в дальнейшем поменять строчку на: SERVER_NOT_ANSWER -> {
                placeholderMessageButtonRefresh.visibility = View.VISIBLE
                placeholderMessageText.text = getString(R.string.search_server_error)
                placeholderMessageImage.setImageResource(R.drawable.ic_search_server_error)
            }
        }
    }

    fun visibilityView(s: CharSequence?): Int{
        return if (s.isNullOrEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE, searchTextString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textSearch.setText(savedInstanceState.getString(SEARCH_LINE))
    }

    private fun searchTrack(){
        if (textSearch.text.isNotEmpty()){
            itunesService.search(textSearch.text.toString()).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200){
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            placeHolderMessage.visibility = View.GONE
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()){
                            showInfo(TRACK_NOT_FOUND)
                        } else{
                            //showInfo() // использовать для отображения информации на экране
                        }
                    } else{
                        showInfo(SERVER_NOT_ANSWER)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showInfo(SERVER_NOT_ANSWER)
                }
            })
        }
    }
}