import com.example.weclean.backend.User
import com.example.weclean.backend.FireBase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class UserTest {

    private lateinit var user : User
    private lateinit var fireBase: FireBase

    @Before
    fun setup() {
        fireBase = mock(FireBase::class.java)
        user = User()  // Initialize User instance
    }

    @Test
    fun testCreateUser() {

        val username = "name"

        val expected = hashMapOf(
            "username" to username,
            "communityAdminIds" to arrayListOf<String>(),
            "communityIds" to arrayListOf<String>(),
            "eventIds" to arrayListOf<String>(),
            "litteringEntries" to arrayListOf<String>()
        )

        val result = user.createUser(username)

        assertEquals(expected, result)
    }

}