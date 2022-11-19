package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.btn_search)
        val buttonMedia = findViewById<Button>(R.id.btn_media)
        val buttonSettings = findViewById<Button>(R.id.btn_settings)

        buttonSearch.setOnClickListener {
            Toast.makeText(this, "Нажали кнопку Поиск", Toast.LENGTH_SHORT).show()
        }
        buttonMedia.setOnClickListener {
            Toast.makeText(this, "Нажали кнопку Медиатека", Toast.LENGTH_SHORT).show()
        }
        buttonSettings.setOnClickListener {
            Toast.makeText(this, "Нажали кнопку Настройки", Toast.LENGTH_SHORT).show()
        }
    }
}