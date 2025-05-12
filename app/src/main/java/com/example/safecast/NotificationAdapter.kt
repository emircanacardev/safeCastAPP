package com.example.safecast

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safecast.databinding.NotificationItemViewBinding
import com.example.safecast.databinding.RelativesItemViewBinding

class NotificationAdapter(
    private val mContext: Context,
    private val notificationList: ArrayList<NotificationDataClass>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolderClass>() {

    inner class ViewHolderClass(var itemViewBinding: NotificationItemViewBinding) : RecyclerView.ViewHolder(itemViewBinding.root){}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val binding = NotificationItemViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolderClass(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val notification = notificationList[position]
        val view = holder.itemViewBinding

        view.textSender.text = notification.sender
        view.textMessage.text = notification.message
        view.textLocation.text = notification.location
        view.textTimestamp.text = notification.timestamp
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}
