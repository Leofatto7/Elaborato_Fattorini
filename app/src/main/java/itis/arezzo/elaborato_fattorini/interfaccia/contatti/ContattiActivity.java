package itis.arezzo.elaborato_fattorini.interfaccia.contatti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.adapter.ContattiAdapter;
import itis.arezzo.elaborato_fattorini.databinding.ActivityContattiBinding;
import itis.arezzo.elaborato_fattorini.modello.utente.Utenti;

public class ContattiActivity extends AppCompatActivity {

    private static final String TAG = "ContattiActivity";
    private ActivityContattiBinding binding;
    private List<Utenti> list= new ArrayList<>();
    private ContattiAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    public static final int REQUEST_READ_CONTACTS = 79;
    private ListView listContatti;
    private ArrayList mobileArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_contatti);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if(firebaseUser!=null){
            getContattiDaRubrica();
            //getListaContatti();
        }

        if(mobileArray!=null){
            getListaContatti();
        }

        binding.bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getContattiDaRubrica() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllNumbers();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllNumbers();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllNumbers() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                //String name = cur.getString(cur.getColumnIndex(
                //        ContactsContract.Contacts.DISPLAY_NAME));
                //nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }

    private void getListaContatti() {
        firestore.collection("Utenti").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshots:queryDocumentSnapshots){
                    String userID= snapshots.getString("userID");
                    String username=snapshots.getString("username");
                    String imageUri= snapshots.getString("immagineProfilo");
                    String descrizione= snapshots.getString("bio");
                    String numeroTelefono= snapshots.getString("numeroTelefono");

                    Utenti utenti= new Utenti();
                    utenti.setUserID(userID);
                    utenti.setBio(descrizione);
                    utenti.setUsername(username);
                    utenti.setImmagineProfilo(imageUri);
                    utenti.setNumeroTelefono(numeroTelefono);

                    if(!userID.equals(firebaseUser.getUid()) && userID!=null){
                        if(mobileArray.contains(utenti.getNumeroTelefono())){
                            list.add(utenti);
                            Log.d(TAG, "getListaContatti: true "+utenti.getNumeroTelefono());
                        }else{
                            Log.d(TAG, "getListaContatti: false "+utenti.getNumeroTelefono());
                        }
                    }

                }

                //for(Utenti utente:list){
                //    if(mobileArray.contains(utente.getNumeroTelefono())){
                //        Log.d(TAG, "getListaContatti: true "+utente.getNumeroTelefono());
                //    }else{
                //        Log.d(TAG, "getListaContatti: false "+utente.getNumeroTelefono());
                //    }
                //}

                adapter=new ContattiAdapter(list,ContattiActivity.this);
                binding.recyclerView.setAdapter(adapter);
            }
        });


    }
}