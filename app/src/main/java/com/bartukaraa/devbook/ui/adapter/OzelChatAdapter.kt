package com.bartukaraa.devbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.ui.model.ChatModel
import com.google.firebase.auth.FirebaseAuth

class OzelChatAdapter : RecyclerView.Adapter<OzelChatAdapter.OzelChatHolder>(){

    private val YOLANAN_MESAJ = 1
    private val GELEN_MESAJ = 2

    class OzelChatHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

    }

    private val diffUtil = object : DiffUtil.ItemCallback<ChatModel>(){
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var chats : List<ChatModel>
    get()=recyclerListDiffer.currentList
    set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = chats.get(position)
        if(chat.user == FirebaseAuth.getInstance().currentUser!!.email.toString()){
            return YOLANAN_MESAJ
        }else{
            return GELEN_MESAJ
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OzelChatHolder {
        if(viewType == GELEN_MESAJ){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_text,parent,false)
            return OzelChatAdapter.OzelChatHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_text_right,parent,false)
            return OzelChatAdapter.OzelChatHolder(view)
        }
    }

    override fun onBindViewHolder(holder: OzelChatHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.chatRecylerTextView)
        textView.text = "${chats.get(position).user}: ${chats.get(position).text}"
    }

    override fun getItemCount(): Int {
        return chats.size
    }


}