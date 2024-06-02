package ru.nsu.fit.sckwo.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.nsu.fit.sckwo.model.RenderController

@Composable
fun RenderControllerSettings(renderController: RenderController, onUpdateSettings: () -> Unit) {
    var repaintIntervalMs by remember { mutableStateOf(renderController.repaintIntervalMs.toFloat()) }
    var batchSize by remember { mutableStateOf(renderController.batchSize.toFloat()) }
    var gamma by remember { mutableStateOf(renderController.gamma) }

    LaunchedEffect(renderController) {
        repaintIntervalMs = renderController.repaintIntervalMs.toFloat()
        batchSize = renderController.batchSize.toFloat()
        gamma = renderController.gamma
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Настройки рендера", style = MaterialTheme.typography.h6)

//        Text("Repaint Interval (ms): ${repaintIntervalMs.toInt()}")
//
//        Slider(
//            value = repaintIntervalMs,
//            onValueChange = { newValue ->
//                repaintIntervalMs = newValue
//                renderController.repaintIntervalMs = newValue.toLong()
//                onUpdateSettings()
//            },
//            valueRange = 10f..200f,
//            steps = 19,
//            modifier = Modifier.width(200.dp)
//        )

        Text("Размер чанка: ${batchSize.toInt()}")

        Slider(
            value = batchSize,
            onValueChange = { newValue ->
                batchSize = newValue
                renderController.batchSize = newValue.toInt()
                onUpdateSettings()
            },
            valueRange = 1f..200f,
            steps = 199,
            modifier = Modifier.width(200.dp)
        )

        Text("Гамма: $gamma")
        Slider(
            value = gamma.toFloat(),
            onValueChange = { newValue ->
                gamma = newValue.toDouble()
                renderController.gamma = newValue.toDouble()
                onUpdateSettings()
            },
            valueRange = 0.0f..10.0f,
            steps = 100,
            modifier = Modifier.width(200.dp)
        )
    }
}
