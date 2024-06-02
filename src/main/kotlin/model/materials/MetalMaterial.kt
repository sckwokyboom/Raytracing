package ru.nsu.fit.sckwo.model.materials

import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.Point3D
import ru.nsu.fit.sckwo.model.Ray
import ru.nsu.fit.sckwo.model.RayHitInfo

class MetalMaterial(val albedo: Color, val fuzzCoefficient: Double) : Material {
    private val fuzz: Double = if (fuzzCoefficient < 1) fuzzCoefficient else 1.0
    override fun scatter(ray: Ray, rayHitInfo: RayHitInfo): ScatterResult? {
        val reflected = ray.direction.normalized().reflect(rayHitInfo.normal.normalized())
        val scatterDirection =
            reflected
//        + Point3D.randomInUnitSphere() * fuzz
        val scatteredRay = Ray(rayHitInfo.intersectionPoint, scatterDirection)
        val attenuation = albedo
        return if (scatterDirection.dot(rayHitInfo.normal) > 0) {
            ScatterResult(scatteredRay, attenuation)
        } else {
            null
        }
    }

}