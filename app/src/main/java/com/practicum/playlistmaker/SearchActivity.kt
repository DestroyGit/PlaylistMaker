package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Array

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_LINE = "SEARCH_LINE"
        const val TRACK_NOT_FOUND = "NOT FOUND"
        const val SERVER_NOT_ANSWER = "SERVER ERROR"
        const val SEARCH_PREFERENCES = "SEARCH_PREFERENCES"
        const val TRACKS_FOR_PLAYER = "TRACKS_FOR_PLAYER"
    }

    private lateinit var textSearch: EditText
    private lateinit var placeHolderMessage: LinearLayout
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderMessageImage: ImageView
    private lateinit var placeholderMessageButtonRefresh: Button
    private lateinit var btnClearText: ImageView
    private lateinit var trackRecycler: RecyclerView
    private lateinit var historySearched: LinearLayout
    private lateinit var historyClearTracks: Button
    private lateinit var searchSharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var historyTrackRecycler: RecyclerView

    private var searchTextString = ""

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val gson = Gson()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private val tracks = ArrayList<Track>()

    private fun initView() {
        textSearch = findViewById(R.id.inputSearch)
        placeHolderMessage = findViewById(R.id.placeholderMessage)
        placeholderMessageText = findViewById(R.id.placeholderMessageText)
        placeholderMessageImage = findViewById(R.id.placeholderMessageImage)
        placeholderMessageButtonRefresh = findViewById(R.id.placeholderMessageButtonRefresh)
        searchSharedPrefs = getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(searchSharedPrefs, gson)
        historyClearTracks = findViewById(R.id.historyClearTracks)
        historySearched = findViewById(R.id.historySearched)
        historyTrackRecycler = findViewById(R.id.historyTrackList)
        btnClearText = findViewById(R.id.clearTextSearch)
        trackRecycler = findViewById(R.id.trackList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initView()

        /**
         * Работа адаптера хранения истории
         */
        historyAdapter = TrackAdapter {
            startPlayerActivity(it)
//            searchHistory.removeTrack(it)
//            if (searchHistory.getHistory()?.isEmpty() == true){
//                historySearched.visibility = View.GONE
//            }
//            refreshHistory()
        }
        historyTrackRecycler.adapter = historyAdapter
        historyTrackRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        refreshHistory()

        /**
         * Кнопка очищения истории поиска
         */
        historyClearTracks.setOnClickListener() {
            searchHistory.clearHistory()
            historySearched.visibility = View.GONE
            refreshHistory()
        }

        /**
         * Нажатие на список песен
         */
        adapter = TrackAdapter {
            searchHistory.addTrack(it)
            startPlayerActivity(it)
        }

        /**
         * работа адаптера поиска музыки
         */
        trackRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecycler.adapter = adapter
        adapter.tracks = tracks

        /**
         * фокусировка на строке поиска
         */
        textSearch.setOnFocusChangeListener() { _, hasFocus ->
            historySearched.visibility =
                if (hasFocus && textSearch.text.isEmpty() && searchHistory.getHistory().isNotEmpty()
                ) {
                    placeHolderMessage.visibility = View.GONE
                    refreshHistory()
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        /**
         * обработка нажатия на Enter на клавиатуре
         */
        textSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        /**
         * очистка списка треков в поиске
         */
        btnClearText.setOnClickListener() {
            textSearch.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(textSearch.windowToken, 0)
            tracks.clear()
            placeHolderMessage.visibility = View.GONE
            adapter.notifyDataSetChanged()
            refreshHistory()
        }

        /**
         * обработка нажатия на кнопку "Обновить", когда нет сети
         */
        placeholderMessageButtonRefresh.setOnClickListener() {
            searchTrack()
        }

        /**
         * обработка введения символов в поисковой строке
         */
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                historySearched.visibility =
                    if (textSearch.hasFocus() && p0?.isEmpty() == true && searchHistory.getHistory()
                            ?.isEmpty() == false
                    ) View.VISIBLE else View.GONE
                searchTextString = textSearch.text.toString()
                btnClearText.visibility = visibilityView(p0)
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        textSearch.addTextChangedListener(searchTextWatcher)
    }

    private fun startPlayerActivity(track: Track){
        val intent = Intent(this,PlayerActivity::class.java)
        intent.putExtra(TRACKS_FOR_PLAYER, gson.toJson(track))
        startActivity(intent)
    }

    private fun refreshHistory() {
        if (searchHistory.getHistory()?.isEmpty() == true) {
            historyAdapter.notifyDataSetChanged()
            return
        }
        historyAdapter.tracks = searchHistory.getHistory() as ArrayList<Track>
        historyAdapter.notifyDataSetChanged()
    }

    fun showInfo(text: String) {
        tracks.clear()
        adapter.notifyDataSetChanged()
        placeHolderMessage.visibility = View.VISIBLE
        when (text) {
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

    fun visibilityView(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
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

    private fun searchTrack() {
        if (textSearch.text.isNotEmpty()) {
            itunesService.search(textSearch.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                placeHolderMessage.visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showInfo(TRACK_NOT_FOUND)
                            } else {
                                //showInfo() // использовать для отображения информации на экране
                            }
                        } else {
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