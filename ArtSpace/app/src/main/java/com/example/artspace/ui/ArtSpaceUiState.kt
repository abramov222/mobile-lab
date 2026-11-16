package com.example.artspace.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.artspace.R

data class ArtSpaceUiState(
    val currentArtwork: Int = 1,
    @DrawableRes val imageId: Int = R.drawable.starry_night,
    @StringRes val titleId: Int = R.string.starry_night_title,
    @StringRes val artistId: Int = R.string.starry_night_artist,
    @StringRes val yearId: Int = R.string.starry_night_year
)
