package ru.nsu.fit.sckwo

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val appState = remember { RaytracingAppState() }
    val focusRequester = remember { FocusRequester() }

    RaytracingAppTheme {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Raytracing",
            state = WindowState(size = DpSize(Dp(1080F), Dp(720F))),
        ) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            RaytracingApp(appState, focusRequester)
        }
    }
}

@Composable
fun RaytracingApp(appState: RaytracingAppState, focusRequester: FocusRequester) {
    Column {
        ToolBar(appState, focusRequester)
        ContentView(appState, focusRequester)
    }
}