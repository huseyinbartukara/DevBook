package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentEtkinlikBinding
import com.bartukaraa.devbook.databinding.FragmentGirisYapBinding
import com.bartukaraa.devbook.databinding.FragmentMainBinding
import com.bartukaraa.devbook.ui.adapter.EtkinlikAdapter
import com.bartukaraa.devbook.ui.model.EtkinliklerModel
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainFragment : Fragment() {

    private lateinit var tasarim : FragmentMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var etkinlikler : EtkinliklerModel
    private lateinit var etkinlikArrayList :ArrayList<EtkinliklerModel>
    private lateinit var etkinlikAdapter : EtkinlikAdapter
    private lateinit var adSoyad : String
    private lateinit var userUid : String
    private lateinit var kullaniciProfile : KullaniciProfile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentMainBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        getDataKullanici()








        tasarim.buttonMenu1.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.ilanFragmentGecis)
        }

        tasarim.buttonMenu2.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.soruFragmentGecis)
        }

        tasarim.buttonMenu3.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mesajlarFragmentGecis)
        }

        tasarim.buttonMenu4.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.etkinlikFragmentGecis)
        }

        tasarim.ProfileButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.profileFragmentGecis)
        }





        return view
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
                            val gelenUserUid = documentKullaniciProfile.get("uiid") as String

                            kullaniciProfile = KullaniciProfile(email,gelenAdSoyad,sehir,ilgiAlani,gelistirdigiDil,yas,cinsiyet,gitHub,linkedin,kullaniciProfileResim,gelenUserUid)

                            val userEmailActive = auth.currentUser?.email as String

                            if(kullaniciProfile.email.equals(userEmailActive)){
                                adSoyad = gelenAdSoyad
                                userUid = gelenUserUid
                                tasarim.textViewAdSoyad.text = "Selam, ${adSoyad}"
                                if(cinsiyet.equals("Kadın")){
                                    tasarim.imageViewProfileResim.setImageResource(R.drawable.profile_ic_kadin)
                                }else{
                                    tasarim.imageViewProfileResim.setImageResource(R.drawable.profile_ic_erkek)
                                }
                                }
                            }

                        }
                    }
                }
            }
        }
    }


