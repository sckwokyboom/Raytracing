package ru.nsu.fit.sckwo.model

import ru.nsu.fit.sckwo.model.scene.Scene

class RayTracer(var scene: Scene?, var maxDepthOfProcessing: Int, var backgroundColor: Color) {
    fun color(ray: Ray): Color {
        return colorRecursively(ray, maxDepthOfProcessing)
    }

    private fun colorRecursively(ray: Ray, currentDepth: Int): Color {
        if (currentDepth <= 0) return Point3D.ZERO

        val hitRecord = scene?.hit(ray, 0.001, Double.POSITIVE_INFINITY)
        return if (hitRecord != null) {
            val scattered = hitRecord.material.scatter(ray, hitRecord)
            val emitted = hitRecord.material.emitted()
            if (scattered != null) {
                emitted + scattered.attenuation * colorRecursively(scattered.scatteredRay, currentDepth - 1)
            } else {
                emitted
            }
        } else {
//            val unitDirection = ray.direction.normalized()
//            // TODO: Explain...
//            val t = 0.5 * (unitDirection.y + 1.0)
//            Color(1.0, 1.0, 1.0) * (1.0 - t) + Color(0.5, 0.7, 0.0) * t
            backgroundColor
        }
    }

}
