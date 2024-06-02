package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.materials.Material

/**
 * @param t parameter along the ray along which the intersection occurs;
 * @param isFrontFace flag indicating whether the point is on the visible side of the object
 */
data class RayHitInfo(
    val intersectionPoint: Point3D,
    val t: Double,
    val normal: Point3D,
    val isFrontFace: Boolean,
    val material: Material,
) {
    companion object {
        fun create(
            intersectionPoint: Point3D,
            t: Double,
            ray: Ray,
            outwardSurfaceNormal: Point3D,
            material: Material,
        ): RayHitInfo {
            val isFrontFace = ray.direction.dot(outwardSurfaceNormal) < 0
            val normal = if (isFrontFace) outwardSurfaceNormal else -outwardSurfaceNormal
            return RayHitInfo(intersectionPoint, t, normal, isFrontFace, material)
        }
    }

}
