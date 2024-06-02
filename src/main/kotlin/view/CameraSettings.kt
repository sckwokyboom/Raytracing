package ru.nsu.fit.sckwo.view


import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.nsu.fit.sckwo.model.Camera


@Composable
fun CameraSettings(camera: Camera, onUpdateSettings: () -> Unit) {
    var fov by remember { mutableStateOf(camera.fieldOfView) }
    var aperture by remember { mutableStateOf(camera.apertureDiameter) }
    var focusDist by remember { mutableStateOf(camera.focusDistance) }
    var samplesPerPixel by remember { mutableStateOf(camera.samplesPerPixel) }
    var movementSpeed by remember { mutableStateOf(camera.movementSpeed) }
    var imageWidth by remember { mutableStateOf(camera.imageWidth) }
    var imageHeight by remember { mutableStateOf(camera.imageHeight) }

    LaunchedEffect(camera) {
        fov = camera.fieldOfView
        aperture = camera.apertureDiameter
        focusDist = camera.focusDistance
        samplesPerPixel = camera.samplesPerPixel
        movementSpeed = camera.movementSpeed
        imageWidth = camera.imageWidth
        imageHeight = camera.imageHeight
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Настройки камеры", style = MaterialTheme.typography.h6)

        // Field of View Slider
        Text("Угол обзора: ${fov.toInt()}°")
        Slider(
            value = fov.toFloat(),
            onValueChange = { newValue ->
                fov = newValue.toDouble()
                camera.fieldOfView = newValue.toDouble()
                onUpdateSettings()
            },
            valueRange = 1f..180f,
            steps = 179,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

//        // Aperture Slider
//        Text("Диаметр апертуры: $aperture")
//        Slider(
//            value = aperture.toFloat(),
//            onValueChange = { newValue ->
//                aperture = newValue.toDouble()
//                camera.apertureDiameter = newValue.toDouble()
//                onUpdateSettings()
//            },
//            valueRange = 0.0f..10f,
//            steps = 199,
//            modifier = Modifier.width(200.dp)  // Applying width here
//        )

        // Focus Distance Slider
//        Text("Focus Distance: $focusDist")
//        Slider(
//            value = focusDist.toFloat(),
//            onValueChange = { newValue ->
//                focusDist = newValue.toDouble()
//                camera.focusDistance = newValue.toDouble()
//                onUpdateSettings()
//            },
//            valueRange = 0.0f..10.0f,
//            steps = 999,
//            modifier = Modifier.width(200.dp)  // Applying width here
//        )

        Text("Количество лучей на пиксель: $samplesPerPixel")
        Slider(

            value = samplesPerPixel.toFloat(),
            onValueChange = { newValue ->
                samplesPerPixel = newValue.toInt()
                camera.samplesPerPixel = samplesPerPixel
                onUpdateSettings()
            },
            valueRange = 1.0f..50.0f,
            steps = 49,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        Text("Скорость передвижения: $movementSpeed")
        Slider(
            value = movementSpeed.toFloat(),
            onValueChange = { newValue ->
                movementSpeed = newValue.toDouble()
                camera.movementSpeed = movementSpeed
                onUpdateSettings()
            },
            valueRange = 0.1f..3.0f,
            steps = 100,
            modifier = Modifier.width(200.dp)  // Applying width here
        )

        // Image Width Integer Field
//        var imageWidth by remember { mutableStateOf(camera.imageWidth) }
        var widthError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = imageWidth.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    if (it in 100..1024) {
                        imageWidth = it
                        camera.imageWidth = it
                        onUpdateSettings()
                        widthError = false
                    } else {
                        widthError = true
                    }
                } ?: run { widthError = true }
            },
            label = { Text("Ширина изображения") },
            isError = widthError,
            modifier = Modifier.fillMaxWidth()
        )
        if (widthError) {
            Text(
                "Width must be between 100 and 1024",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }

        // Image Height Integer Field
//        var imageHeight by remember { mutableStateOf(camera.imageHeight) }
        var heightError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = imageHeight.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let {
                    if (it in 100..1024) {
                        imageHeight = it
                        camera.imageHeight = it
                        onUpdateSettings()
                        heightError = false
                    } else {
                        heightError = true
                    }
                } ?: run { heightError = true }
            },
            label = { Text("Высота изображения") },
            isError = heightError,
            modifier = Modifier.fillMaxWidth()
        )
        if (heightError) {
            Text(
                "Height must be between 100 and 1024",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
    }
}
