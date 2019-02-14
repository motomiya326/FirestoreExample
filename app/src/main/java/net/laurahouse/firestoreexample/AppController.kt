package net.laurahouse.firestoreexample

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import net.laurahouse.firestoreexample.data.FirestoreUseCase


open class AppController : Application() {

    companion object {
        private val TAG = AppController::class.java.simpleName
        private lateinit var appController: AppController
        @JvmStatic
        fun getInstance() = appController

    }

    lateinit var firebaseFirestore: FirebaseFirestore

    init {
        appController = this
    }


    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()
        initFirebase()
        // Firestoreの更新イベントハンドラー設定
        FirestoreUseCase.initSnapshotListener()
    }

    // 実際は呼ばれないけど念の為
    override fun onTerminate() {
        Log.d(TAG, "onTerminate()")
        FirestoreUseCase.finalizeSnapshotListener()
        super.onTerminate()
    }

    open fun initFirebase() {
        FirebaseApp.initializeApp(this)
        firebaseFirestore = FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    }

}