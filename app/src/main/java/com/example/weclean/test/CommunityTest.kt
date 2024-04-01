import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.ArrayList

class CommunityTest {

    private lateinit var community: Community
    private lateinit var fireBase: FireBase

    @Before
    fun setup() {
        fireBase = mock(FireBase::class.java)
        community = Community(fireBase)
    }

    @Test
    fun testCreateCommunity() {
        val cName = "Test Community"
        val email = "test@example.com"
        val location = "Test Location"
        val cCode = 123
        val userIds = ArrayList<String>()
        val adminIds = ArrayList<String>()

        val expected = hashMapOf(
            "name" to cName,
            "contactEmail" to email,
            "location" to location,
            "code" to cCode,
            "userIds" to userIds,
            "adminIds" to adminIds
        )

        val result = community.createCommunity(cName, email, location, cCode, userIds, adminIds)

        assertEquals(expected, result)
    }

    @Test
    fun testAddToDatabase() {
        assert(true)

        //FINISH
    }

    @Test
    fun testAddUserWithCode() {

        assert(true)

        //FINISH
    }
}