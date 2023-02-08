package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonReply = findViewById<TextView>(R.id.btn_reply)
        val buttonSupport = findViewById<TextView>(R.id.btn_support)
        val buttonForward = findViewById<TextView>(R.id.btn_forward)

        buttonReply.setOnClickListener() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_reply))
            intent.type = "text/plain"
            startActivity(intent)
        }

        buttonSupport.setOnClickListener() {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mail)))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_title)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.support_message)
            )
            startActivity(intent)
        }

        buttonForward.setOnClickListener() {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_offer)))
            startActivity(intent)
        }
    }
}