package ru.nsu.fit.sckwo

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.launch


@Composable
fun ToolBar(appState: RaytracingAppState, focusRequester: FocusRequester) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = { Text("Raytracing") },
        actions = {
            IconButton(onClick = {
                expanded = true
                coroutineScope.launch {
                    focusRequester.requestFocus()
                }
            }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Settings")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
//                    appState.saveScene()
                    expanded = false
                }) {
                    Text("Сохранить сцену")
                }
                DropdownMenuItem(onClick = {
//                    appState.loadScene()
                    expanded = false
                }) {
                    Text("Загрузить сцену")
                }
                DropdownMenuItem(onClick = {
//                    appState.saveSettings()
                    expanded = false
                }) {
                    Text("Сохранить настройки рендеринга")
                }
                DropdownMenuItem(onClick = {
//                    appState.loadSettings()
                    expanded = false
                }) {
                    Text("Загрузить настройки рендеринга")
                }
                DropdownMenuItem(onClick = {
                    expanded = false
                }) {
                    if (expanded) {
                        // Save file logic here
                    }
                    Text("Сохранить изображение")
                }
                DropdownMenuItem(onClick = {
//                    appState.exit()
                    expanded = false
                }) {
                    Text("Закрыть")
                }
            }
            Button(onClick = {
                appState.renderController.toggleWireframeMode()
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
                    focusRequester.requestFocus() // Return focus to the main content
                }
            }) {
                Text("Режим проволочного каркаса")
            }
        }
    )
}
