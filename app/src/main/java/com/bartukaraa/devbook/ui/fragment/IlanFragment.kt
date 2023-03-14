package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentIlanBinding
import com.bartukaraa.devbook.databinding.FragmentMainBinding
import com.bartukaraa.devbook.ui.adapter.IlanAdapter
import com.bartukaraa.devbook.ui.model.IlanModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class IlanFragment : Fragment() {

    private lateinit var tasarim : FragmentIlanBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var ilanArrayList :ArrayList<IlanModel>
    private lateinit var ilanAdapter : IlanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentIlanBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore

        ilanArrayList = ArrayList<IlanModel>()


        tasarim.toolbarIlan.title = ""
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarIlan)

        getData()

        tasarim.rvIlan.layoutManager = LinearLayoutManager(requireActivity())
        ilanAdapter = IlanAdapter(ilanArrayList)
        tasarim.rvIlan.adapter = ilanAdapter





        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.ilan_toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_ara) {

                        // ara buttonuna basılırsa
                }

                if(menuItem.itemId == R.id.action_ekle){

                        // ekle butonuna basılırsa
                    findNavController().navigate(R.id.ilanVerFragmentGecis)

                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)









        return view

    }

    private fun getData(){

        firestore.collection("Ilanlar").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireActivity(),"Ilan Yayınlarken Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        ilanArrayList.clear()


                        for(document in documents){

                            val ilgiAlani = document.get("ilgiAlani") as String
                            val sehir = document.get("sehir") as String
                            val universite = document.get("universite") as String
                            val adSoyad = document.get("adSoyad") as String
                            val uid = document.get("uid") as String

                            val ilan = IlanModel(ilgiAlani,sehir,universite,adSoyad,uid)
                            ilanArrayList.add(ilan)

                        }
                        ilanAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


}