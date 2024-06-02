package ru.nsu.fit.sckwo.model

import kotlinx.coroutines.*
import ru.nsu.fit.sckwo.model.scene.Scene
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import kotlin.random.Random

class RenderController(
    var repaintIntervalMs: Long,
    var batchSize: Int,
) : AutoCloseable {
    var gamma: Double = 2.2
    private var renderJob: Job? = null
    private val renderDispatcher =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
    private var lastRepaintTime = 0L

    private var useWireframe: Boolean = true

    private var pixelsRendered: Int = 0

    fun toggleWireframeMode() {
        useWireframe = !useWireframe
    }

    fun renderImage(
        image: BufferedImage,
        rayTracer: RayTracer,
        camera: Camera,
        stepsOfWireframe: Int = 25,
        onUpdateFunction: (() -> Unit),
        onProgressUpdate: (Double) -> Unit,
    ) {
        renderJob?.cancel()
        pixelsRendered = 0
        val totalPixels = camera.imageWidth * camera.imageHeight

        if (useWireframe) {
            renderWireframe(image, rayTracer.scene, camera, stepsOfWireframe, onUpdateFunction)
        } else {
            renderRayTracedImage(image, rayTracer, camera, onUpdateFunction, onProgressUpdate, totalPixels)
        }
    }

    private fun renderRayTracedImage(
        image: BufferedImage,
        rayTracer: RayTracer,
        camera: Camera,
        onUpdateFunction: () -> Unit,
        onProgressUpdate: (Double) -> Unit,
        totalPixels: Int,
    ) {
        val pixels = (0 until camera.imageHeight).flatMap { j ->
            (0 until camera.imageWidth).map { i ->
                Pair(j, i)
            }
        }
//            .shuffled()

        renderJob = CoroutineScope(renderDispatcher).launch {
            pixels.chunked(batchSize).forEach { batch ->
                batch.map { (j, i) ->
                    async {
                        val color = (0 until camera.samplesPerPixel).fold(Point3D.ZERO) { acc, _ ->
                            val u = (i.toDouble() + Random.nextDouble()) / (camera.imageWidth - 1)
                            val v = (j.toDouble() + Random.nextDouble()) / (camera.imageHeight - 1)
                            val ray = camera.ray(u, v)
                            acc + rayTracer.color(ray)
                        }
                        if (isActive) {
                            image.setRGB(
                                i,
                                camera.imageHeight - 1 - j,
                                color.toAWTColor(camera.samplesPerPixel, gamma)
                            )
                        }
                    }
                }.awaitAll()

                pixelsRendered += batch.size
                val progress = pixelsRendered.toDouble() / totalPixels
                onProgressUpdate(progress)

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastRepaintTime > repaintIntervalMs) {
                    SwingUtilities.invokeLater {
                        onUpdateFunction.invoke()
                    }
                    lastRepaintTime = currentTime
                }
            }
            onUpdateFunction.invoke()
        }
    }

    private fun renderWireframe(
        image: BufferedImage,
        scene: Scene?,
        camera: Camera,
        stepsOfWireframe: Int,
        onUpdate: (() -> Unit),
    ) {
        if (scene == null) {
            return
        }
        // Clear image
        val graphics = image.createGraphics()
        graphics.color = Color.BLACK
        graphics.fillRect(0, 0, image.width, image.height)
        graphics.dispose()

        // Render wireframe
        val lineSegments = scene.objects.flatMap { it.linearize(stepsOfWireframe) }
        val graphics2D = image.createGraphics()
        graphics2D.color = Color.WHITE

        lineSegments.forEach { segment ->
            val start = camera.project(segment.start)
            val end = camera.project(segment.end)
            if (start != null && end != null) {
                graphics2D.drawLine(start.x, start.y, end.x, end.y)
            }
        }

        graphics2D.dispose()
        onUpdate.invoke()
    }

    companion object {
        fun default(): RenderController = RenderController(50L, 50)
    }

    override fun close() {
        renderDispatcher.close()
    }

}
