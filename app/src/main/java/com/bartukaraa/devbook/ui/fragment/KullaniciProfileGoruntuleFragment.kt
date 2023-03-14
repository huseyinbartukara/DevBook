package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentKullaniciProfileGoruntuleBinding
import com.bartukaraa.devbook.databinding.FragmentProfileBinding
import com.bartukaraa.devbook.ui.adapter.OzelChatAdapter
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class KullaniciProfileGoruntuleFragment : Fragment() {

    private lateinit var tasarim : FragmentKullaniciProfileGoruntuleBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var kullaniciProfile : KullaniciProfile
    private lateinit var gelenUid : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentKullaniciProfileGoruntuleBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        db = Firebase.firestore

        val bundle:KullaniciProfileGoruntuleFragmentArgs by navArgs()
        gelenUid = bundle.useruid

        getDataKullanici()


        tasarim.imageViewMesajAt.setOnClickListener {
            val gecis = KullaniciProfileGoruntuleFragmentDirections.ozelChatFragmentGecis(ozelchatuid = gelenUid)
            Navigation.findNavController(it).navigate(gecis)
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

                            val userUid = gelenUid

                            if(kullaniciProfile.userUiid.equals(userUid)){
                                tasarim.textViewAdSoyadS.text = "Selam, ${kullaniciProfile.adSoyad}"
                                tasarim.textViewSehirS.text = kullaniciProfile.sehir
                                tasarim.textViewIlgiAlaniS.text = kullaniciProfile.ilgiAlani
                                tasarim.textViewGelistirdigiDilS.text = kullaniciProfile.gelistirdigiDil
                                tasarim.textViewYasS.text = kullaniciProfile.yas
                                tasarim.textViewCinsiyetS.text = kullaniciProfile.cinsiyet
                                tasarim.textViewGithubS.text = kullaniciProfile.gitHub
                                tasarim.textViewLinkedinS.text = kullaniciProfile.linkedin
                                Picasso.get().load(kullaniciProfile.kullaniciProfileResim).into(tasarim.imageViewProfileResimS)
                            }
                        }
                    }
                }
            }
        }
    }



}