package ru.nsu.fit.sckwo.model.materials

import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.Point3D
import ru.nsu.fit.sckwo.model.Ray
import ru.nsu.fit.sckwo.model.RayHitInfo

// TODO: explanation???
data class ScatterResult(val scatteredRay: Ray, val attenuation: Color)

/**
 * Materials determine how objects interact with light rays.
 */
interface Material {
    fun scatter(ray: Ray, rayHitInfo: RayHitInfo): ScatterResult?
    fun emitted(): Color {
        return Point3D.ZERO
    }
}
