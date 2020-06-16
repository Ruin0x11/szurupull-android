package xyz.ruin.kiki

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import xyz.ruin.kiki.model.Link

class UploadAutoActivity : Activity() {
    private lateinit var link: Link

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        link = handleIntent(intent)
        sendLink(link)
        finish()
    }

    private fun handleIntent(intent: Intent): Link =
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleTextUrl(intent) // Handle text being sent
                } else {
                    Link("")
                }
            }
            else -> Link("")
        }

    private fun handleTextUrl(intent: Intent): Link {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        return Link(url!!)
    }

    private fun sendLink(link: Link) {
        val networkIntent = Intent(this, UploadService::class.java)
        networkIntent.putExtra("action", UploadService.INTENT_POST)
        networkIntent.putExtra("link", link)
        startService(networkIntent)
    }
}
