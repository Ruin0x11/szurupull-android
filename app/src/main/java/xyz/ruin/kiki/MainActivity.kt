package xyz.ruin.kiki

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startUpload()
    }

    private fun startUpload() {
        val intent = Intent(this, UploadActivity::class.java).apply {
        }
        startActivity(intent)
    }
}
