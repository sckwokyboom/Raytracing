package ru.nsu.fit.sckwo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toComposeImageBitmap
import ru.nsu.fit.sckwo.model.Camera
import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.RayTracer
import ru.nsu.fit.sckwo.model.RenderController
import ru.nsu.fit.sckwo.model.scene.Scene
import java.awt.image.BufferedImage

class RaytracingAppState {
    var scene by mutableStateOf(Scene.default())
    var rayTracer by mutableStateOf(RayTracer(scene, 20, Color(1.0, 0.0, 0.0)))
    var camera by mutableStateOf(Camera.default())
    var renderController by mutableStateOf(RenderController.default())
    var buffer by mutableStateOf(
        BufferedImage(
            camera.imageWidth,
            camera.imageHeight,
            BufferedImage.TYPE_INT_ARGB
        )
    )
    var imageBitmap by mutableStateOf(buffer.toComposeImageBitmap())
    var progress by mutableStateOf(0.0)
}
