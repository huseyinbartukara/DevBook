package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentIlanBinding
import com.bartukaraa.devbook.databinding.FragmentSoruBinding
import com.bartukaraa.devbook.ui.adapter.EtkinlikAdapter
import com.bartukaraa.devbook.ui.adapter.SoruAdapter
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.bartukaraa.devbook.ui.model.SoruModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList


class SoruFragment : Fragment() {

    private lateinit var tasarim : FragmentSoruBinding
    private lateinit var yazilimDilSecim : String
    private  var secilenDil : String =""
    private var  secilenKonu : String=""
    private lateinit var sorular : SoruModel
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var soruArrayList : ArrayList<SoruModel>
    private lateinit var soruAdapter: SoruAdapter
    private lateinit var adSoyad : String
    private lateinit var kullaniciProfile : KullaniciProfile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentSoruBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        soruArrayList = ArrayList<SoruModel>()

        val uuid = UUID.randomUUID().toString()

        getDataKullanici()
        getDataSoru()


        tasarim.toolbarSoru.title = ""
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarSoru)

        tasarim.rvSoru.layoutManager = LinearLayoutManager(requireActivity())
        soruAdapter = SoruAdapter(soruArrayList)
        tasarim.rvSoru.adapter = soruAdapter


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.soru_toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_ara) {

                    // ara buttonuna basılırsa
                }

                if(menuItem.itemId == R.id.action_ekle){

                    // ekle butonuna basılırsa

                    val tasarim = LayoutInflater.from(requireContext()).inflate(R.layout.soru_sor_alertview_tasarim,null)

                    val spinnerDil = tasarim.findViewById(R.id.spinnerDil) as Spinner
                    val editTextKonu = tasarim.findViewById(R.id.editTextKonu) as EditText


                    var yazilimDili = resources.getStringArray(R.array.yazilimDilleri)

                    if(spinnerDil != null){
                        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,yazilimDili)
                        spinnerDil.adapter = adapter

                        spinnerDil.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                // secilen değeri veriyor
                                yazilimDilSecim = yazilimDili[p2]




                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    }



                    val ad = AlertDialog.Builder(requireContext())




                    ad.setTitle("Soru Sor")
                    ad.setView(tasarim)
                    ad.setPositiveButton("Soru Ekle"){dialogInterface, i ->

                        var konu = editTextKonu.text.toString()

                        secilenDil = yazilimDilSecim
                        secilenKonu = konu

                        if(auth.currentUser != null){
                            val soruMap = hashMapOf<String,Any>()


                            soruMap.put("konu",secilenKonu)
                            soruMap.put("secilenDil",secilenDil)
                            soruMap.put("adSoyad",adSoyad)
                            soruMap.put("soruid", uuid)

                            firestore.collection("Sorular").add(soruMap).addOnSuccessListener {
                                // firestore içerisine aktarılırsa

                            }.addOnFailureListener {
                                // bir sorun olursa
                                Toast.makeText(requireActivity(),"Bir sorun ile karşılaştık...",Toast.LENGTH_LONG).show()
                            }

                        }
                    }

                    ad.setNegativeButton("İptal"){dialogInterface, i ->

                    }
                    ad.create().show()
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)


        return view
    }

    private fun getDataSoru(){

        firestore.collection("Sorular").addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireActivity(),"Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
            }else{
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents
                        soruArrayList.clear()

                        for(documentSorular in documents){

                            val konu = documentSorular.get("konu") as String
                            val dil = documentSorular.get("secilenDil") as String
                            val adSoyadGelen = documentSorular.get("adSoyad") as String
                            val gelenSoruid = documentSorular.get("soruid") as String

                            val soru = SoruModel(konu,dil,adSoyadGelen, gelenSoruid)
                            soruArrayList.add(soru)
                        }
                        soruAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getDataKullanici(){

        firestore.collection("KullaniciProfile").addSnapshotListener { value, error ->
            if(error != null){
                // hata var demektir!
            }else{
                // hata yok demektir
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(documentKullaniciProfile in documents){
                            val email = documentKullaniciProfile.get("userEmail") as String
                            val gelenAdSoyad = documentKullaniciProfile.get("adSoyad") as String
                            val cinsiyet = documentKullaniciProfile.get("cinsiyet") as String
                            val gelistirdigiDil = documentKullaniciProfile.get("gelistirdigiDil") as String
                            val gitHub = documentKullaniciProfile.get("github") as String
                            val ilgiAlani = documentKullaniciProfile.get("ilgiAlani") as String
                            val linkedin = documentKullaniciProfile.get("linkedin") as String
                            val sehir = documentKullaniciProfile.get("sehir") as String
                            val yas = documentKullaniciProfile.get("yas") as String
                            val kullaniciProfileResim = documentKullaniciProfile.get("downloadUrlKullaniciProfil") as String
                            val userUid = documentKullaniciProfile.get("uiid") as String

                            kullaniciProfile = KullaniciProfile(email,gelenAdSoyad,sehir,ilgiAlani,gelistirdigiDil,yas,cinsiyet,gitHub,linkedin,kullaniciProfileResim,userUid)

                            val userEmailActive = auth.currentUser?.email as String

                            if(kullaniciProfile.email.equals(userEmailActive)){
                                adSoyad = gelenAdSoyad
                            }

                        }
                    }
                }
            }
        }
    }


}