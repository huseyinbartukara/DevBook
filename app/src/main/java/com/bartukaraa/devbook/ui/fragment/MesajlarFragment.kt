package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentEtkinlikBinding
import com.bartukaraa.devbook.databinding.FragmentMesajlarBinding
import com.bartukaraa.devbook.databinding.FragmentOzelChatBinding
import com.bartukaraa.devbook.ui.adapter.MesajlasilanKisiAdapter
import com.bartukaraa.devbook.ui.model.ChatModel
import com.bartukaraa.devbook.ui.model.MesajlasilanKisiModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MesajlarFragment : Fragment() {

    private lateinit var tasarim : FragmentMesajlarBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private var mesajlasilanKisiArrayList = arrayListOf<MesajlasilanKisiModel>()
    private lateinit var adapter : MesajlasilanKisiAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentMesajlarBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore

        tasarim.toolbarMesajlar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarMesajlar)

        getDataMesaj()

        tasarim.rvMesajlar.layoutManager = LinearLayoutManager(requireActivity())
        adapter = MesajlasilanKisiAdapter(mesajlasilanKisiArrayList)
        tasarim.rvMesajlar.adapter = adapter


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.mesajlar_toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_ara) {

                    // ara buttonuna basılırsa
                }

                if(menuItem.itemId == R.id.action_ekle){

                    // ekle butonuna basılırsa
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)


        return view
    }

    private fun getDataMesaj(){
        firestore.collection("Chats").document(auth.currentUser!!.uid).collection("Chats").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireContext(),"Error",Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(value.isEmpty){
                        Toast.makeText(requireContext(),"Henüz Mesaj Yok neden",Toast.LENGTH_LONG).show()
                    }else{

                        val documents = value.documents
                        mesajlasilanKisiArrayList.clear()
                        for(document in documents){
                            val userid = document.id
                            val mesajlasilanKisi = MesajlasilanKisiModel(userid)
                            mesajlasilanKisiArrayList.add(mesajlasilanKisi)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }


    }

}