package ru.nsu.fit.sckwo.model.materials

import ru.nsu.fit.sckwo.model.Point3D
import ru.nsu.fit.sckwo.model.Ray
import ru.nsu.fit.sckwo.model.RayHitInfo
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class DielectricMaterial(val indexOfReflection: Double) : Material {
    override fun scatter(ray: Ray, rayHitInfo: RayHitInfo): ScatterResult {
        val attenuation = Point3D.ONE
        val refractionRatio = if (rayHitInfo.isFrontFace) 1.0 / indexOfReflection else indexOfReflection

        val unitDirection = ray.direction.normalized()
        val cosTheta = min(-unitDirection.dot(rayHitInfo.normal), 1.0)
        val sinTheta = sqrt(1.0 - cosTheta * cosTheta)

        val cannotRefract = refractionRatio * sinTheta > 1.0
        val shouldReflect = cannotRefract || reflectance(cosTheta, refractionRatio) > Random.nextDouble()
        val direction = if (shouldReflect) {
            unitDirection.reflect(rayHitInfo.normal)
        } else {
            unitDirection.refract(rayHitInfo.normal, refractionRatio)
        }

        return ScatterResult(Ray(rayHitInfo.intersectionPoint, direction), attenuation)
    }

    private fun reflectance(cosine: Double, refIdx: Double): Double {
        val r0 = ((1 - refIdx) / (1 + refIdx)).let { it * it }
        return r0 + (1 - r0) * (1 - cosine).pow(5)
    }
}
