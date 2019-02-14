package net.laurahouse.firestoreexample

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import net.laurahouse.firestoreexample.data.FirestoreUseCase
import net.laurahouse.firestoreexample.data.Message
import net.laurahouse.firestoreexample.databinding.LayoutMessageBinding

class MessageListAdapter(
    private val masters: MutableList<Message>
) : RecyclerView.Adapter<MessageListAdapter.MessageListViewHolder>() {

    class MessageListViewHolder(val binding: LayoutMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        return MessageListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_message,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return masters.count()
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {

        holder.binding.message.text = masters[position].message
        holder.binding.nickName.text = masters[position].name
        if (FirestoreUseCase.nickName == masters[position].name) {
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
            }
            holder.binding.message.layoutParams = lp
            holder.binding.message.setTextColor(Color.RED)
            holder.binding.nickName.layoutParams = lp
        } else {
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.START
            }
            holder.binding.message.layoutParams = lp
            holder.binding.message.setTextColor(Color.BLACK)
            holder.binding.nickName.layoutParams = lp

        }
    }

    fun update(items: MutableList<Message>) {
        Log.d(TAG, "update()")
//        masters.clear()
//        masters.addAll(items)
        Log.d(TAG, "count=${masters.size}")
        notifyDataSetChanged()
    }

    interface OnNoticeListClickListener {
        fun onClickNoticeList(item: Message)
    }

    companion object {
        private val TAG = MessageListAdapter::class.java.simpleName
    }
}