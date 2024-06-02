package ru.nsu.fit.sckwo.model

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import ru.nsu.fit.sckwo.model.materials.*
import ru.nsu.fit.sckwo.model.scene.MaterialDeserializer
import ru.nsu.fit.sckwo.model.scene.MaterialSerializer
import ru.nsu.fit.sckwo.model.scene.TraceableDeserializer
import ru.nsu.fit.sckwo.model.scene.TraceableSerializer
import java.io.File
import java.lang.reflect.Type

// Custom serializer and deserializer for Camera
class CameraSerializer : JsonSerializer<Camera> {
    override fun serialize(src: Camera, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.add("position", context.serialize(src.position))
        jsonObject.add("lookAt", context.serialize(src.lookAt))
        jsonObject.add("upVector", context.serialize(src.upVector))
        jsonObject.addProperty("fieldOfView", src.fieldOfView)
        jsonObject.addProperty("apertureDiameter", src.apertureDiameter)
        jsonObject.addProperty("focusDistance", src.focusDistance)
        jsonObject.addProperty("imageWidth", src.imageWidth)
        jsonObject.addProperty("imageHeight", src.imageHeight)
        jsonObject.addProperty("samplesPerPixel", src.samplesPerPixel)
        return jsonObject
    }
}

class CameraDeserializer : JsonDeserializer<Camera> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Camera {
        val jsonObject = json.asJsonObject
        return Camera(
            context.deserialize(jsonObject.get("position"), Point3D::class.java),
            context.deserialize(jsonObject.get("lookAt"), Point3D::class.java),
            context.deserialize(jsonObject.get("upVector"), Point3D::class.java),
            jsonObject.get("fieldOfView").asDouble,
            jsonObject.get("apertureDiameter").asDouble,
            jsonObject.get("focusDistance").asDouble,
            jsonObject.get("imageWidth").asInt,
            jsonObject.get("imageHeight").asInt,
            jsonObject.get("samplesPerPixel").asInt
        )
    }
}

// Custom serializer and deserializer for RenderController
class RenderControllerSerializer : JsonSerializer<RenderController> {
    override fun serialize(src: RenderController, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("repaintIntervalMs", src.repaintIntervalMs)
        jsonObject.addProperty("batchSize", src.batchSize)
        return jsonObject
    }
}

class RenderControllerDeserializer : JsonDeserializer<RenderController> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RenderController {
        val jsonObject = json.asJsonObject
        return RenderController(
            jsonObject.get("repaintIntervalMs").asLong,
            jsonObject.get("batchSize").asInt
        )
    }
}

// Custom serializer and deserializer for RayTracer
class RayTracerSerializer : JsonSerializer<RayTracer> {
    override fun serialize(src: RayTracer, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.add("scene", context.serialize(src.scene))
        jsonObject.addProperty("maxDepthOfProcessing", src.maxDepthOfProcessing)
        jsonObject.add("backgroundColor", context.serialize(src.backgroundColor))
        return jsonObject
    }
}

class RayTracerDeserializer : JsonDeserializer<RayTracer> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RayTracer {
        val jsonObject = json.asJsonObject
        return RayTracer(
            null,
//            context.deserialize(jsonObject.get("scene"), Scene::class.java),
            jsonObject.get("maxDepthOfProcessing").asInt,
            context.deserialize(jsonObject.get("backgroundColor"), Color::class.java)
        )
    }
}

class Point3DSerializer : JsonSerializer<Point3D> {
    override fun serialize(src: Point3D, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("x", src.x)
        jsonObject.addProperty("y", src.y)
        jsonObject.addProperty("z", src.z)
        return jsonObject
    }
}

class Point3DDeserializer : JsonDeserializer<Point3D> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Point3D {
        val jsonObject = json.asJsonObject
        return Point3D(
            jsonObject.get("x").asDouble,
            jsonObject.get("y").asDouble,
            jsonObject.get("z").asDouble
        )
    }
}


val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Camera::class.java, CameraSerializer())
    .registerTypeAdapter(Camera::class.java, CameraDeserializer())
    .registerTypeAdapter(RenderController::class.java, RenderControllerSerializer())
    .registerTypeAdapter(RenderController::class.java, RenderControllerDeserializer())
    .registerTypeAdapter(RayTracer::class.java, RayTracerSerializer())
    .registerTypeAdapter(RayTracer::class.java, RayTracerDeserializer())
    .registerTypeAdapter(Point3D::class.java, Point3DSerializer())
    .registerTypeAdapter(Point3D::class.java, Point3DDeserializer())
    .registerTypeAdapter(Traceable::class.java, TraceableSerializer())
    .registerTypeAdapter(Sphere::class.java, TraceableSerializer())
    .registerTypeAdapter(Sphere::class.java, TraceableDeserializer())
    .registerTypeAdapter(Triangle::class.java, TraceableSerializer())
    .registerTypeAdapter(Triangle::class.java, TraceableDeserializer())
    .registerTypeAdapter(Traceable::class.java, TraceableDeserializer())
    .registerTypeAdapter(Material::class.java, MaterialSerializer())
    .registerTypeAdapter(Material::class.java, MaterialDeserializer())
    .registerTypeAdapter(MetalMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(DielectricMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(EmittingMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(LambertMaterial::class.java, MaterialSerializer())
    .create()

fun saveSettings(camera: Camera, renderController: RenderController, rayTracer: RayTracer, filename: String) {
    val settings = mapOf(
        "camera" to camera,
        "renderController" to renderController,
        "rayTracer" to rayTracer
    )
    val json = gson.toJson(settings)
    File(filename).writeText(json)
}

fun loadSettings(filename: String): Map<String, Any> {
    val json = File(filename).readText()
    val type = object : TypeToken<Map<String, JsonElement>>() {}.type
    val jsonElementMap: Map<String, JsonElement> = gson.fromJson(json, type)
    return jsonElementMap.mapValues { entry ->
        when (entry.key) {
            "camera" -> gson.fromJson(entry.value, Camera::class.java)
            "renderController" -> gson.fromJson(entry.value, RenderController::class.java)
            "rayTracer" -> gson.fromJson(entry.value, RayTracer::class.java)
            else -> throw IllegalArgumentException("Unknown key: ${entry.key}")
        }
    }
}
