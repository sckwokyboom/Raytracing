package ru.nsu.fit.sckwo.model.materials

import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.Point3D
import ru.nsu.fit.sckwo.model.Ray
import ru.nsu.fit.sckwo.model.RayHitInfo

class LambertMaterial(val albedo: Color) : Material {
    override fun scatter(ray: Ray, rayHitInfo: RayHitInfo): ScatterResult {
        val scatterDirection =
            (rayHitInfo.normal + Point3D.randomInUnitSphere()).takeIf { !it.isNearZero() } ?: rayHitInfo.normal
//

        val scatteredRay = Ray(rayHitInfo.intersectionPoint, scatterDirection)
        val attenuation = albedo
        return ScatterResult(scatteredRay, attenuation)
    }
}