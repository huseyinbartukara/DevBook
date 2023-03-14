package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentGirisYapBinding
import com.bartukaraa.devbook.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GirisYapFragment : Fragment() {

    private lateinit var tasarim : FragmentGirisYapBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentGirisYapBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth


        tasarim.buttonGirisYap.setOnClickListener {

            val email = tasarim.editTextEmail.text.toString()
            val sifre = tasarim.editTextSifre.text.toString()

            if(email.equals("") || sifre.equals("")){
                Toast.makeText(requireContext(),"E-mail ve Şifre giriniz!",Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(email,sifre).addOnSuccessListener {
                    // Giriş Yapılırsa
                    Navigation.findNavController(tasarim.buttonGirisYap).navigate(R.id.mainFragmentGecis)

                }.addOnFailureListener {
                    // giriş yapılamazsa
                    Toast.makeText(requireContext(),"Bir sorun oluştu!!",Toast.LENGTH_LONG).show()
                }
            }
        }


        tasarim.buttonKaydol.setOnClickListener {

            val email = tasarim.editTextEmail.text.toString()
            val sifre = tasarim.editTextSifre.text.toString()

            if(email.equals("") || sifre.equals("")){
                Toast.makeText(requireContext(),"E-mail ve Şifre Giriniz!",Toast.LENGTH_LONG).show()
            }else{
                auth.createUserWithEmailAndPassword(email,sifre).addOnSuccessListener {
                    // kayıt başarılı olursa
                    Navigation.findNavController(tasarim.buttonGirisYap).navigate(R.id.mainFragmentGecis)

                }.addOnFailureListener {
                    // kayıt olurken bir sorun olursa
                    Toast.makeText(requireContext(),"Bir sorun oluştu daha sonra tekrar deneyiniz!!",Toast.LENGTH_LONG).show()
                }
            }

        }



        return view

    }
}