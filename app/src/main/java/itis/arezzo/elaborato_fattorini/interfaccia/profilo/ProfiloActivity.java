package itis.arezzo.elaborato_fattorini.interfaccia.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import itis.arezzo.elaborato_fattorini.BuildConfig;
import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.databinding.ActivityProfiloBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.impostazioni.ImpostazioniActivity;
import itis.arezzo.elaborato_fattorini.interfaccia.startup.SplashScreenActivity;

public class ProfiloActivity extends AppCompatActivity {

    private ActivityProfiloBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private BottomSheetDialog bottomSheetDialog, bottomSheetDialogUsername, bottomSheetDialogBiografia;

    private ProgressDialog progressDialog;

    private int RICHIESTA_IMMAGINE_GALLERIA=111;
    private Uri uriImmagine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_profilo);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);

        if(firebaseUser!=null){
            getInformazioni();
        }

        azioneClick();


    }

    private void azioneClick() {
        binding.bottoneScattaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetPickFoto();
            }
        });
        binding.editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetEditUsername();
            }
        });
        binding.bottoneLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogLogout();
            }
        });
        binding.editBiografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetEditBiografia();
            }
        });
    }

    private void showBottomSheetEditBiografia() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_biografia,null);

        view.findViewById(R.id.bottone_annulla_bio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialogBiografia.dismiss();
            }
        });

        EditText editTextBiografia=view.findViewById(R.id.edit_biografia);

        view.findViewById(R.id.bottone_salva_bio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextBiografia.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Il campo biografia non può essere vuoto", Toast.LENGTH_SHORT).show();
                }else{
                    aggiornaBiografia(editTextBiografia.getText().toString());
                    bottomSheetDialogBiografia.dismiss();
                }


            }
        });

        bottomSheetDialogBiografia=new BottomSheetDialog(this);
        bottomSheetDialogBiografia.setContentView(view);

        bottomSheetDialogBiografia.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialogBiografia=null;
            }
        });

        bottomSheetDialogBiografia.show();
    }

    private void aggiornaBiografia(String bio) {
        firestore.collection("Utenti").document(firebaseUser.getUid()).update("bio",bio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getInformazioni();
                        Toast.makeText(getApplicationContext(),"Aggiornamento biografia avvenuto con successo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDialogLogout() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfiloActivity.this);
        builder.setMessage("Sei sicuro di voler uscire dall'account?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfiloActivity.this, SplashScreenActivity.class));
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void showBottomSheetEditUsername() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_username,null);

        view.findViewById(R.id.bottone_annulla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialogUsername.dismiss();
            }
        });

        EditText editTextUsername=view.findViewById(R.id.edit_username);

        view.findViewById(R.id.bottone_salva).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(editTextUsername.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Il campo username non può essere vuoto", Toast.LENGTH_SHORT).show();
                }else{
                    aggiornaUsername(editTextUsername.getText().toString());
                    bottomSheetDialogUsername.dismiss();
                }


            }
        });

        bottomSheetDialogUsername=new BottomSheetDialog(this);
        bottomSheetDialogUsername.setContentView(view);

        bottomSheetDialogUsername.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialogUsername=null;
            }
        });

        bottomSheetDialogUsername.show();
    }

    private void aggiornaUsername(String username) {
            firestore.collection("Utenti").document(firebaseUser.getUid()).update("username",username)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    getInformazioni();
                    Toast.makeText(getApplicationContext(),"Aggiornamento username avvenuto con successo", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void showBottomSheetPickFoto() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        view.findViewById(R.id.pick_galleria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriGalleria();
                bottomSheetDialog.dismiss();
            }
        });
        view.findViewById(R.id.pick_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Camera non implementata", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();
    }



    private void apriGalleria() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"seleziona immagine"),RICHIESTA_IMMAGINE_GALLERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RICHIESTA_IMMAGINE_GALLERIA && resultCode== RESULT_OK && data != null && data.getData() != null){
            uriImmagine=data.getData();

            uploadImmagienInFirebase();
            //try {
            //    Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriImmagine);
            //    binding.profiloImmagine.setImageBitmap(bitmap);

            //}catch (Exception e){
            //    e.printStackTrace();
            //}
        }

    }

    private void uploadImmagienInFirebase() {
        if(uriImmagine!=null){
            progressDialog.setMessage("Inserimento immagine...");
            progressDialog.show();
            StorageReference mountainImagesRef = FirebaseStorage.getInstance().getReference().child("ImmaginiProfilo/"+
                    System.currentTimeMillis()+"."+getFileExtension(uriImmagine));
            mountainImagesRef.putFile(uriImmagine).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadUri=uriTask.getResult();

                    final String download_uri=String.valueOf(downloadUri);


                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("immagineProfilo",download_uri);

                    progressDialog.dismiss();
                    firestore.collection("Utenti").document(firebaseUser.getUid()).update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Inserimento effettuato",Toast.LENGTH_SHORT).show();
                            getInformazioni();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Inserimento fallito",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void getInformazioni() {
        firestore.collection("Utenti").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username= documentSnapshot.getString("username");
                binding.profiloUsername.setText(username);
                String numeroTelefono= documentSnapshot.getString("numeroTelefono");
                binding.profiloTelefono.setText(numeroTelefono);
                String bio= documentSnapshot.getString("bio");
                binding.profiloBio.setText(bio);
                String immagineProfilo= documentSnapshot.getString("immagineProfilo");

                if (immagineProfilo.equals("")){
                    Glide.with(ProfiloActivity.this).load(R.drawable.profilo_default).into(binding.profiloImmagine);
                }else{
                    Glide.with(ProfiloActivity.this).load(immagineProfilo).into(binding.profiloImmagine);
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
