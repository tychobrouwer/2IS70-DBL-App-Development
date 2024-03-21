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

class FireBase {
    private val db = Firebase.firestore
    private val dbAuth = FirebaseAuth.getInstance()
    private val dbStore = FirebaseStorage.getInstance()

    fun currentUserId() = dbAuth.currentUser!!.uid
    fun currentUserEmail() = dbAuth.currentUser!!.email

    suspend fun addFileWithUri(document: String, uri: Uri) : UploadTask.TaskSnapshot? {
        return try {
            dbStore.getReference(document).putFile(uri).await()
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun addDocumentWithName(collection: String, document: String, data: Any) : Boolean {
        return try {
            db.collection(collection).document(document).set(data).await()
            true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun addDocument(collection: String, data: Any) : DocumentReference? {
        return try {
            db.collection(collection).add(data).await()
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getDocument(collection: String, document: String) : DocumentSnapshot? {
        return try {
            db.collection(collection).document(document).get().await()
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getDocumentsWithFilter(
        collection: String, filter: Filter
    ) : QuerySnapshot? {
        return try {
            db.collection(collection).where(filter).get().await()
        } catch (e: Exception) {
            return null
        }
    }

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
}