package ru.nsu.fit.sckwo.model.scene

import com.google.gson.*
import ru.nsu.fit.sckwo.model.*
import ru.nsu.fit.sckwo.model.materials.*
import java.io.File
import java.lang.reflect.Type

// Serializers and Deserializers for Materials
class MaterialSerializer : JsonSerializer<Material> {
    override fun serialize(src: Material, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        when (src) {
            is DielectricMaterial -> {
                jsonObject.addProperty("type", "DielectricMaterial")
                jsonObject.addProperty("indexOfReflection", src.indexOfReflection)
            }

            is EmittingMaterial -> {
                jsonObject.addProperty("type", "EmittingMaterial")
                jsonObject.add("emitColor", context.serialize(src.emitColor))
                jsonObject.addProperty("intensity", src.intensity)
            }

            is LambertMaterial -> {
                jsonObject.addProperty("type", "LambertMaterial")
                jsonObject.add("albedo", context.serialize(src.albedo))
            }

            is MetalMaterial -> {
                jsonObject.addProperty("type", "MetalMaterial")
                jsonObject.add("albedo", context.serialize(src.albedo))
                jsonObject.addProperty("fuzzCoefficient", src.fuzzCoefficient)
            }
        }
        return jsonObject
    }
}

class MaterialDeserializer : JsonDeserializer<Material> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Material {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        return when (type) {
            "DielectricMaterial" -> DielectricMaterial(jsonObject.get("indexOfReflection").asDouble)
            "EmittingMaterial" -> EmittingMaterial(
                context.deserialize(jsonObject.get("emitColor"), Color::class.java),
                jsonObject.get("intensity").asDouble
            )

            "LambertMaterial" -> LambertMaterial(context.deserialize(jsonObject.get("albedo"), Color::class.java))
            "MetalMaterial" -> MetalMaterial(
                context.deserialize(jsonObject.get("albedo"), Color::class.java),
                jsonObject.get("fuzzCoefficient").asDouble
            )

            else -> throw IllegalArgumentException("Unknown material type")
        }
    }
}


// Serializers and Deserializers for Traceable Objects
class TraceableSerializer : JsonSerializer<Traceable> {
    override fun serialize(src: Traceable, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        when (src) {
            is Sphere -> {
                jsonObject.addProperty("type", "Sphere")
                jsonObject.add("center", context.serialize(src.center))
                jsonObject.addProperty("radius", src.radius)
                jsonObject.add("material", context.serialize(src.material))
            }

            is Triangle -> {
                jsonObject.addProperty("type", "Triangle")
                jsonObject.add("v0", context.serialize(src.v0))
                jsonObject.add("v1", context.serialize(src.v1))
                jsonObject.add("v2", context.serialize(src.v2))
                jsonObject.add("material", context.serialize(src.material))
            }
        }
        return jsonObject
    }
}

class TraceableDeserializer : JsonDeserializer<Traceable> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Traceable {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        return when (type) {
            "Sphere" -> Sphere(
                context.deserialize(jsonObject.get("center"), Point3D::class.java),
                jsonObject.get("radius").asDouble,
                context.deserialize(jsonObject.get("material"), Material::class.java)
            )

            "Triangle" -> Triangle(
                context.deserialize(jsonObject.get("v0"), Point3D::class.java),
                context.deserialize(jsonObject.get("v1"), Point3D::class.java),
                context.deserialize(jsonObject.get("v2"), Point3D::class.java),
                context.deserialize(jsonObject.get("material"), Material::class.java)
            )

            else -> throw IllegalArgumentException("Unknown traceable type")
        }
    }
}


// Gson initialization with custom serializers/deserializers
val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Traceable::class.java, TraceableSerializer())
    .registerTypeAdapter(Traceable::class.java, TraceableDeserializer())
    .registerTypeAdapter(Material::class.java, MaterialSerializer())
    .registerTypeAdapter(Material::class.java, MaterialDeserializer())
    .registerTypeAdapter(MetalMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(DielectricMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(EmittingMaterial::class.java, MaterialSerializer())
    .registerTypeAdapter(LambertMaterial::class.java, MaterialSerializer())
    .create()

// Save and Load functions
fun saveScene(scene: Scene, filename: String) {
    val json = gson.toJson(scene)
    File(filename).writeText(json)
}

fun loadScene(filename: String): Scene? {
    val json = File(filename).readText()
    return gson.fromJson(json, Scene::class.java)
}
//
//
//class SceneSerializer : JsonSerializer<Scene> {
//    override fun serialize(src: Scene?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        val jsonObject = JsonObject()
//        val jsonArray = JsonArray()
//        src?.objects?.forEach { obj ->
//            jsonArray.add(context?.serialize(obj))
//        }
//        jsonObject.add("objects", jsonArray)
//        return jsonObject
//    }
//}
//
//class SceneDeserializer : JsonDeserializer<Scene> {
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Scene {
//        val jsonObject = json?.asJsonObject
//        val objects = mutableListOf<Traceable>()
//        jsonObject?.getAsJsonArray("objects")?.forEach { objJson ->
//            val obj = context?.deserialize<Traceable>(objJson, Traceable::class.java)
//            if (obj != null) objects.add(obj)
//        }
//        return Scene(objects)
//    }
//}
//
//fun saveScene(scene: Scene, filename: String) {
//    val gson = GsonBuilder()
//        .registerTypeAdapter(Scene::class.java, SceneSerializer())
//        .setPrettyPrinting()
//        .create()
//    try {
//        FileWriter(filename).use { writer ->
//            gson.toJson(scene, writer)
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//}
//
//fun loadScene(filename: String): Scene? {
//    val gson = GsonBuilder()
//        .registerTypeAdapter(Scene::class.java, SceneDeserializer())
//        .create()
//    return try {
//        FileReader(filename).use { reader ->
//            gson.fromJson(reader, Scene::class.java)
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//        null
//    }
//}
