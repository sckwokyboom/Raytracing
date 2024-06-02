package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.materials.Material
import java.util.*
import kotlin.math.abs

class Parallelepiped(
    val center: Point3D,
    val width: Double,
    val height: Double,
    val depth: Double,
    val material: Material,
) : Traceable {
    private val uuid by lazy { UUID.randomUUID() }

    override fun id(): String {
        return "Parallelepiped[$uuid]"
    }

    override fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo? {
//        val halfWidth = width / 2.0
//        val halfHeight = height / 2.0
//        val halfDepth = depth / 2.0
//
//        val min = center - Point3D(halfWidth, halfHeight, halfDepth)
//        val max = center + Point3D(halfWidth, halfHeight, halfDepth)
//
//        var tMinTemp = (min.x - ray.origin.x) / ray.direction.x
//        var tMaxTemp = (max.x - ray.origin.x) / ray.direction.x
//        if (tMinTemp > tMaxTemp) tMinTemp = tMaxTemp.also { tMaxTemp = tMinTemp }
//
//        var tMinT = (min.y - ray.origin.y) / ray.direction.y
//        var tMaxT = (max.y - ray.origin.y) / ray.direction.y
//        if (tMinT > tMaxT) tMinT = tMaxT.also { tMaxT = tMinT }
//
//        tMinTemp = tMinTemp.coerceAtLeast(tMinT)
//        tMaxTemp = tMaxTemp.coerceAtMost(tMaxT)
//
//        tMinT = (min.z - ray.origin.z) / ray.direction.z
//        tMaxT = (max.z - ray.origin.z) / ray.direction.z
//        if (tMinT > tMaxT) tMinT = tMaxT.also { tMaxT = tMinT }
//
//        tMinTemp = tMinTemp.coerceAtLeast(tMinT)
//        tMaxTemp = tMaxTemp.coerceAtMost(tMaxT)

//        if (tMaxTemp >= tMinTemp) {
        val hitPoint = ray.at(tMin)
        val outwardSurfaceNormal = hitPoint.normalized()
        return RayHitInfo.create(hitPoint, tMin, ray, outwardSurfaceNormal, material)
//        }
//        return null
    }

//    private fun computeNormal(point: Point3D, min: Point3D, max: Point3D): Point3D {
//        val epsilon = 1e-4
//        return when {
//            abs(point.x - min.x) < epsilon -> Point3D(-1.0, 0.0, 0.0)
//            abs(point.x - max.x) < epsilon -> Point3D(1.0, 0.0, 0.0)
//            abs(point.y - min.y) < epsilon -> Point3D(0.0, -1.0, 0.0)
//            abs(point.y - max.y) < epsilon -> Point3D(0.0, 1.0, 0.0)
//            abs(point.z - min.z) < epsilon -> Point3D(0.0, 0.0, -1.0)
//            abs(point.z - max.z) < epsilon -> Point3D(0.0, 0.0, 1.0)
//            else -> Point3D(0.0, 0.0, 0.0)
//        }
//    }

    override fun translate(offset: Point3D) {
        center.translate(offset)
    }

    override fun linearize(steps: Int): List<LineSegment> {
        val halfWidth = width / 2.0
        val halfHeight = height / 2.0
        val halfDepth = depth / 2.0

        val vertices = listOf(
            center + Point3D(-halfWidth, -halfHeight, -halfDepth),
            center + Point3D(halfWidth, -halfHeight, -halfDepth),
            center + Point3D(halfWidth, halfHeight, -halfDepth),
            center + Point3D(-halfWidth, halfHeight, -halfDepth),
            center + Point3D(-halfWidth, -halfHeight, halfDepth),
            center + Point3D(halfWidth, -halfHeight, halfDepth),
            center + Point3D(halfWidth, halfHeight, halfDepth),
            center + Point3D(-halfWidth, halfHeight, halfDepth)
        )

        return listOf(
            LineSegment(vertices[0], vertices[1]),
            LineSegment(vertices[1], vertices[2]),
            LineSegment(vertices[2], vertices[3]),
            LineSegment(vertices[3], vertices[0]),
            LineSegment(vertices[4], vertices[5]),
            LineSegment(vertices[5], vertices[6]),
            LineSegment(vertices[6], vertices[7]),
            LineSegment(vertices[7], vertices[4]),
            LineSegment(vertices[0], vertices[4]),
            LineSegment(vertices[1], vertices[5]),
            LineSegment(vertices[2], vertices[6]),
            LineSegment(vertices[3], vertices[7])
        )
    }
}
