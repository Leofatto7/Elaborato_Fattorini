package itis.arezzo.elaborato_fattorini.interfaccia.impostazioni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.databinding.ActivityImpostazioniBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.profilo.ProfiloActivity;
import itis.arezzo.elaborato_fattorini.modello.utente.Utenti;

public class ImpostazioniActivity extends AppCompatActivity {

    private ActivityImpostazioniBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_impostazioni);

        firestore= FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            getInformazioni();
        }
        azioneClick();
    }

    private void azioneClick() {
        binding.impostazioniProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImpostazioniActivity.this, ProfiloActivity.class));
                finish();
            }
        });
    }

    private void getInformazioni() {
        firestore.collection("Utenti").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username= documentSnapshot.get("username").toString();
                binding.impoUsername.setText(username);
                String bio= documentSnapshot.get("bio").toString();
                binding.impoBio.setText(bio);
                String immagineProfilo= documentSnapshot.get("immagineProfilo").toString();
                if (immagineProfilo.equals("")){
                    Glide.with(ImpostazioniActivity.this).load(R.drawable.profilo_default).into(binding.impoImmagine);
                }else{
                    Glide.with(ImpostazioniActivity.this).load(immagineProfilo).into(binding.impoImmagine);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("Prendi i dati","onFallimento: "+e.getMessage());
            }
        });
    }
}