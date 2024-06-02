package ru.nsu.fit.sckwo.model.scene

import ru.nsu.fit.sckwo.model.*
import ru.nsu.fit.sckwo.model.materials.*
import kotlin.random.Random

class Scene(val objects: MutableList<Traceable> = mutableListOf()) {
    fun translateObjectById(objectId: String, offset: Point3D) {
        objects.find { it.id() == objectId }?.translate(offset)
    }

    fun getObjectIds(): List<String> = objects.filter { it.id() != null }.map { it.id()!! }

    fun add(obj: Traceable) {
        objects.add(obj)
    }

    // TODO: explain min in doc
    fun hit(ray: Ray, tMin: Double, tMax: Double): RayHitInfo? {
        return objects.asSequence()
            .mapNotNull { it.hit(ray, tMin, tMax) }
            .minByOrNull { it.t }
    }

    companion object {
        fun default(): Scene {
            val scene = Scene()
            scene.add(
                Triangle(
                    Point3D(100.0, -0.5, 100),
                    Point3D(-100.0, -0.5, -100.0),
                    Point3D(500.0, -0.5, -150),
                    LambertMaterial(Color(0.5, 0.3, 0.7))
                )
            )
//            scene.add(Sphere(Point3D(1000.0, -1000.0, 0.0), 1000.0, MetalMaterial(Color(0.2, 0.3, 0.5), 0.2)))
//            scene.add(Sphere(Point3D(-4.0, 1.0, 0.0), 2.0, LambertMaterial(Color(0.4, 0.2, 0.1))))
            // Add parallelepipeds
//            scene.add(
//                Parallelepiped(
//                    Point3D(14.0, 14.0, 0.0),
//                    1.0,
//                    1.0,
//                    2.0,
//                    MetalMaterial(Color(0.5, 0.3, 0.1), 0.3)
//                )
//            )
//            scene.add(Parallelepiped(Point3D(-14.0, 14.0, 4.0), 5.0, 10.0, 1.0, LambertMaterial(Color(0.3, 0.6, 0.3))))
//            scene.add(Parallelepiped(Point3D(-6.0, 14.0, 4.0), 2.0, 5.0, 1.0, LambertMaterial(Color(0.4, 0.2, 0.1))))
//            scene.add(Parallelepiped(Point3D(5.0, -14.0, 4.0), 2.0, 5.0, 1.0, DielectricMaterial(1.5)))
            addParallelepiped(scene, Point3D(-2.0, 0.0, 0.0), 1.0, 1.0, 1.0, DielectricMaterial(1.5))
            addParallelepiped(scene, Point3D(4.0, 0.0, 0.0), 1.0, 1.0, 1.0, MetalMaterial(Color(1.0, 0.0, 0.0), 0.3))
            scene.add(Sphere(Point3D(5.5, 0.0, 0.0), 0.5, MetalMaterial(Color(0.2, 0.4, 0.0), 0.3)))
//            addParallelepiped(scene, Point3D(-4.0, 0.0, 2.0), 1.0, 1.0, 1.0, LambertMaterial(Color(0.4, 0.2, 0.1)))
//            addParallelepiped(
//                scene,
//                Point3D(2.0, 1.0, 2.0),
//                1.0,
//                1.0,
//                1.0,
//                EmittingMaterial(Color(0.1, 0.7, 0.4), 5.0)
//            )
//            addParallelepiped(
//                scene,
//                Point3D(-2.0, 1.0, 2.0),
//                1.0,
//                1.0,
//                1.0,
//                MetalMaterial(Color(0.5, 0.3, 0.1), 0.3)
//            )
            // Add rectangles
//            scene.add(Rectangle(Point3D(10.0, 0.0, 10.0), 200.0, 200.0, EmittingMaterial(Color(0.4, 0.1, 0.7), 5.0)))
//            scene.add(Rectangle(Point3D(-4.0, 0.0, 1.0), 100.0, 100.0, DielectricMaterial(1.5)))
//            scene.add(Rectangle(Point3D(0.0, -4.0, 10.0), 100.0, 100.0, DielectricMaterial(1.5)))
            val random = Random.Default
            for (a in -2 until 2) {
                for (b in -2 until 2) {
                    val chooseMaterial = random.nextDouble()
                    val center = Point3D(a + 0.9 * random.nextDouble(), 0.2, b + 0.9 * random.nextDouble())
                    if ((center - Point3D(4.0, 0.2, 0.0)).length() > 0.9) {
                        val material: Material = when {
                            chooseMaterial < 0.8 -> LambertMaterial(Point3D.random() * Point3D.random())
                            chooseMaterial < 0.95 -> MetalMaterial(
                                Point3D.random(0.5, 1.0),
                                random.nextDouble(0.0, 0.5)
                            )

                            else -> LambertMaterial(Point3D.random() * Point3D.random())
                        }
                        scene.add(Sphere(center, 0.2, material))
                    }
                }
            }
            scene.add(Sphere(Point3D(0.0, 1.0, 2.0), 0.0001, EmittingMaterial(Color(0.1, 0.7, 0.4), 5.0)))
//            scene.add(Sphere(Point3D(8.5, 1.0, 0.0), 2.0, LambertMaterial(Color(0.4, 0.2, 0.1))))
//            scene.add(
//                Sphere(
//                    Point3D(6.0, 0.0, 2.0),
//                    1.0,
//                    EmittingMaterial(Color(0.1, 0.7, 0.4), 5.0)
//                )
//            )
//            scene.add(
//                Sphere(
//                    Point3D(3.0, 0.0, 2.0),
//                    1.0,
//                    DielectricMaterial(5.0)
//                )
//            )
            return scene
        }

        private fun addParallelepiped(
            scene: Scene,
            center: Point3D,
            width: Double,
            height: Double,
            depth: Double,
            material: Material,
        ) {
            val halfWidth = width / 2.0
            val halfHeight = height / 2.0
            val halfDepth = depth / 2.0

            val vertices = listOf(
                center + Point3D(-halfWidth, -halfHeight, -halfDepth),
                center + Point3D(halfWidth, -halfHeight, -halfDepth),
                center + Point3D(halfWidth, halfHeight, -halfDepth),
                center + Point3D(-halfWidth, halfHeight, -halfDepth),
                center + Point3D(-halfWidth, -halfHeight, halfDepth),
                center + Point3D(halfWidth, -halfHeight, halfDepth),
                center + Point3D(halfWidth, halfHeight, halfDepth),
                center + Point3D(-halfWidth, halfHeight, halfDepth)
            )

            // Front face
            scene.add(Triangle(vertices[0], vertices[1], vertices[2], material))
            scene.add(Triangle(vertices[0], vertices[2], vertices[3], material))

            // Back face
            scene.add(Triangle(vertices[4], vertices[5], vertices[6], material))
            scene.add(Triangle(vertices[4], vertices[6], vertices[7], material))

            // Left face
            scene.add(Triangle(vertices[0], vertices[3], vertices[7], material))
            scene.add(Triangle(vertices[0], vertices[7], vertices[4], material))

            // Right face
            scene.add(Triangle(vertices[1], vertices[5], vertices[6], material))
            scene.add(Triangle(vertices[1], vertices[6], vertices[2], material))

            // Top face
            scene.add(Triangle(vertices[3], vertices[2], vertices[6], material))
            scene.add(Triangle(vertices[3], vertices[6], vertices[7], material))

            // Bottom face
            scene.add(Triangle(vertices[0], vertices[1], vertices[5], material))
            scene.add(Triangle(vertices[0], vertices[5], vertices[4], material))
        }
    }
}
