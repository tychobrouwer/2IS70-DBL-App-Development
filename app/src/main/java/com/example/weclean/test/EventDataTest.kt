import com.example.weclean.backend.EventData
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class EventDataTest {

    private lateinit var eventData: EventData

    @Before
    fun setup() {
        eventData = EventData()
    }

    @Test
    fun testCreateEventData() {
        val creator = "creator"
        eventData.name = "Test Event"
        eventData.community = "Community A"
        eventData.description = "This is a test event"
        eventData.location = "Test Location"
        eventData.numPeople = 10
        eventData.id = "event_id"

        val expected = hashMapOf(
            "description" to "This is a test event".replace("\n", "_newline"),
            "imageId" to eventData.imageId,
            "date" to Date(eventData.timeStamp),
            "community" to "Community A",
            "name" to "Test Event",
            "location" to "Test Location",
            "userIds" to listOf(creator)
        )

        val result = eventData.createEventData(creator)

        assertEquals(expected, result)
    }

}