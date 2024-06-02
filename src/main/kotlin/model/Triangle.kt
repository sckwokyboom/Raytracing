package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.materials.Material
import java.util.*

class Triangle(
    var v0: Point3D,
    var v1: Point3D,
    var v2: Point3D,
    val material: Material,
) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo? {
        val edge1 = v1 - v0
        val edge2 = v2 - v0
        val h = ray.direction.cross(edge2)
        val a = edge1.dot(h)

        if (a > -1e-10 && a < 1e-10) {
            return null
        }

        val f = 1.0 / a
        val s = ray.origin - v0
        val u = f * (s.dot(h))

        if (u < 0.0 || u > 1.0) {
            return null
        }

        val q = s.cross(edge1)
        val v = f * ray.direction.dot(q)

        if (v < 0.0 || u + v > 1.0) {
            return null
        }

        val t = f * edge2.dot(q)
        if (t < tMin || t > tMax) {
            return null
        }

        val hitPoint = ray.at(t)
        val outwardNormal = edge1.cross(edge2).normalized()
        return RayHitInfo.create(hitPoint, t, ray, outwardNormal, material)
    }

    override fun translate(offset: Point3D) {
        v0 += offset
        v1 += offset
        v2 += offset
    }

    override fun id(): String {
        return "Triangle[$uuid]"
    }

    override fun linearize(steps: Int): List<LineSegment> {
        return listOf(
            LineSegment(v0, v1),
            LineSegment(v1, v2),
            LineSegment(v2, v0)
        )
    }
}