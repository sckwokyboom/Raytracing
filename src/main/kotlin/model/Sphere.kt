package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.materials.Material
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class Sphere(val center: Point3D, val radius: Double, val material: Material) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }
    override fun id(): String {
        return "Sphere[$uuid]"
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo? {
        val originToCenter = ray.origin - center
        val squareOfRayDirectionLength = ray.direction.dot(ray.direction)
        val halfB = originToCenter.dot(ray.direction)
        val c = originToCenter.dot(originToCenter) - radius * radius
        val discriminant = halfB * halfB - squareOfRayDirectionLength * c
        if (discriminant < 0) {
            return null
        }
        val sqrtDiscriminant = sqrt(discriminant)
        var root = (-halfB - sqrtDiscriminant) / squareOfRayDirectionLength

        if (root < tMin || root > tMax) {
            root = (-halfB + sqrtDiscriminant) / squareOfRayDirectionLength
            if (root < tMin || root > tMax) {
                return null
            }
        }
        val hitPoint = ray.at(root)
        val outwardSurfaceNormal = (hitPoint - center) / radius
        return RayHitInfo.create(hitPoint, root, ray, outwardSurfaceNormal, material)
    }

    override fun translate(offset: Point3D) {
        center.translate(offset)
    }

    override fun linearize(steps: Int): List<LineSegment> {
        val segments = mutableListOf<LineSegment>()

        for (i in 0 until steps) {
            val theta1 = 2 * Math.PI * i / steps
            val theta2 = 2 * Math.PI * (i + 1) / steps
            for (j in 0 until steps) {
                val phi = Math.PI * j / steps
                val p1 = center + Point3D(
                    radius * cos(theta1) * sin(phi),
                    radius * sin(theta1) * sin(phi),
                    radius * cos(phi)
                )
                val p2 = center + Point3D(
                    radius * cos(theta2) * sin(phi),
                    radius * sin(theta2) * sin(phi),
                    radius * cos(phi)
                )
                segments.add(LineSegment(p1, p2))
            }
        }

        for (i in 0 until steps) {
            val theta = Math.PI * 2 * i / steps
            for (j in 0 until steps) {
                val phi1 = Math.PI * j / steps
                val phi2 = Math.PI * (j + 1) / steps
                val p1 = center + Point3D(
                    radius * cos(theta) * sin(phi1),
                    radius * sin(theta) * sin(phi1),
                    radius * cos(phi1)
                )
                val p2 = center + Point3D(
                    radius * cos(theta) * sin(phi2),
                    radius * sin(theta) * sin(phi2),
                    radius * cos(phi2)
                )
                segments.add(LineSegment(p1, p2))
            }
        }

        return segments
    }

}
