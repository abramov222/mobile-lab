package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artspace.ui.ArtSpaceViewModel
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

@Composable
fun ArtSpaceApp(
    modifier: Modifier = Modifier,
    viewModel: ArtSpaceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageId = uiState.imageId
    val titleId = uiState.titleId
    val artistId = uiState.artistId
    val yearId = uiState.yearId
    val onPrevious = viewModel::showPrevious
    val onNext = viewModel::showNext

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        if (maxWidth > maxHeight) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArtworkWall(
                    imageId = imageId,
                    contentDescription = stringResource(titleId),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    ArtworkDescriptor(titleId = titleId, artistId = artistId, yearId = yearId)
                    Spacer(modifier = Modifier.height(24.dp))
                    DisplayController(onPrevious = onPrevious, onNext = onNext)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArtworkWall(
                    imageId = imageId,
                    contentDescription = stringResource(titleId),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                ArtworkDescriptor(titleId = titleId, artistId = artistId, yearId = yearId)
                Spacer(modifier = Modifier.height(24.dp))
                DisplayController(onPrevious = onPrevious, onNext = onNext)
            }
        }
    }
}

@Composable
fun ArtworkWall(
    @DrawableRes imageId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Image(
                painter = painterResource(imageId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun ArtworkDescriptor(
    @StringRes titleId: Int,
    @StringRes artistId: Int,
    @StringRes yearId: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(titleId),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light
            )
            val artist = stringResource(artistId)
            val year = stringResource(yearId)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(artist)
                    }
                    append(" ($year)")
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DisplayController(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onPrevious,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(R.string.previous), textAlign = TextAlign.Center)
        }
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(R.string.next), textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpaceAppPreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 360)
@Composable
fun ArtSpaceAppWidePreview() {
    ArtSpaceTheme {
        ArtSpaceApp()
    }
}
