package itis.arezzo.elaborato_fattorini.interfaccia.autenticazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.internal.DiskLruCache;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.databinding.ActivityLoginBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.MainActivity;
import itis.arezzo.elaborato_fattorini.modello.utente.Utenti;

public class LoginActivity extends AppCompatActivity{

    private static String TAG="LoginActivity";
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private String idVerificazione;
    private PhoneAuthProvider.ForceResendingToken rinvioToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    //String[] country={"Italia"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        /*Spinner spinner =findViewById(R.id.spinner_nazione);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,country);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);*/

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,InfoUtenteActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        binding.bottoneContinua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.bottoneContinua.getText().toString().equals("Continua")) {
                    String phone = "+"+binding.prefissoTelefono.getText().toString()+binding.numeroTelefono.getText().toString();
                        inizioVerificazioneTelefono(phone);
                }else{
                    progressDialog.setMessage("Verifica in corso...");
                    progressDialog.show();
                    verificaTelefonoCodice(idVerificazione,binding.codiceConferma.getText().toString());
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"onVerificazioneCompletata:Completa");
                accessoConCredenzialiTelefono(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                Log.d(TAG,"onVerificazioneFallita: "+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG,"onCodiceInviato:"+s);
                idVerificazione=s;
                rinvioToken=forceResendingToken;
                binding.bottoneContinua.setText("Conferma");
                binding.codiceConferma.setVisibility(View.VISIBLE);
                binding.prefissoTelefono.setEnabled(false);
                binding.numeroTelefono.setEnabled(false);
                progressDialog.dismiss();
            }
        };
    }

    public void inizioVerificazioneTelefono(String phoneNumber){
        progressDialog.setMessage("Invio codice a: "+phoneNumber);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    private void verificaTelefonoCodice(String verificationId,String code){
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        accessoConCredenzialiTelefono(credential);
    }

    private void accessoConCredenzialiTelefono(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Log.d(TAG,"accessoConCredenziali:successo");
                            FirebaseUser user=task.getResult().getUser();
                            startActivity(new Intent(LoginActivity.this, InfoUtenteActivity.class));


                        }else{
                            progressDialog.dismiss();
                            Log.d(TAG,"accessoConCredenziali:fallito",task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Log.d(TAG,"onCompletamento:Errore Codice");
                            }
                        }
                    }
                });
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(), country[i],Toast.LENGTH_LONG).show();
    }*/

}