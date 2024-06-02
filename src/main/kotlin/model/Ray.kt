package ru.nsu.fit.sckwo.model

class Ray(val origin: Point3D, val direction: Point3D) {
    fun at(t: Double): Point3D = origin + direction * t
}