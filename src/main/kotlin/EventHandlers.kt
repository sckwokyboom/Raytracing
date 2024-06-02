package ru.nsu.fit.sckwo

import ru.nsu.fit.sckwo.model.Camera
import androidx.compose.ui.input.key.*

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
