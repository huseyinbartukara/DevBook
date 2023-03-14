package com.bartukaraa.devbook.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentEtkinlikBinding
import com.bartukaraa.devbook.databinding.FragmentProfileEditBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class ProfileEditFragment : Fragment() {

    private lateinit var tasarim : FragmentProfileEditBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storge : FirebaseStorage
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    private lateinit var adSoyad  : String
    private lateinit var sehir :String
    private lateinit var ilgiAlani :String
    private lateinit var dil : String
    private lateinit var yas : String
    private lateinit var secilenCinsiyet : String
    private lateinit var gitHub : String
    private lateinit var linkedin : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentProfileEditBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storge = Firebase.storage

        val spinnerCinsiyet = tasarim.spinnerCinsiyet.findViewById(R.id.spinnerCinsiyet) as Spinner
        var cinsiyet = resources.getStringArray(R.array.cinsiyet)

        if(spinnerCinsiyet != null){
            val adapter:ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,cinsiyet)
            spinnerCinsiyet.adapter = adapter

            spinnerCinsiyet.onItemSelectedListener = object  :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    secilenCinsiyet = cinsiyet[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

        }

        registerLauncher()



        tasarim.imageViewResimSec.setOnClickListener {
            // resim secicek kullanıcı.
            selectImage(it)
        }

        tasarim.buttonKaydet.setOnClickListener {
             adSoyad = tasarim.editTextAdSoyad.text.toString()
             sehir = tasarim.editTextSehir.text.toString()
             val cinsiyet = secilenCinsiyet
             ilgiAlani = tasarim.editTextIlgiAlani.text.toString()
             dil = tasarim.editTextGelistirdigiDil.text.toString()
             yas = tasarim.editTextYas.text.toString()
             gitHub = tasarim.editTextGithub.text.toString()
             linkedin = tasarim.editTextLinkedin.text.toString()





            if(adSoyad.equals("") || sehir.equals("") || ilgiAlani.equals("") || dil.equals("") || yas.equals("") || secilenCinsiyet.equals("") || gitHub.equals("") || linkedin.equals("")){
                Toast.makeText(requireActivity(),"Lütfen Her Alanı Doldurduğunuzdan Emin Olun!", Toast.LENGTH_LONG).show()
            }else{
                upload(it)
            }


        }


        return view
    }

    fun upload(view: View){



        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storge.reference
        val imageReference = reference.child("kullaniciProfileImages").child(imageName)

        if(selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                // upload edilirse
                val uploadPictureReference = storge.reference.child("kullaniciProfileImages").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    // bize resmin storage da nereye kayıtlı oldugunu uri ile veriyor
                    val dowloadUrl = it.toString()

                    if(auth.currentUser != null){
                        val postMap = hashMapOf<String, Any>()

                        postMap.put("downloadUrlKullaniciProfil",dowloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!)
                        postMap.put("adSoyad",adSoyad)
                        postMap.put("sehir",sehir)
                        postMap.put("ilgiAlani",ilgiAlani)
                        postMap.put("gelistirdigiDil",dil)
                        postMap.put("yas",yas)
                        postMap.put("cinsiyet",secilenCinsiyet)
                        postMap.put("github",gitHub)
                        postMap.put("linkedin",linkedin)
                        postMap.put("uiid", auth.currentUser!!.uid)


                        firestore.collection("KullaniciProfile").document(auth.currentUser!!.uid).set(postMap).addOnSuccessListener {
                            // firestore içerisine aktarılırsa

                            Navigation.findNavController(tasarim.buttonKaydet).navigate(R.id.profileFragmentGecisEdit)

                        }.addOnFailureListener {
                            // fireStore içerisine aktaramazsam
                            Toast.makeText(requireActivity(),"Bir sorun oluştu.",Toast.LENGTH_LONG).show()
                        }
                    }


                }

            }.addOnFailureListener{
                // UPLOAD başarısız olursa
                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }




    fun selectImage(view:View){

        if(ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // izin yok demek
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission Needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    // izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                // izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            // izin var demek galeriye gidicek.
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    selectedPicture = intentFromResult.data // galeriden gelen verinin uri si
                    selectedPicture?.let {
                        tasarim.imageViewResimSec.setImageURI(it) // kullanıcı arayuzde sectiği resmi görsün diye
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                // izin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                // izin verilmedi
                Toast.makeText(requireActivity(),"permission needed!", Toast.LENGTH_LONG).show()
            }
        }

    }


}