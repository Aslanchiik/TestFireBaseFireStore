package com.example.data.repositories

import com.example.model.TaskModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class FireStoreRepos @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val server: ExecutorService
) {
    private val collectionReference = firebaseFireStore.collection("users")

    //    fun setupToFireStore(user: HashMap<String, Any>) {
//        collectionReference.add(user).addOnCompleteListener { document ->
//            if (document.isSuccessful) {
//                Log.e(TAG, "is good")
//            } else {
//                Log.e(TAG, "Not bad$document")
//            }
//
//        }
//    }
    suspend fun saveDataInFireStore(
        hashMap: HashMap<String, Any>
    ): Boolean {
        return  try {
            collectionReference.document()
                .set(hashMap).await()
             return true
        } catch (e : Exception) {
            return false
        }
    }

    @ExperimentalCoroutinesApi
    fun CollectionReference.getQuerySnapshotFlow(): Flow<QuerySnapshot?> {
        return callbackFlow {
            val listenerRegistration =
                addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        cancel(
                            message = "error fetching collection data at path - $path",
                            cause = firebaseFirestoreException
                        )
                        return@addSnapshotListener
                    }
                    offer(querySnapshot)
                }
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun <T> CollectionReference.getDataFlow(mapper: (QuerySnapshot?) -> T): Flow<T> {
        return getQuerySnapshotFlow()
            .map {
                return@map mapper(it)
            }
    }

    @ExperimentalCoroutinesApi
    fun getShoppingListItemsFlow(): Flow<List<TaskModel>> {
        return collectionReference
            .getDataFlow { querySnapshot ->
                querySnapshot?.documents?.map {
                    getShoppingListItemFromSnapshot(it)
                } ?: listOf()
            }
    }

    private fun getShoppingListItemFromSnapshot(documentSnapshot: DocumentSnapshot): TaskModel {
        return documentSnapshot.toObject(TaskModel::class.java)!!
    }

    fun delete() {
        deleteCollection(collectionReference, server)
    }

    private fun deleteCollection(
        collectionReference: CollectionReference,
        server: ExecutorService
    ) {
        Tasks.call(server) {
            val batchSize = 10
            var query =
                collectionReference.orderBy(FieldPath.documentId()).limit(batchSize.toLong())
            var deleted = deleteQueryBatch(query)

            while (deleted.size > batchSize) {
                val last = deleted[deleted.size - 1]
                query = collectionReference.orderBy(FieldPath.documentId()).startAfter(last.id)
                    .limit(batchSize.toLong())
                deleted = deleteQueryBatch(query)
            }
            null

        }
    }

    private fun deleteQueryBatch(query: Query): List<DocumentSnapshot> {
        val querySnapshot = Tasks.await(query.get())
        val batch = query.firestore.batch()
        for (snapshot in querySnapshot) {
            batch.delete(snapshot.reference)
        }
        Tasks.await(batch.commit())

        return querySnapshot.documents
    }
}