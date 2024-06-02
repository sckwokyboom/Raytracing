package ru.nsu.fit.sckwo

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import ru.nsu.fit.sckwo.model.*
import ru.nsu.fit.sckwo.model.scene.Scene
import ru.nsu.fit.sckwo.model.scene.loadScene
import ru.nsu.fit.sckwo.model.scene.saveScene
import ru.nsu.fit.sckwo.view.CameraSettings
import ru.nsu.fit.sckwo.view.RayTracerSettings
import ru.nsu.fit.sckwo.view.RenderControllerSettings
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val DarkColorPalette = darkColors(
    primary = Black,
//    primaryVariant = CustomSecondaryColor,
    secondary = Black,
    background = Black,
    surface = Black,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorPalette = lightColors(
    primary = Black,
//    primaryVariant = CustomSecondaryColor,
    secondary = Black,
    background = White,
    surface = White,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun YourAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
//        typography = Typography,
//        shapes = Shapes,
        content = content
    )
}

fun main() = application {
//    var scene = Scene.default()
//    var rayTracer = RayTracer(scene, 20, Color(0.5, 0.7, 1.0))
//    var camera = Camera.default()
//    var renderController = RenderController.default()
    var scene by remember { mutableStateOf(Scene.default()) }
    var rayTracer by remember { mutableStateOf(RayTracer(scene, 20, Color(1.0, 0.0, 0.0))) }
    var camera by remember { mutableStateOf(Camera.default()) }
    var renderController by remember { mutableStateOf(RenderController.default()) }

    var buffer = remember {
        BufferedImage(
            camera.imageWidth,
            camera.imageHeight,
            BufferedImage.TYPE_INT_ARGB
        )
    }


    var imageBitmap by remember { mutableStateOf(buffer.toComposeImageBitmap()) }
    var progress by remember { mutableStateOf(0.0) }
    renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
        imageBitmap = buffer.toComposeImageBitmap()
    }, onProgressUpdate = { newProgress ->
        progress = newProgress
    })
    val scope = rememberCoroutineScope()
    YourAppTheme {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Raytracing",
            state = WindowState(size = DpSize(Dp(1080F), Dp(720F))),
        ) {
            val focusRequester = remember { FocusRequester() }
            val scrollState = rememberScrollState()
            var prevX by remember { mutableStateOf(0) }
            var prevY by remember { mutableStateOf(0) }

            Column {
                ToolBar(
                    onSaveScene = {
                        saveScene(scene, "scene_test.json")
                    },
                    onLoadScene = {
                        val loadedScene = loadScene("scene_test.json")
                        if (loadedScene != null) {
                            scene = loadedScene
                            rayTracer.scene = loadedScene
                            renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                                imageBitmap = buffer.toComposeImageBitmap()
                            }, onProgressUpdate = { newProgress ->
                                progress = newProgress
                            })
                        }
                    },
                    onSaveSettings = {
                        saveSettings(camera, renderController, rayTracer, "settings.json")
                    },
                    onLoadSettings = {
                        val settings = loadSettings("settings.json")
                        camera = settings["camera"] as Camera
                        println(camera.samplesPerPixel)
                        renderController = settings["renderController"] as RenderController
                        rayTracer = settings["rayTracer"] as RayTracer
                        rayTracer.scene = scene
                    },
                    onSaveImage = {
//                    SaveFile("Save Image", "png")?.let { file ->
//                        saveImage(buffer, file)
//                    }
                    },
                    onExit = {
                        exitApplication()
                    },
                    renderController,
                    buffer,
                    rayTracer,
                    camera,
                    {
                        imageBitmap = buffer.toComposeImageBitmap()
                    }, onProgressUpdate = { newProgress ->
                        progress = newProgress
                    }

                )
                Row(modifier = Modifier.focusRequester(focusRequester).pointerInput(Unit) {
                    detectDragGestures(onDragStart = { offset ->
                        prevX = offset.x.toInt()
                        prevY = offset.y.toInt()
                    }, onDrag = { change, _ ->
                        val deltaX = (change.position.x.toInt() - prevX) * 2 * Math.PI / camera.imageWidth * 0.1
                        val deltaY = (change.position.y.toInt() - prevY) * 2 * Math.PI / camera.imageHeight * 0.1

                        camera.adjustYaw(deltaX)
                        camera.adjustPitch(-deltaY)
                        prevX = change.position.x.toInt()
                        prevY = change.position.y.toInt()
                        change.consume()
                        renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                            imageBitmap = buffer.toComposeImageBitmap()
                        }, onProgressUpdate = { newProgress ->
                            progress = newProgress
                        })
                    })
                }.focusable().onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        handleKeyEvent(keyEvent, camera)
                        renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                            imageBitmap = buffer.toComposeImageBitmap()
                        }, onProgressUpdate = { newProgress ->
                            progress = newProgress
                        })
                        true
                    } else false
                }) {
                    Column(Modifier.verticalScroll(scrollState).weight(1f)) {
                        settingsPanel(
                            scene,
                            rayTracer,
                            camera,
                            renderController,
                            buffer,
                            { buffer = it },
                            { imageBitmap = it.toComposeImageBitmap() },
                            { newProgress ->
                                progress = newProgress
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(bitmap = imageBitmap, contentDescription = null)
                        Text(
                            text = "Rendering: ${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        LinearProgressIndicator(
                            progress = progress.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
fun settingsPanel(
    scene: Scene,
    rayTracer: RayTracer,
    camera: Camera,
    renderController: RenderController,
    buffer: BufferedImage,
    updateBuffer: (BufferedImage) -> Unit,
    updateImage: (BufferedImage) -> Unit,
    onProgressUpdate: (Double) -> Unit,
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

        RayTracerSettings(rayTracer, onUpdateSettings = {
            renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                updateImage(buffer)
            }, onProgressUpdate = onProgressUpdate)
        })

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))

        RenderControllerSettings(renderController) {
            renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                updateImage(buffer)
            }, onProgressUpdate = onProgressUpdate)
        }

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))

        CameraSettings(camera) {
            if (camera.imageWidth > 50 && camera.imageHeight > 50) {
                val image = if (camera.imageWidth != buffer.width || camera.imageHeight != buffer.height) {
                    val image = BufferedImage(
                        camera.imageWidth,
                        camera.imageHeight,
                        BufferedImage.TYPE_INT_ARGB
                    )
                    updateBuffer(image)
                    image
                } else {
                    buffer
                }
                renderController.renderImage(image, rayTracer, camera, onUpdateFunction = {
                    updateImage(image)
                }, onProgressUpdate = onProgressUpdate)
            }
        }

        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
    }
}

