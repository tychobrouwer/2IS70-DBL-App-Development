import com.example.weclean.backend.Community
import com.example.weclean.backend.User
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
import java.util.Date


class UserTest {

    private lateinit var user : User
    private lateinit var fireBase: FireBase

    @Before
    fun setup() {
        fireBase = mock(FireBase::class.java)
        user = User(fireBase)  // Initialize User instance
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