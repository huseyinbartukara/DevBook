package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentChatBinding
import com.bartukaraa.devbook.ui.adapter.ChatSoruAdapter
import com.bartukaraa.devbook.ui.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap


class ChatFragment : Fragment() {

    private lateinit var tasarim : FragmentChatBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private var chats = arrayListOf<ChatModel>()
    private lateinit var adapter : ChatSoruAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore

        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentChatBinding.inflate(inflater,container,false)
        val view = tasarim.root

        tasarim.toolbarChat.title = "Chat Screen"
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarChat)

        val bundle : ChatFragmentArgs by navArgs()
        val gelenSoruid = bundle.useruid



        //tasarim.editTextChat.setText("${gelenUserid}")

        adapter = ChatSoruAdapter()
        tasarim.rvChat.adapter = adapter
        tasarim.rvChat.layoutManager = LinearLayoutManager(requireContext())


        tasarim.buttonChat.setOnClickListener {

            val chatText = tasarim.editTextChat.text.toString()
            val user = auth.currentUser!!.email
            val date = FieldValue.serverTimestamp()

            val dataMap = HashMap<String,Any>()

            dataMap.put("text",chatText)
            if (user != null) {
                dataMap.put("user",user)
            }
            dataMap.put("date",date)

            firestore.collection("ChatRooms").document(gelenSoruid).collection("Chats").add(dataMap).addOnSuccessListener {
                // başarılı eklenirse
                tasarim.editTextChat.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Bir hata Oluştu", Toast.LENGTH_LONG).show()
            }
        }


        firestore.collection("ChatRooms").document(gelenSoruid).collection("Chats").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(),"Henüz Mesaj Yok",Toast.LENGTH_LONG).show()
                    }else{

                        val documents = value.documents
                        chats.clear()
                        for(document in documents){
                            val text = document.get("text") as String
                            val user = document.get("user") as String
                            val chat = ChatModel(user,text)
                            chats.add(chat)
                            adapter.chats = chats
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }

}