@Composable
fun ToolBar(
    onSaveScene: () -> Unit,
    onLoadScene: () -> Unit,
    onSaveSettings: () -> Unit,
    onLoadSettings: () -> Unit,
    onSaveImage: () -> Unit,
    onExit: () -> Unit,
    renderController: RenderController,
    buffer: BufferedImage,
    rayTracer: RayTracer,
    camera: Camera,
    updateImage: (BufferedImage) -> Unit,
    onProgressUpdate: (Double) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Raytracing") },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Settings")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    onSaveScene()
                    expanded = false
                }) {
                    Text("Сохранить сцену")
                }
                DropdownMenuItem(onClick = {
                    onLoadScene()
                    expanded = false
                }) {
                    Text("Загрузить сцену")
                }
                DropdownMenuItem(onClick = {
                    onSaveSettings()
                    expanded = false
                }) {
                    Text("Сохранить настройки рендеринга")
                }
                DropdownMenuItem(onClick = {
                    onLoadSettings()
                    expanded = false
                }) {
                    Text("Загрузить настройки рендеринга")
                }
                DropdownMenuItem(onClick = {
                    expanded = false
                }) {
                    if (expanded) {
//                        SaveFile("Save Scene", "json") { filePath ->
//                            scope.launch {
//
//                            }
//                        }
                    }
                    Text("Сохранить изображение")

                }
                DropdownMenuItem(onClick = {
                    onExit()
                    expanded = false
                }) {
                    Text("Закрыть")
                }
            }
            Button(onClick = {
                renderController.toggleWireframeMode()
                renderController.renderImage(buffer, rayTracer, camera, onUpdateFunction = {
                    updateImage(buffer)
                }, onProgressUpdate = onProgressUpdate)
            }) {
                Text("Режим проволочного каркаса")
            }
        }
    )
}

fun handleKeyEvent(keyEvent: KeyEvent, camera: Camera) {
    when (keyEvent.key) {
        Key.W, Key.DirectionUp -> camera.moveForward()
        Key.S, Key.DirectionDown -> camera.moveBackward()
        Key.A, Key.DirectionLeft -> camera.moveLeft()
        Key.D, Key.DirectionRight -> camera.moveRight()
        Key.R -> camera.moveUp()
        Key.F -> camera.moveDown()
    }
}

fun saveImage(buffer: BufferedImage, fileName: String) {
    val file = File(fileName)
    ImageIO.write(buffer, "png", file)
}

@Composable
fun SaveFile(dialogTitle: String, fileType: String): String? {
    var showFilePicker by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<String?>(null) }

    Button(onClick = { showFilePicker = true }) {
        Text(dialogTitle)
    }

    val fileTypes = listOf(fileType)
    FilePicker(show = showFilePicker, fileExtensions = fileTypes) { platformFile ->
        showFilePicker = false
        platformFile?.let {
            selectedFile = it.path
        }
    }

    return selectedFile
}