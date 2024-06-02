package ru.nsu.fit.sckwo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import ru.nsu.fit.sckwo.view.CameraSettings
import ru.nsu.fit.sckwo.view.RayTracerSettings
import ru.nsu.fit.sckwo.view.RenderControllerSettings
import java.awt.image.BufferedImage

@Composable
fun SettingsPanel(
    appState: RaytracingAppState,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Настройки",
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))

        RayTracerSettings(appState.rayTracer, onUpdateSettings = {
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
        })

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))

        RenderControllerSettings(appState.renderController) {
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

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))

        CameraSettings(appState.camera) {
            if (appState.camera.imageWidth > 50 && appState.camera.imageHeight > 50) {
                val image =
                    if (appState.camera.imageWidth != appState.buffer.width || appState.camera.imageHeight != appState.buffer.height) {
                        val newImage = BufferedImage(
                            appState.camera.imageWidth,
                            appState.camera.imageHeight,
                            BufferedImage.TYPE_INT_ARGB
                        )
                        appState.buffer = newImage
                        newImage
                    } else {
                        appState.buffer
                    }
                appState.renderController.renderImage(
                    image,
                    appState.rayTracer,
                    appState.camera,
                    onUpdateFunction = {
                        appState.imageBitmap = image.toComposeImageBitmap()
                    },
                    onProgressUpdate = { newProgress ->
                        appState.progress = newProgress
                    }
                )
            }
        }

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    }
}
