package itis.arezzo.elaborato_fattorini.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.interfaccia.chat.ChatActivity;
import itis.arezzo.elaborato_fattorini.modello.utente.Utenti;

public class ContattiAdapter extends RecyclerView.Adapter<ContattiAdapter.ViewHolder> {

    private List<Utenti> list;
    private Context context;

    public ContattiAdapter(List<Utenti> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_contatti_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Utenti utenti=list.get(position);

        holder.username.setText(utenti.getUsername());
        holder.descrizione.setText(utenti.getBio());
        holder.numero_telefono.setText(utenti.getNumeroTelefono());

        if (utenti.getImmagineProfilo().equals("")){
            Glide.with(context).load(R.drawable.profilo_default).into(holder.immagineProfilo);
        }else{
            Glide.with(context).load(utenti.getImmagineProfilo()).into(holder.immagineProfilo);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID",utenti.getUserID())
                        .putExtra("username",utenti.getUsername())
                        .putExtra("immagineProfilo",utenti.getImmagineProfilo()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView immagineProfilo;
        private TextView username,descrizione,numero_telefono;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            immagineProfilo=itemView.findViewById(R.id.contatti_immagine);
            username=itemView.findViewById(R.id.contatti_username);
            descrizione=itemView.findViewById(R.id.contatti_descrizione);
            numero_telefono=itemView.findViewById(R.id.numero_telefono);
        }
    }
}
