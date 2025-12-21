import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// Decoupled Configuration
data class LifeConfig(
    val initialVisaDays: Int = 60,
    val initialPtoDays: Int = 17,
    val initialSolvedCount: Int = 545,
    val simulationSpeedMs: Long = 50
)

// Dynamics: Maps company names to their required "Muscle Strength"
val targetThresholds = mapOf(
    "Google" to 600,
    "Meta" to 580,
    "Stripe" to 590,
    "Startup" to 450
)

data class LifeState(
    val visaDays: Int,
    val ptoDays: Int,
    val prepLevel: Int,
    val activeOffers: List<String> = emptyList(),
    val isFlightBooked: Boolean = false
)

class HustleEngine(private val config: LifeConfig) {
    private val _state = MutableStateFlow(
        LifeState(config.initialVisaDays, config.initialPtoDays, config.initialSolvedCount)
    )
    val state = _state.asStateFlow()

    suspend fun runStrategicHustle() = coroutineScope {
        // PTO Grind: Use days to increment Prep Level
        val grindJob = launch {
            while (state.value.ptoDays > 0 && state.value.activeOffers.isEmpty()) {
                delay(config.simulationSpeedMs)
                _state.update { it.copy(ptoDays = it.ptoDays - 1, prepLevel = it.prepLevel + 1) }
            }
        }

        // Window Bleed: Visa days only decrease when PTO is 0
        val visaJob = launch {
            while (state.value.visaDays > 0 && state.value.activeOffers.isEmpty()) {
                if (state.value.ptoDays == 0) {
                    delay(config.simulationSpeedMs)
                    _state.update { it.copy(visaDays = it.visaDays - 1) }
                } else yield()
            }
            if (state.value.activeOffers.isEmpty()) _state.update { it.copy(isFlightBooked = true) }
        }

        // Concurrent Interview Tracks
        targetThresholds.map { (company, threshold) ->
            async {
                delay(config.simulationSpeedMs * 5) // Simulate interview duration
                if (state.value.prepLevel >= threshold) {
                    _state.update { it.copy(activeOffers = it.activeOffers + company) }
                }
            }
        }.awaitAll()

        grindJob.cancel()
        visaJob.cancel()
    }
}

fun main() = runBlocking {
    val engine = HustleEngine(LifeConfig())

    launch {
        engine.state.collect { s ->
            val status = when {
                s.activeOffers.isNotEmpty() -> "SUCCESS: Offers from ${s.activeOffers.joinToString()}"
                s.isFlightBooked -> "TERMINAL: Window exhausted. Booking flight."
                else -> "HUSTLING: Visa ${s.visaDays} | PTO ${s.ptoDays} | Prep ${s.prepLevel}"
            }
            println(status)
        }
    }

    engine.runStrategicHustle()
}
/**
 * Sample Hustling Output
 *
 * HUSTLING: Visa 60 | PTO 17 | Prep 545
   HUSTLING: Visa 60 | PTO 16 | Prep 546
   HUSTLING: Visa 60 | PTO 15 | Prep 547
   HUSTLING: Visa 60 | PTO 14 | Prep 548
   HUSTLING: Visa 60 | PTO 13 | Prep 549
   HUSTLING: Visa 60 | PTO 12 | Prep 550
   SUCCESS: Offers from Startup
 */