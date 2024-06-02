package ru.nsu.fit.sckwo.model

import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import androidx.compose.ui.graphics.Color as ComposeColor

class Point3D(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
) {
    constructor(x: Number, y: Number, z: Number) : this(x.toDouble(), y.toDouble(), z.toDouble())

    companion object {
        val ZERO = Point3D()
        val ONE = Point3D(1, 1, 1)

        fun random(from: Double = 0.0, until: Double = 1.0): Point3D =
            Point3D(Random.nextDouble(from, until), Random.nextDouble(from, until), Random.nextDouble(from, until))

        fun randomInUnitSphere(): Point3D {
            while (true) {
                val vector = random(0.0, 1.0)
                if (vector.length() < 1) return vector
            }
        }

        fun randomInUnitDisk(): Point3D {
            while (true) {
                val vector = Point3D(Random.nextDouble(0.0, 1.0), Random.nextDouble(-1.0, 1.0), 0)
                if (vector.length() < 1) return vector
            }
        }

    }

    operator fun unaryMinus(): Point3D = Point3D(-x, -y, -z)
    operator fun get(i: Int): Double = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }

    operator fun plus(other: Point3D): Point3D = Point3D(x + other.x, y + other.y, z + other.z)
    operator fun plus(scalar: Double): Point3D = Point3D(x + scalar, y + scalar, z + scalar)
    operator fun minus(other: Point3D): Point3D = Point3D(x - other.x, y - other.y, z - other.z)
    operator fun minus(scalar: Double): Point3D = Point3D(x - scalar, y - scalar, z - scalar)
    operator fun times(other: Point3D): Point3D = Point3D(x * other.x, y * other.y, z * other.z)
    operator fun times(scalar: Double): Point3D = Point3D(x * scalar, y * scalar, z * scalar)
    operator fun div(other: Point3D): Point3D = Point3D(x / other.x, y / other.y, z / other.z)
    operator fun div(scalar: Double): Point3D = Point3D(x / scalar, y / scalar, z / scalar)
    operator fun divAssign(scalar: Double) {
        x /= scalar
        y /= scalar
        z /= scalar
    }

    private fun l2norm(): Double = sqrt(x * x + y * y + z * z)
    fun length(): Double = l2norm()

    fun dot(other: Point3D): Double = x * other.x + y * other.y + z * other.z

    fun isNearZero(): Boolean = (x * x + y * y + z * z) < 1e-8

    fun translate(offset: Point3D) {
        x += offset.x
        y += offset.y
        z += offset.z
    }

    fun normalized(): Point3D = this / length()

    // TODO: explanation
    fun cross(other: Point3D): Point3D =
        Point3D(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)

    fun reflect(normal: Point3D): Point3D = this - normal * 2.0 * this.dot(normal)

    fun refract(normal: Point3D, etaiOverEtat: Double): Point3D {
        val cosTheta = min((-this).dot(normal), 1.0)
        val rOutPerp = (this + normal * cosTheta) * etaiOverEtat
        val rOutParallel = normal * (-sqrt((1.0 - rOutPerp.l2norm()).absoluteValue))
        return rOutPerp + rOutParallel
    }
}


typealias Color = Point3D

//fun Color.toAWTColor(samplesPerPixel: Int, gamma: Double = 2.2): Int {
//    fun Double.clamp(min: Double, max: Double): Double = when {
//        this < min -> min
//        this > max -> max
//        else -> this
//    }
//
//    fun scaleTo255(x: Double) = (256 * sqrt(x).clamp(0.0, 0.990)).toInt()
//
//    val scale = 1.0 / samplesPerPixel
//    val r = scaleTo255(x * scale)
//    val g = scaleTo255(y * scale)
//    val b = scaleTo255(z * scale)
//    return java.awt.Color(r, g, b).rgb
//}

fun Color.applyGammaCorrection(gamma: Double): Point3D {
    val invGamma = 1.0 / gamma
    return Point3D(
        x.pow(invGamma),
        y.pow(invGamma),
        z.pow(invGamma)
    )
}

fun Color.toAWTColor(samplesPerPixel: Int, gamma: Double = 2.2): Int {
    // Scale the color by the number of samples and apply gamma correction
    val scale = 1.0 / samplesPerPixel
    val correctedColor = (this * scale).applyGammaCorrection(gamma)

    // Clamp values to [0, 1] and convert to [0, 255]
    val r = (256 * correctedColor.x.coerceIn(0.0, 0.990)).toInt()
    val g = (256 * correctedColor.y.coerceIn(0.0, 0.990)).toInt()
    val b = (256 * correctedColor.z.coerceIn(0.0, 0.990)).toInt()
    val a = 255  // Full opacity

    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

fun Color.toComposeColor(): ComposeColor {
    return ComposeColor(x.toFloat(), y.toFloat(), z.toFloat(), 1f)
}