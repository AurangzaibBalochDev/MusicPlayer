package com.myapp.androidbasics.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.myapp.androidbasics.MyMusic

class MyViewModel : ViewModel() {

    var Audio by mutableStateOf(emptyList<MyMusic>())
        private set

    fun updateAudio(Audio:List<MyMusic>){
        this.Audio = Audio
    }
}