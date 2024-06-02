package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.materials.Material
import java.util.*

class Rectangle(
    val center: Point3D,
    val width: Double,
    val height: Double,
    val material: Material,
) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }

    override fun id(): String {
        return "Rectangle[$uuid]"
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo? {
        // Assume the rectangle is aligned with the xy-plane
        val normal = Point3D(0.0, 0.0, 1.0)
        val t = (center.z - ray.origin.z) / ray.direction.z
        if (t < tMin || t > tMax) {
            return null
        }
        val hitPoint = ray.at(t)
        if (hitPoint.x < center.x - width / 2 || hitPoint.x > center.x + width / 2 ||
            hitPoint.y < center.y - height / 2 || hitPoint.y > center.y + height / 2
        ) {
            return null
        }
        return RayHitInfo.create(hitPoint, t, ray, normal, material)
    }

    override fun translate(offset: Point3D) {
        center.translate(offset)
    }

    override fun linearize(steps: Int): List<LineSegment> {
        val halfWidth = width / 2.0
        val halfHeight = height / 2.0

        val vertices = listOf(
            center + Point3D(-halfWidth, -halfHeight, 0.0),
            center + Point3D(halfWidth, -halfHeight, 0.0),
            center + Point3D(halfWidth, halfHeight, 0.0),
            center + Point3D(-halfWidth, halfHeight, 0.0)
        )

        return listOf(
            LineSegment(vertices[0], vertices[1]),
            LineSegment(vertices[1], vertices[2]),
            LineSegment(vertices[2], vertices[3]),
            LineSegment(vertices[3], vertices[0])
        )
    }
}
