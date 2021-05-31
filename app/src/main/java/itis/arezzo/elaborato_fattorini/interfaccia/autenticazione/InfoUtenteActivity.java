package itis.arezzo.elaborato_fattorini.interfaccia.autenticazione;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.databinding.ActivityInfoUtenteBinding;
import itis.arezzo.elaborato_fattorini.databinding.ActivityLoginBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.MainActivity;
import itis.arezzo.elaborato_fattorini.interfaccia.profilo.ProfiloActivity;
import itis.arezzo.elaborato_fattorini.modello.utente.Utenti;

public class InfoUtenteActivity extends AppCompatActivity {

    private ActivityInfoUtenteBinding binding;
    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private String uriImmagine;
    private String bio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_info_utente);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db=FirebaseFirestore.getInstance();

        db.collection("Utenti").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    binding.username.setText(task.getResult().getString("username"));
                    uriImmagine=task.getResult().getString("immagineProfilo");
                    bio=task.getResult().getString("bio");
                    if(uriImmagine!=null){
                        Glide.with(InfoUtenteActivity.this).load(uriImmagine).into(binding.immagineProfilo);
                    }
                    if(bio==null){
                        bio="Hey sto usando Blitzy!";
                    }
                } else {
                }
            }
        });


        progressDialog=new ProgressDialog(this);
        clickBottone();

    }


    private void clickBottone() {
        binding.bottoneContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(binding.username.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Inserisci almeno l'username",Toast.LENGTH_SHORT).show();
                }else{
                    eseguiAggiornamento();
                }

            }
        });

        /*
        binding.immagineProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prendiImmagine();
                Toast.makeText(getApplicationContext(),"Funzione non ancora implementata",Toast.LENGTH_SHORT).show();
            }
        });*/
    }


   private void eseguiAggiornamento() {
       progressDialog.setMessage("Caricamento...");
       progressDialog.show();
       FirebaseFirestore firestore=FirebaseFirestore.getInstance();
       FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
       if(firebaseUser!=null){
           if(uriImmagine!=null){
               String userID=firebaseUser.getUid();
               Utenti utenti=new Utenti(userID,
                       binding.username.getText().toString(),
                       firebaseUser.getPhoneNumber(),
                       uriImmagine,
                       bio);
               firestore.collection("Utenti").document(firebaseUser.getUid()).set(utenti)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               progressDialog.dismiss();
                               Toast.makeText(getApplicationContext(), "L'operazione ha avuto successo", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull @NotNull Exception e) {
                       progressDialog.dismiss();
                       Toast.makeText(getApplicationContext(), "Hai riscontrato un errore", Toast.LENGTH_SHORT).show();
                   }
               });
           }else{
               String userID=firebaseUser.getUid();
               Utenti utenti=new Utenti(userID,
                       binding.username.getText().toString(),
                       firebaseUser.getPhoneNumber(),
                       "",
                       bio);
               firestore.collection("Utenti").document(firebaseUser.getUid()).set(utenti)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               progressDialog.dismiss();
                               Toast.makeText(getApplicationContext(), "L'operazione ha avuto successo", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull @NotNull Exception e) {
                       progressDialog.dismiss();
                       Toast.makeText(getApplicationContext(), "Hai riscontrato un errore", Toast.LENGTH_SHORT).show();
                   }
               });
           }


       }else{
           Toast.makeText(getApplicationContext(), "Hai riscontrato un errore/L'utente esiste", Toast.LENGTH_SHORT).show();
           progressDialog.dismiss();

   }


}
}