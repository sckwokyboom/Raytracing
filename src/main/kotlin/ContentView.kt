package ru.nsu.fit.sckwo

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContentView(appState: RaytracingAppState, focusRequester: FocusRequester) {
    val scrollState = rememberScrollState()
    var prevX by remember { mutableStateOf(0.0) }
    var prevY by remember { mutableStateOf(0.0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        appState.renderController.renderImage(
            appState.buffer,
            appState.rayTracer,
            appState.camera,
            onUpdateFunction = {
                appState.imageBitmap = appState.buffer.toComposeImageBitmap()
            },
            onProgressUpdate = { newProgress ->
                appState.progress = newProgress
            }
        )
    }

    Column(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.W, Key.DirectionUp -> appState.camera.moveForward()
                        Key.S, Key.DirectionDown -> appState.camera.moveBackward()
                        Key.A, Key.DirectionLeft -> appState.camera.moveLeft()
                        Key.D, Key.DirectionRight -> appState.camera.moveRight()
                        Key.R -> appState.camera.moveUp()
                        Key.F -> appState.camera.moveDown()
                    }
                    coroutineScope.launch {
                        appState.renderController.renderImage(
                            appState.buffer,
                            appState.rayTracer,
                            appState.camera,
                            onUpdateFunction = {
                                appState.imageBitmap = appState.buffer.toComposeImageBitmap()
                            },
                            onProgressUpdate = { newProgress ->
                                appState.progress = newProgress
                            }
                        )
                    }
                    true
                } else false
            }
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        prevX = offset.x.toDouble()
                        prevY = offset.y.toDouble()
                    },
                    onDrag = { change, _ ->
                        val deltaX = (change.position.x - prevX) * 2 * Math.PI / appState.camera.imageWidth * 0.1
                        val deltaY = (change.position.y - prevY) * 2 * Math.PI / appState.camera.imageHeight * 0.1

                        appState.camera.adjustYaw(deltaX)
                        appState.camera.adjustPitch(-deltaY)
                        prevX = change.position.x.toDouble()
                        prevY = change.position.y.toDouble()
                        change.consume()

                        coroutineScope.launch {
                            appState.renderController.renderImage(
                                appState.buffer,
                                appState.rayTracer,
                                appState.camera,
                                onUpdateFunction = {
                                    appState.imageBitmap = appState.buffer.toComposeImageBitmap()
                                },
                                onProgressUpdate = { newProgress ->
                                    appState.progress = newProgress
                                }
                            )
                        }
                    }
                )
            }
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Column(Modifier.verticalScroll(scrollState).weight(1f)) {
                SettingsPanel(appState)
            }
            Column(
                modifier = Modifier.weight(2f).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(bitmap = appState.imageBitmap, contentDescription = null)
                Text(
                    text = "Rendering: ${(appState.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 8.dp)
                )
                LinearProgressIndicator(
                    progress = appState.progress.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
