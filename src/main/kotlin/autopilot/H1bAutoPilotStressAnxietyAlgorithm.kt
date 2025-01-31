import java.time.Duration
import java.time.Instant
import kotlin.random.Random

enum class EventType { PROMOTED_TO_CUSTOMER, SURVIVED_THE_DAY }

const val DAYS_TO_SURVIVE = 60L
const val MANDATORY_DAILY_SLEEPING_HOURS = 8L

var windowStart = Instant.now()
var windowEnd = windowStart.plus(Duration.ofDays(DAYS_TO_SURVIVE)) // 60 days window

fun autoPilot() {
    while (true) {
        // Generate a random event using when
        val randomEvent = generateRandomEvent()

        // Call the dailyTriggerOfAnxiety function with the random event
        dailyTriggerOfAnxiety(randomEvent)

        // Sleep for 8 hours
        val sleepDuration = Duration.ofHours(MANDATORY_DAILY_SLEEPING_HOURS).toMillis()
        Thread.sleep(sleepDuration) // Sleep for 8 hours before checking again
    }
}

fun dailyTriggerOfAnxiety(uncertainEvent: EventType) {
    val now = Instant.now()
    println("Uncertain event happened : $uncertainEvent")

    when (uncertainEvent) {
        EventType.PROMOTED_TO_CUSTOMER -> {
            windowStart = now
            windowEnd = windowStart.plus(Duration.ofDays(60)) // Reset window to 60 days
        }
        EventType.SURVIVED_THE_DAY -> {
            windowStart = windowStart.plus(Duration.ofDays(1)) // Slide window by one day
            windowEnd = windowEnd.plus(Duration.ofDays(1))
        }
    }
}

fun generateRandomEvent(): EventType {
    return when (Random.nextBoolean()) {
        true -> EventType.PROMOTED_TO_CUSTOMER
        false -> EventType.SURVIVED_THE_DAY
    }
}

// Start the autopilot
fun main() {
    autoPilot()
}
