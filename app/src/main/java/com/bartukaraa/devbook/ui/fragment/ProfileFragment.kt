package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentProfileBinding
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var tasarim : FragmentProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var kullaniciProfile : KullaniciProfile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentProfileBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        db = Firebase.firestore

        getDataKullanici()

        tasarim.imageViewProfileEdit.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.profileEditGecis)
        }

        tasarim.imageViewCikisYap.setOnClickListener {
            auth.signOut()
            Navigation.findNavController(it).navigate(R.id.cikisYapGecis)
        }


        return view
    }


    private fun getDataKullanici(){

        db.collection("KullaniciProfile").addSnapshotListener { value, error ->
            if(error != null){
                // hata var demektir!
            }else{
                // hata yok demektir
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(documentKullaniciProfile in documents){
                            val email = documentKullaniciProfile.get("userEmail") as String
                            val adSoyad = documentKullaniciProfile.get("adSoyad") as String
                            val cinsiyet = documentKullaniciProfile.get("cinsiyet") as String
                            val gelistirdigiDil = documentKullaniciProfile.get("gelistirdigiDil") as String
                            val gitHub = documentKullaniciProfile.get("github") as String
                            val ilgiAlani = documentKullaniciProfile.get("ilgiAlani") as String
                            val linkedin = documentKullaniciProfile.get("linkedin") as String
                            val sehir = documentKullaniciProfile.get("sehir") as String
                            val yas = documentKullaniciProfile.get("yas") as String
                            val kullaniciProfileResim = documentKullaniciProfile.get("downloadUrlKullaniciProfil") as String
                            val userUiid = documentKullaniciProfile.get("uiid") as String


                            kullaniciProfile = KullaniciProfile(email,adSoyad,sehir,ilgiAlani,gelistirdigiDil,yas,cinsiyet,gitHub,linkedin,kullaniciProfileResim,userUiid)

                            val userEmailActive = auth.currentUser?.email as String

                            if(kullaniciProfile.email.equals(userEmailActive)){
                                tasarim.textViewAdSoyad.text = kullaniciProfile.adSoyad
                                tasarim.textViewSehir.text = kullaniciProfile.sehir
                                tasarim.textViewIlgiAlani.text = kullaniciProfile.ilgiAlani
                                tasarim.textViewGelistirdigiDil.text = kullaniciProfile.gelistirdigiDil
                                tasarim.textViewYas.text = kullaniciProfile.yas
                                tasarim.textViewCinsiyet.text = kullaniciProfile.cinsiyet
                                tasarim.textViewGithub.text = kullaniciProfile.gitHub
                                tasarim.textViewLinkedin.text = kullaniciProfile.linkedin
                                Picasso.get().load(kullaniciProfile.kullaniciProfileResim).into(tasarim.imageViewProfileResim)
                            }
                        }
                    }
                }
            }
        }
    }


}