package ru.nsu.fit.sckwo.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.RayTracer
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun RayTracerSettings(
    rayTracer: RayTracer,
    onUpdateSettings: (() -> Unit),
) {
    var maxDepth by remember { mutableStateOf(rayTracer.maxDepthOfProcessing.toFloat()) }
    var backgroundColor by remember { mutableStateOf(HsvColor.from(color = ComposeColor.Red)) }

    LaunchedEffect(rayTracer) {
        maxDepth = rayTracer.maxDepthOfProcessing.toFloat()
        backgroundColor = HsvColor.from(
            ComposeColor(
                rayTracer.backgroundColor.x.toFloat(),
                rayTracer.backgroundColor.y.toFloat(),
                rayTracer.backgroundColor.z.toFloat()
            )
        )
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Настройки рейтресинга", style = MaterialTheme.typography.h6)

        // Maximum Depth Slider
        Text("Цвет фона:")
        ClassicColorPicker(
            color = backgroundColor,
            showAlphaBar = false,
            modifier = Modifier.size(400.dp),
            onColorChanged = { color: HsvColor ->
                backgroundColor = color
                rayTracer.backgroundColor = Color(color.toColor().red, color.toColor().green, color.toColor().blue)
                onUpdateSettings()
            }
        )

        Text("Количество отражений: ${maxDepth.toInt() - 1}")

        Slider(
            value = maxDepth,
            onValueChange = { newValue ->
                maxDepth = newValue
                rayTracer.maxDepthOfProcessing = newValue.toInt()
                onUpdateSettings()
            },
            valueRange = 1f..20f,
            steps = 19,
            modifier = Modifier.width(200.dp)
        )
    }
}
