package ru.nsu.fit.sckwo.model

interface Traceable {
    /**
     * @param tMin min parameter by which it is possible to move along the ray.
     * @param tMax max parameter by which it is possible to move along the ray.
     */
    fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo?
    fun translate(offset: Point3D)
    fun id(): String? = null
    fun linearize(steps: Int): List<LineSegment>
}
