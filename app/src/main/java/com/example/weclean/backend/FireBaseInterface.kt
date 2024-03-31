import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask

interface FireBaseInterface {
    fun currentUserId(): String?
    fun currentUserEmail(): String?
    suspend fun addDocumentWithName(collection: String, document: String, data: Any): Boolean
    suspend fun addDocument(collection: String, data: Any): DocumentReference?
    suspend fun getDocument(collection: String, document: String): DocumentSnapshot?
    suspend fun getDocumentsWithFilter(collection: String, filter: Filter): QuerySnapshot?
    suspend fun updateValue(collection: String, document: String, field: String, value: Any): Boolean
    suspend fun addToArray(collection: String, document: String, field: String, value: Any): Boolean
    suspend fun removeFromArray(collection: String, document: String, field: String, value: Any): Boolean
    suspend fun deleteDocument(collection: String, document: String): Boolean
    suspend fun addFileWithUri(document: String, uri: Uri): UploadTask.TaskSnapshot?
    suspend fun getFileBytes(document: String, maxByteSize: Long): ByteArray?
}
