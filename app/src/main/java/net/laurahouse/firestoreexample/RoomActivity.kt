package net.laurahouse.firestoreexample

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import net.laurahouse.firestoreexample.data.FirestoreUseCase
import net.laurahouse.firestoreexample.data.Message
import net.laurahouse.firestoreexample.databinding.ActivityRoomBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RoomActivity : AppCompatActivity() {

    private val binding: ActivityRoomBinding by lazy {
        DataBindingUtil.setContentView<ActivityRoomBinding>(
            this,
            R.layout.activity_room
        )
    }

    private val nickName: String by lazy { intent.getStringExtra("NICK_NAME") }

    private val masters = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirestoreUseCase.finalizeSnapshotListener()
        FirestoreUseCase.nickName = nickName
        FirestoreUseCase.initSnapshotListener()

        masters.apply {
            clear()
            addAll(FirestoreUseCase.messages)
        }

//        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MessageListAdapter(masters)
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.setHasFixedSize(true)

        binding.sendButton.setOnClickListener {
            val msg = binding.sendMessage.text.toString()
            if (!TextUtils.isEmpty(msg)) {
                FirestoreUseCase.addMessage(msg)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
    @Suppress("unused")
    fun onUpdateMessageEvent(event: UpdateMessagesEvent) {
        Log.d(TAG, "onUpdateMessageEvent()")
        event.messages?.let {
            masters.apply {
                clear()
                addAll(it)
            }
            (binding.recyclerView.adapter as? MessageListAdapter)?.update(masters)
        }
    }
    companion object {
        val TAG = RoomActivity::class.java.simpleName!!

        fun createIntent(context: Context, nickName: String): Intent {
            return Intent(context, RoomActivity::class.java).apply {
                putExtra("NICK_NAME", nickName)
            }
        }
    }
}
