package com.example.weclean.backend

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

open class FireBase {
    // FireBase class instance to communicate with the database
    private val db = Firebase.firestore
    // FireBase authentication instance
    private val dbAuth = FirebaseAuth.getInstance()
    // FireBase storage instance
    private val dbStore = FirebaseStorage.getInstance()

    // Get the currently logged in user's ID and email
    fun currentUserId(): String? {
        val currentUser = dbAuth.currentUser
        return currentUser?.uid
    }
    fun currentUserEmail() = dbAuth.currentUser?.email

    /**
     * Add a file to the storage with the given document name
     *
     * @param document
     * @param uri
     * @return TaskSnapshot of the file upload
     */
    suspend fun addFileWithUri(document: String, uri: Uri) : UploadTask.TaskSnapshot? {
        return try {
            dbStore.getReference(document).putFile(uri).await()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Get the file bytes from the storage with the given document name
     *
     * @param document
     * @param maxByteSize
     * @return ByteArray of the file bytes
     */
    suspend fun getFileBytes(document: String, maxByteSize: Long) : ByteArray? {
        return try {
            dbStore.getReference(document).getBytes(maxByteSize).await()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Add a document to the collection with the given data and document name
     *
     * @param collection
     * @param document
     * @param data
     * @return Boolean if adding the document was successful
     */
    suspend fun addDocumentWithName(collection: String, document: String, data: Any) : Boolean {
        return try {
            db.collection(collection).document(document).set(data).await()
            true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Add a document to the collection with the given data
     *
     * @param collection
     * @param data
     * @return DocumentReference of the added document
     */
    suspend fun addDocument(collection: String, data: Any) : DocumentReference? {
        return try {
            db.collection(collection).add(data).await()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Get a document from the collection with the given document name
     *
     * @param collection
     * @param document
     * @return DocumentSnapshot of the document
     */
    suspend fun getDocument(collection: String, document: String) : DocumentSnapshot? {
        return try {
            db.collection(collection).document(document).get().await()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Get documents from the collection using a filter
     *
     * @param collection
     * @param filter
     * @return QuerySnapshot of the documents
     */
    suspend fun getDocumentsWithFilter(
        collection: String, filter: Filter
    ) : QuerySnapshot? {
        return try {
            db.collection(collection).where(filter).get().await()
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Update a document field in the collection with the given data and document name
     *
     * @param collection
     * @param document
     * @param field
     * @param value
     * @return Boolean if updating the document field was successful
     */
    suspend fun updateValue(
        collection: String, document: String, field: String, value: Any
    ) : Boolean {
        return try {
            db.collection(collection).document(document).update(field, value).await()
            true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Add a value to an array in a document in the collection
     *
     * @param collection
     * @param document
     * @param field
     * @param value
     * @return Boolean if adding the value to the array was successful
     */
    suspend fun addToArray(
        collection: String, document: String, field: String, value: Any
    ) : Boolean {
        return try {
            db.collection(collection).document(document).update(field, FieldValue.arrayUnion(value)).await()
            true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Remove a value from an array in a document in the collection
     *
     * @param collection
     * @param document
     * @param field
     * @param value
     * @return Boolean if removing the value from the array was successful
     */
    suspend fun removeFromArray(
        collection: String, document: String, field: String, value: Any
    ) : Boolean {
        return try {
            db.collection(collection).document(document)
                .update(field, FieldValue.arrayRemove(value)).await()
            true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun deleteDocument(collection: String, document: String): Boolean {
        return try {
            db.collection(collection).document(document).delete().await()
            true
        } catch (e: Exception) {
            return false
        }

    }
}