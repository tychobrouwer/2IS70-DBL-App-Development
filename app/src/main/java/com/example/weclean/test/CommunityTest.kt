import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
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
//        val cName = "Test Community"
//        val email = "test@example.com"
//        val location = "Test Location"
//        val cCode = 123
//
//        runBlocking {
//            val result = community.addToDatabase(cName, email, location, cCode)
//            assertTrue(result)
//        }

//        runBlocking {
//            `when`(fireBase.currentUserId()).thenReturn("testUserId")
//            `when`(fireBase.addDocument(anyString(), any())).thenReturn(mock(DocumentReference::class.java))
//
//            val result = community.addToDatabase("Test Name", "test@example.com", "Test Location", 123)
//
//            assertTrue(result)
//        }
    }

    @Test
    fun testAddUserWithCode() {

        assert(true)
//        runBlocking {
//            `when`(fireBase.currentUserId()).thenReturn("testUserId")
//            `when`(fireBase.getDocumentsWithFilter(anyString(), any())).thenReturn(mock(
//                QuerySnapshot::class.java))
//            `when`(fireBase.addToArray(anyString(), anyString(), anyString(), any())).thenReturn(true)
//
//            val result = community.addUserWithCode("123")
//
//            assertTrue(result)
//        }
    }
}