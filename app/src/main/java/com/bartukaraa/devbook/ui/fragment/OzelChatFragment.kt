package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentChatBinding
import com.bartukaraa.devbook.databinding.FragmentOzelChatBinding
import com.bartukaraa.devbook.ui.adapter.ChatSoruAdapter
import com.bartukaraa.devbook.ui.adapter.OzelChatAdapter
import com.bartukaraa.devbook.ui.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OzelChatFragment : Fragment() {

    private lateinit var tasarim : FragmentOzelChatBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private var chats = arrayListOf<ChatModel>()
    private lateinit var adapter : OzelChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentOzelChatBinding.inflate(inflater,container,false)
        val view = tasarim.root

        val bundle : OzelChatFragmentArgs by navArgs()
        val gelenUserUid = bundle.ozelchatuid


        adapter =   OzelChatAdapter()
        tasarim.rvOzelChat.adapter = adapter
        tasarim.rvOzelChat.layoutManager = LinearLayoutManager(requireContext())

        tasarim.buttonGonder.setOnClickListener {

            val chatText = tasarim.editTextOzelChat.text.toString()
            val user = auth.currentUser!!.email
            val date = FieldValue.serverTimestamp()

            val dataMap = HashMap<String,Any>()

            dataMap.put("text",chatText)
            if (user != null) {
                dataMap.put("user",user)
            }
            dataMap.put("date",date)

            firestore.collection("Chats").document(auth.currentUser!!.uid).collection("Chats").document(gelenUserUid).collection("Chats").add(dataMap).addOnSuccessListener {
                // başarılı eklenirse
                tasarim.editTextOzelChat.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Bir hata Oluştu", Toast.LENGTH_LONG).show()
            }
        }

        firestore.collection("Chats").document(auth.currentUser!!.uid).collection("Chats").document(gelenUserUid).collection("Chats").orderBy("date",
            Query.Direction.ASCENDING).addSnapshotListener { value, error ->
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
}