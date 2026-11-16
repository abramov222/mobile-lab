package com.example.artspace.ui

import androidx.lifecycle.ViewModel
import com.example.artspace.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val ARTWORK_COUNT = 5

class ArtSpaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArtSpaceUiState())
    val uiState: StateFlow<ArtSpaceUiState> = _uiState.asStateFlow()

    fun showPrevious() {
        val current = _uiState.value.currentArtwork
        showArtwork(if (current == 1) ARTWORK_COUNT else current - 1)
    }

    fun showNext() {
        val current = _uiState.value.currentArtwork
        showArtwork(if (current == ARTWORK_COUNT) 1 else current + 1)
    }

    private fun showArtwork(number: Int) {
        _uiState.value = when (number) {
            1 -> ArtSpaceUiState(
                currentArtwork = 1,
                imageId = R.drawable.starry_night,
                titleId = R.string.starry_night_title,
                artistId = R.string.starry_night_artist,
                yearId = R.string.starry_night_year
            )
            2 -> ArtSpaceUiState(
                currentArtwork = 2,
                imageId = R.drawable.ninth_wave,
                titleId = R.string.ninth_wave_title,
                artistId = R.string.ninth_wave_artist,
                yearId = R.string.ninth_wave_year
            )
            3 -> ArtSpaceUiState(
                currentArtwork = 3,
                imageId = R.drawable.morning_pine_forest,
                titleId = R.string.morning_pine_forest_title,
                artistId = R.string.morning_pine_forest_artist,
                yearId = R.string.morning_pine_forest_year
            )
            4 -> ArtSpaceUiState(
                currentArtwork = 4,
                imageId = R.drawable.great_wave,
                titleId = R.string.great_wave_title,
                artistId = R.string.great_wave_artist,
                yearId = R.string.great_wave_year
            )
            else -> ArtSpaceUiState(
                currentArtwork = 5,
                imageId = R.drawable.girl_pearl_earring,
                titleId = R.string.girl_pearl_earring_title,
                artistId = R.string.girl_pearl_earring_artist,
                yearId = R.string.girl_pearl_earring_year
            )
        }
    }
}
