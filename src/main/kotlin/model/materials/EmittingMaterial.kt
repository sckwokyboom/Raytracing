package ru.nsu.fit.sckwo.model.materials

import ru.nsu.fit.sckwo.model.Color
import ru.nsu.fit.sckwo.model.Ray
import ru.nsu.fit.sckwo.model.RayHitInfo

class EmittingMaterial(
    val emitColor: Color,
    val intensity: Double,
) : Material {

    override fun scatter(ray: Ray, rayHitInfo: RayHitInfo): ScatterResult? {
        return null
    }

    override fun emitted(): Color {
        return emitColor * intensity
    }
}
