package ru.nsu.fit.sckwo.model

import java.awt.Point
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Camera(
    position: Point3D,
    lookAt: Point3D,
    upVector: Point3D,
    fieldOfView: Double,
    var apertureDiameter: Double,
    focusDistance: Double,
    var imageWidth: Int,
    var imageHeight: Int,
    var samplesPerPixel: Int = 5,
) {
    private val aspectRatio: Double get() = imageWidth.toDouble() / imageHeight.toDouble()
    private val lensRadius: Double get() = apertureDiameter / 2

    private var v: Point3D = Point3D.ZERO
    private var u: Point3D = Point3D.ZERO
    private var w: Point3D = Point3D.ZERO
    private var lowerLeftCorner: Point3D = Point3D.ZERO
    private var horizontal: Point3D = Point3D.ZERO
    private var vertical: Point3D = Point3D.ZERO

    //TODO: changeable??
    var movementSpeed: Double = 0.1

    private var yaw = 0.0
    private var pitch = 0.0
    private val maxPitch = Math.PI / 2 - 0.01

    fun ray(s: Double, t: Double): Ray {
        val randomDisk = Point3D.randomInUnitDisk() * lensRadius
        val offset = u * randomDisk.x + v * randomDisk.y
        return Ray(
            position + offset,
            lowerLeftCorner + horizontal * s + vertical * t - position - offset
        )
    }

    var upVector = upVector
    var position = position
    var lookAt = lookAt

    var vup: Point3D = upVector
        set(value) {
            field = value
            updateCamera()
        }
    var fieldOfView: Double = fieldOfView
        set(value) {
            field = value
            updateProjection()
        }
    var focusDistance: Double = focusDistance
        set(value) {
            field = value
            updateProjection()
        }


    fun adjustYaw(delta: Double) {
        yaw += delta
        updateCamera()
    }

    fun adjustPitch(delta: Double) {
        pitch = (pitch + delta).coerceIn(-maxPitch, maxPitch)
        updateCamera()
    }

    private fun moveInDirection(x: Double, y: Double, z: Double) {
        position += Point3D(x, y, z) * movementSpeed
        updateCamera()
    }

    fun moveForward() = moveInDirection(cos(pitch) * cos(yaw), sin(pitch), cos(pitch) * sin(yaw))
    fun moveBackward() = moveInDirection(-cos(pitch) * cos(yaw), -sin(pitch), -cos(pitch) * sin(yaw))
    fun moveLeft() = moveInDirection(sin(yaw), 0.0, -cos(yaw))
    fun moveRight() = moveInDirection(-sin(yaw), 0.0, cos(yaw))
    fun moveUp() = run { position += upVector * movementSpeed; updateCamera() }
    fun moveDown() = run { position -= upVector * movementSpeed; updateCamera() }


    init {
        updateCamera()
    }

    private fun updateCamera() {
        val forward = Point3D(cos(pitch) * cos(yaw), sin(pitch), cos(pitch) * sin(yaw))
        lookAt = position + forward
        w = (position - lookAt).normalized()
        u = upVector.cross(w).normalized()
        v = w.cross(u)
        updateProjection()
    }

    private fun updateProjection() {
        val theta = Math.toRadians(fieldOfView)
        val halfHeight = tan(theta / 2)
        val halfWidth = aspectRatio * halfHeight
        lowerLeftCorner = position - (u * halfWidth + v * halfHeight + w) * focusDistance
        horizontal = u * 2.0 * halfWidth * focusDistance
        vertical = v * 2.0 * halfHeight * focusDistance
    }

    fun project(point: Point3D): Point? {
        val cameraSpacePoint = point - position
        val x = cameraSpacePoint.dot(u)
        val y = cameraSpacePoint.dot(v)
        val z = cameraSpacePoint.dot(w)

        if (z >= 0) {
            return null
        }

        val theta = Math.toRadians(fieldOfView)
        val halfHeight = tan(theta / 2)
        val halfWidth = aspectRatio * halfHeight

        val screenX = (x / (-z * halfWidth)) * (imageWidth / 2) + (imageWidth / 2)
        val screenY = (y / (-z * halfHeight)) * (imageHeight / 2) + (imageHeight / 2)

        return Point(screenX.toInt(), (imageHeight - screenY.toInt()))
    }

    companion object {
        fun default() = Camera(
            position = Point3D.ONE,
            lookAt = Point3D.ZERO,
            upVector = Point3D(0.0, 1.0, 0.0),
            fieldOfView = 90.0,
            apertureDiameter = 0.0,
            focusDistance = 10.0,
            imageWidth = 400,
            imageHeight = 400,
            samplesPerPixel = 20
        )
    }

}