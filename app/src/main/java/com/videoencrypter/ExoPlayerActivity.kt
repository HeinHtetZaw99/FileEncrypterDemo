package com.videoencrypter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ui.PlayerView

class ExoPlayerActivity : AppCompatActivity() {
    private lateinit var videoView: PlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)
        videoView = findViewById(R.id.ep_video_view)
    }
}
