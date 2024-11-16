package com.myapp.androidbasics

import android.Manifest
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil3.compose.AsyncImage
import com.myapp.androidbasics.ui.MyViewModel
import com.myapp.androidbasics.ui.theme.AndroidBasicsTheme
import kotlinx.coroutines.Delay

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel by viewModels<MyViewModel>()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            0
        )

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null
        )?.use { cursor ->
            val music = mutableListOf<MyMusic>()
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getLong(durationColumn) // Duration in milliseconds

                if (duration >= 60000) { // Only include audio files 1 minute or longer
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    music.add(MyMusic(id, name, uri))
                }
            }
            viewModel.updateAudio(music)
        }

        setContent {
            AndroidBasicsTheme {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(viewModel.Audio) { audio ->
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "sdsf",
                                    Modifier
                                        .clickable {
                                            stopAudio()
                                        }
                                        .size(40.dp),
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .background(color = Color.DarkGray)
                                    .clickable {
                                        if (mediaPlayer == null) {
                                            playMusic(audio.uri)
                                        } else {
                                            stopAudio()
                                            playMusic(audio.uri)
                                        }

                                    },
                            ) {

                                Text(
                                    audio.displayname,
                                    fontSize = 20.sp, color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                )

                            }
                        }
                    }
                }

            }
        }
    }

    private fun playMusic(uri: Uri) {
        mediaPlayer = MediaPlayer.create(this, uri).apply {
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                stopAudio()
            }
        }
    }

    private fun stopAudio() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
}

data class MyMusic(val id: Long, val displayname: String, val uri: Uri)