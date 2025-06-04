package app.naga.audiotranscription.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainBottomBar()
        }
    ) { innerPadding ->
        MainBody(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
fun MainBody(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Transcribed text will appear here")
        }
    }
}

@Composable
fun MainBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { /* TODO: 音声録音処理 */ }, modifier = Modifier.size(72.dp)) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewMainContent() {
    MainContent()
}