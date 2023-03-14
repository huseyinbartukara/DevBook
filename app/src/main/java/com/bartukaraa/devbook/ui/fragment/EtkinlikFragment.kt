package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentEtkinlikBinding
import com.bartukaraa.devbook.databinding.FragmentIlanBinding
import com.bartukaraa.devbook.ui.adapter.EtkinlikAdapter
import com.bartukaraa.devbook.ui.adapter.IlanAdapter
import com.bartukaraa.devbook.ui.model.EtkinliklerModel
import com.bartukaraa.devbook.ui.model.IlanModel
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class EtkinlikFragment : Fragment() {

    private lateinit var tasarim : FragmentEtkinlikBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var etkinlikler : EtkinliklerModel
    private lateinit var etkinlikArrayList :ArrayList<EtkinliklerModel>
    private lateinit var etkinlikAdapter : EtkinlikAdapter
    private lateinit var adSoyad : String
    private lateinit var kullaniciProfile : KullaniciProfile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentEtkinlikBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        getDataKullanici()
        getDataEtklinlik()

        etkinlikArrayList = ArrayList<EtkinliklerModel>()

        tasarim.toolbarEtkinlik.title = ""
        (activity as AppCompatActivity).setSupportActionBar(tasarim.toolbarEtkinlik)



        tasarim.rvEtkinlik.layoutManager = LinearLayoutManager(requireActivity())
        etkinlikAdapter = EtkinlikAdapter(etkinlikArrayList)
        tasarim.rvEtkinlik.adapter = etkinlikAdapter



        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.etkinlik_toolbar_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_ara) {

                    // ara buttonuna basılırsa
                }

                if(menuItem.itemId == R.id.action_ekle){

                    // ekle butonuna basılırsa

                    val tasarim = LayoutInflater.from(requireContext()).inflate(R.layout.etkinlik_yap_alertview_tasarim,null)

                    val editTextMekan = tasarim.findViewById(R.id.editTextMekan) as EditText
                    val editTextKonu = tasarim.findViewById(R.id.editTextKonu) as EditText
                    val editTextZamanSaat = tasarim.findViewById(R.id.editTextTimePickerAlert) as EditText
                    val editTextZamanGun = tasarim.findViewById(R.id.editTextDatePickerAlert) as EditText


                    val ad = AlertDialog.Builder(requireContext())


                    ad.setTitle("Etkinlik Oluştur")
                    ad.setView(tasarim)
                    ad.setPositiveButton("Oluştur"){dialogInterface, i ->

                        var konu = editTextKonu.text.toString()
                        var mekan = editTextMekan.text.toString()
                        var zamanSaat = editTextZamanSaat.text.toString()
                        var zamanGun = editTextZamanGun.text.toString()



                        //---------------------------

                        if(auth.currentUser != null){
                            val etkinlikMap = hashMapOf<String, Any>()


                            etkinlikMap.put("konu",konu)
                            etkinlikMap.put("mekan",mekan)
                            etkinlikMap.put("zamanSaat",zamanSaat)
                            etkinlikMap.put("zamanGun",zamanGun)
                            etkinlikMap.put("uid", auth.currentUser!!.uid)
                            etkinlikMap.put("adSoyad",adSoyad)



                            firestore.collection("Etkinlikler").add(etkinlikMap).addOnSuccessListener {
                                // firestore içerisine aktarılırsa
                                Toast.makeText(requireContext(),"Etkinlik Oluşturuluyor",Toast.LENGTH_LONG).show()

                            }.addOnFailureListener {
                                // fireStore içerisine aktaramazsam
                                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }


                        //--------------------------




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

    private fun getDataEtklinlik(){

        firestore.collection("Etkinlikler").addSnapshotListener { value, error ->
         if(error != null){
             Toast.makeText(requireActivity(),"Bir Hata Oluştu!", Toast.LENGTH_LONG).show()
         }else{
             if(value != null){
                 if(!value.isEmpty){
                     val documents = value.documents

                     for(documentEtkinlikler in documents){

                         val konu = documentEtkinlikler.get("konu") as String
                         val mekan = documentEtkinlikler.get("mekan") as String
                         val zamanGun = documentEtkinlikler.get("zamanGun") as String
                         val zamanSaat = documentEtkinlikler.get("zamanSaat") as String
                         val uid = documentEtkinlikler.get("uid") as String
                         val adSoyad = documentEtkinlikler.get("adSoyad") as String

                        etkinlikler = EtkinliklerModel(konu,mekan,uid,zamanGun,zamanSaat,adSoyad)
                         etkinlikArrayList.add(etkinlikler)
                     }
                     etkinlikAdapter.notifyDataSetChanged()
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