//Test cases file to fix: import com.example.weclean.backend.Community
import com.example.weclean.backend.FireBase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.google.firebase.firestore.Filter
import com.example.weclean.backend.Community

//TODO: Fix the issue reading background thread execution of firebase not allowed
class CommunityTest {

    private var community = Community()
    private var fireBase = FireBase()

    @Before
    fun setUp() {
        //do
    }

    @Test
    fun testCreateCommunity() {
        val userIds = arrayListOf("user1", "user2")
        val adminIds = arrayListOf("admin1", "admin2")
        val result = community.createCommunity("Community Name", "email@example.com", "Location", 123, userIds, adminIds)

        assert(result["name"] == "Community Name")
        assert(result["contactEmail"] == "email@example.com")
        assert(result["location"] == "Location")
        assert(result["code"] == 123)
        assert(result["userIds"] == userIds)
        assert(result["adminIds"] == adminIds)
        assert(false)
    }

    @Test
    fun testAddToDatabase() = runBlocking {
        val result = community.addToDatabase("Community Name", "email@example.com", "Location", 123)
        assertTrue(result)
    }

    @Test
    fun testAddUserWithCode() = runBlocking {
        // Create a random community using the Community class method
        val communityName = "Test Community"
        val communityCode = "000"
        val communityLocation = "Test Location"
        val communityId = "test_community_${System.currentTimeMillis()}"
        val userIds = arrayListOf("user1", "user2")
        val adminIds = arrayListOf("admin1", "admin2")
        val communityData = community.createCommunity(communityName, "admin@example.com", communityLocation, 123, userIds, adminIds)

        // Add the community to the database
        val communityAdded = fireBase.addDocumentWithName("Community", communityId, communityData)
        assertTrue(communityAdded)

        // Create a random user using the User class method
        val userName = "Test User"
        val userEmail = "user@example.com"
        val userDob = "1990-01-01"
        val userCountry = "Test Country"
        val userId = "test_user_${System.currentTimeMillis()}"
        val userData = mapOf(
            "username" to userName,
            "email" to userEmail,
            "dob" to userDob,
            "country" to userCountry
        )

        // Define filter to retrieve communities with the specified code
        val filter = Filter.equalTo("code", communityCode)

        // Add the user to the database
        val userAdded = fireBase.addDocumentWithName("Users", userId, userData)
        assertTrue(userAdded)

        // Add the user to the community using the method being tested
        val userAddedToCommunity = community.addUserWithCode(communityCode)
        assertTrue(userAddedToCommunity)

        // Retrieve all communities from Firebase
        val allCommunities = fireBase.getDocumentsWithFilter("Community", filter)

        // Check if the user is present in any community
        var userFound = false
        if (allCommunities != null) {
            for (communityData in allCommunities.documents) {
                val userIds = communityData.data?.get("userIds") as? ArrayList<*>
                if (userIds != null && userId in userIds) {
                    userFound = true
                    break
                }
            }
        }

        // Assert based on whether the user was found
        assertTrue(userFound)
    }
}