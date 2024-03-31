import com.example.weclean.backend.Community
import com.example.weclean.backend.User
import com.google.common.base.CharMatcher.any
import com.google.firebase.Firebase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CommunityTest {

    @Mock
    private lateinit var user: User
    private lateinit var firebase : Firebase
    private lateinit var mockFireBase: FireBaseInterface
    private lateinit var community: Community

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        community = Community()
    }

    @Test
    fun testCreateCommunity() {
        val cName = "Test Community"
        val email = "test@example.com"
        val location = "Test Location"
        val cCode = 123
        val userIds = arrayListOf("user1", "user2")
        val adminIds = arrayListOf("admin1", "admin2")

        `when`(mockFireBase.currentUserId()).thenReturn("admin1")
        `when`(runBlocking { mockFireBase.addDocumentWithName("communities", "test", any()) }).thenReturn(true)

        val result = community.createCommunity(cName, email, location, cCode, userIds, adminIds)

        assertEquals(true, result)
    }

    @Test
    fun testAddToDatabase() {
        //do test case using Mockito
    }

    @Test
    fun testAddUserWithCode() {
        //do test case using Mockito
    }
}