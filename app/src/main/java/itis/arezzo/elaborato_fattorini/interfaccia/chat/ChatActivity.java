package itis.arezzo.elaborato_fattorini.interfaccia.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.adapter.ChatAdapter;
import itis.arezzo.elaborato_fattorini.databinding.ActivityChatBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.impostazioni.ImpostazioniActivity;
import itis.arezzo.elaborato_fattorini.modello.chat.Chat;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String idDestinatario;
    private ChatAdapter adapter;
    private List<Chat> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chat);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();

        Intent intent=getIntent();
        String username= intent.getStringExtra("username");
        idDestinatario= intent.getStringExtra("userID");
        String immagineProfilo= intent.getStringExtra("immagineProfilo");


        if(idDestinatario!=null){
            binding.chatUsername.setText(username);
            if (immagineProfilo.equals("")){
                Glide.with(this).load(R.drawable.profilo_default).into(binding.chatImmagine);
            }else{
                Glide.with(this).load(immagineProfilo).into(binding.chatImmagine);
            }

        }

        binding.bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.chatMessaggio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inizializzaBottoneInvio();

        list= new ArrayList<>();
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        leggiChat();
    }

    private void leggiChat(){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    list.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Chat chat= snapshot1.getValue(Chat.class);
                        if (chat!=null && chat.getMittente().equals(firebaseUser.getUid()) && chat.getDestinatario().equals(idDestinatario) ||
                                chat.getMittente().equals(idDestinatario) && chat.getDestinatario().equals(firebaseUser.getUid())){
                            list.add(chat);
                        }

                    }
                    if(adapter!=null){
                        binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount()-1);
                        adapter.notifyDataSetChanged();
                    }else{
                        adapter= new ChatAdapter(list,ChatActivity.this);
                        binding.recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void inizializzaBottoneInvio(){
        binding.chatBottoneInvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(binding.chatMessaggio.getText().toString())){
                    inviaMessaggio(binding.chatMessaggio.getText().toString());

                    binding.chatMessaggio.setText("");
                }
            }
        });

        binding.bottoneIndietro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void inviaMessaggio(String messaggio) {
        try{

            Date data = Calendar.getInstance().getTime();
            SimpleDateFormat format= new SimpleDateFormat("dd-MM-yyyy");
            String dataOggi = format.format(data);

            Calendar currentDateTime = Calendar.getInstance();
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
            String oraCorrente = format2.format(currentDateTime.getTime());

            Chat chat= new Chat(
                    dataOggi+", "+oraCorrente,
                    messaggio,
                    "TEXT",
                    firebaseUser.getUid(),
                    idDestinatario);

            reference.child("Chat").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("Send", "onSuccess: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.d("Send", "onFailure: "+e.getMessage());
                }
            });

            //Aggiungi alla ListaChat
            DatabaseReference chatRef1= FirebaseDatabase.getInstance().getReference("ListaChat").child(firebaseUser.getUid()).child(idDestinatario);
            chatRef1.child("idChat").setValue(idDestinatario);

            DatabaseReference chatRef2= FirebaseDatabase.getInstance().getReference("ListaChat").child(idDestinatario).child(firebaseUser.getUid());
            chatRef2.child("idChat").setValue(firebaseUser.getUid());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}