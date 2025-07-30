package components

import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ConfettiPresets {
    companion object {
        fun randomFirework(rockets: Int): List<Party> {
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
            for (i in 0 until rockets) {
                delay += (200..400).random()
                parties.add(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = (200..500).random(),
                        colors = listOf(colors.random(), colors.random(), colors.random(), colors.random()),
                        emitter = Emitter(duration = 100.milliseconds).max(100),
                        position = Position.Relative((0.1f..0.9f).random().toDouble(), (0.1f..0.9f).random().toDouble()),
                        delay = delay,
                    ),
                )
            }
            return parties
        }

        fun rain(duration: Duration = 5.seconds): List<Party> {
            return listOf(
                Party(
                    speed = 0f,
                    maxSpeed = 15f,
                    damping = 0.9f,
                    angle = Angle.BOTTOM,
                    spread = Spread.ROUND,
                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                    emitter = Emitter(duration = duration).perSecond(100),
                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                ),
            )
        }
    }
}

fun ClosedRange<Float>.random() = Random.nextFloat() * (endInclusive - start) + start
