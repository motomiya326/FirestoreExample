package net.laurahouse.firestoreexample.data

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import net.laurahouse.firestoreexample.AppController
import net.laurahouse.firestoreexample.UpdateMessagesEvent
import org.greenrobot.eventbus.EventBus
import java.util.*


class FirestoreUseCase {

    companion object {
        val TAG = FirestoreUseCase::class.java.simpleName!!

        private var messagesRegistration: ListenerRegistration? = null

        val messages = mutableListOf<Message>()
        var nickName = ""

        fun finalizeSnapshotListener() {
            Log.d(TAG, "finalizeSnapshotListener()")
            messagesRegistration?.remove()
            messagesRegistration = null
            messages.clear()
        }

        fun initSnapshotListener() {
            Log.d(TAG, "initSnapshotListener()")
            // 更新ハンドラー設定
            messagesRegistration = AppController.getInstance().firebaseFirestore
                .collection("messages")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val messages = mutableListOf<Message>().apply {
                            snapshot.documents.forEach {
                                val message = it.toObject(Message::class.java)?.apply {
                                    documentId = it.id
                                }
                                if (message != null) {
                                    add(message)
                                }
                            }
                        }.asSequence().sortedBy {
                            it.updateAt
                        }.toMutableList()

                        Companion.messages.apply {
                            clear()
                            addAll(messages)
                        }
                        EventBus.getDefault().post(UpdateMessagesEvent(Companion.messages))
                        Log.d(TAG, "更新された: messages=${messages.first()}")
                        messages.clear()
                    } else {
                        Log.d(TAG, "Current messages: null")
                        messages.clear()
                        EventBus.getDefault().post(UpdateMessagesEvent(messages))
                    }
                }

        }

        fun addMessage(msg: String) {
            Log.d(TAG, "addMessage(msg=$msg)")
            val message = Message().apply {
                name = nickName
                message = msg
                updateAt = Date()
            }
            AppController.getInstance().firebaseFirestore
                .collection("messages")
                .add(message)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "addMessage() success")
                    } else {
                        Log.d(TAG, "addMessage() error")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "addMessage(msg=$msg) error:", it)
                }


        }


        fun deleteAll() {
            Log.d(TAG, "deleteAll()")
            val refs = mutableListOf<DocumentReference>()

            messages.forEach { message ->
                refs.add(AppController.getInstance().firebaseFirestore
                    .collection("messages")
                    .document(message.documentId!!))
            }

            // batch処理は一度に処理できる件数が500件まで
            val batch = AppController.getInstance().firebaseFirestore.batch()
            refs.forEachIndexed { index, documentReference ->
                batch.delete(documentReference)
                if ((index + 1 % 500) == 0) {
                    batch.commit().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "batch commit success")
                        } else {
                            Log.d(TAG, "batch commit error", it.exception)
                        }
                    }
                }
            }

            batch.commit().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "batch commit success")
                } else {
                    Log.d(TAG, "batch commit error", it.exception)
                }

            }
            refs.clear()
        }




    }
}