package com.hansholz.bestenotenapp.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import bestenotenapp.composeapp.generated.resources.Res
import bestenotenapp.composeapp.generated.resources.logo
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Rotation
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.models.Shape
import io.github.vinceglb.confettikit.core.models.Size
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.compose.resources.imageResource

class ConfettiPresets {
    companion object {
        @OptIn(ExperimentalMaterial3ExpressiveApi::class)
        @Composable
        fun randomFirework(rockets: Int): List<Party> {
            val shapes = with(MaterialShapes) {
                listOf(
                    Circle,
                    Square,
                    Slanted,
                    Arch,
                    Fan,
                    Arrow,
                    SemiCircle,
                    Oval,
                    Pill,
                    Triangle,
                    Diamond,
                    ClamShell,
                    Pentagon,
                    Gem,
                    VerySunny,
                    Sunny,
                    Cookie4Sided,
                    Cookie6Sided,
                    Cookie7Sided,
                    Cookie9Sided,
                    Cookie12Sided,
                    Ghostish,
                    Clover4Leaf,
                    Clover8Leaf,
                    Burst,
                    SoftBurst,
                    Boom,
                    SoftBoom,
                    Flower,
                    Puffy,
                    PuffyDiamond,
                    PixelCircle,
                    PixelTriangle,
                    Bun,
                    Heart
                ).map { Shape.CustomShape(it.toShape()) }
            }
            val parties = mutableListOf<Party>()
            val colors =
                listOf(
                    0xfce18a,
                    0xffb2c9,
                    0xffc6a3,
                    0xffecd7,
                    0xbae1ff,
                    0xafcbff,
                    0xaeffa1,
                    0xb2ffcd,
                    0xd1c4ff,
                    0xffafe1,
                    0xfff9b5,
                    0xf9b2ff,
                    0xe5ccff,
                    0xcce5ff,
                    0xfde1d1,
                    0x1f78b4,
                    0x54278f,
                    0xce2029,
                    0x873600,
                    0x2ecc71,
                    0x16a085,
                    0x264d28,
                    0x4b0082,
                    0x343a40,
                    0x4c2f27,
                )
            var delay = 0
            (0 until rockets).forEach { _ ->
                delay += (200..400).random()
                parties.add(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        size = listOf(Size(5), Size(10), Size(15), Size(20)),
                        spread = (200..500).random(),
                        colors = listOf(colors.random(), colors.random(), colors.random(), colors.random()),
                        shapes = shapes,
                        emitter = Emitter(duration = 100.milliseconds).max(100),
                        position = Position.Relative((0.1f..0.9f).random().toDouble(), (0.1f..0.9f).random().toDouble()),
                        delay = delay,
                    ),
                )
            }
            return parties
        }

        @Composable
        fun logos(): List<Party> {
            val logo = imageResource(Res.drawable.logo)
            return listOf(
                Party(
                    speed = 5f,
                    maxSpeed = 30f,
                    damping = 0.95f,
                    size = listOf(Size(40), Size(60), Size(80)),
                    angle = Angle.BOTTOM,
                    spread = Spread.SMALL,
                    shapes = listOf(Shape.Image(logo)),
                    timeToLive = 4000,
                    rotation = Rotation.disabled(),
                    emitter = Emitter(duration = 5.seconds).perSecond(20),
                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                ),
            )
        }
    }
}

fun ClosedRange<Float>.random() = Random.nextFloat() * (endInclusive - start) + start
