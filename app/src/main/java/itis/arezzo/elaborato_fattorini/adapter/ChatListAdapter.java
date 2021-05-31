package itis.arezzo.elaborato_fattorini.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.interfaccia.chat.ChatActivity;
import itis.arezzo.elaborato_fattorini.modello.ChatList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ChatList chatList = list.get(position);

        holder.username.setText(chatList.getUsername());
        holder.descrizione.setText(chatList.getDescrizione());
        holder.data.setText(chatList.getData());

        //per l'immagine serve la libreria
        if (chatList.getUrlProfile().equals("")){
            holder.profilo.setImageResource(R.drawable.profilo_default);
        }else{
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.profilo);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID",chatList.getUserID())
                        .putExtra("username",chatList.getUsername())
                        .putExtra("immagineProfilo",chatList.getUrlProfile()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView username, descrizione, data;
        private CircularImageView profilo;

        public Holder(@NonNull View itemView) {
            super(itemView);
            data=itemView.findViewById(R.id.data);
            username=itemView.findViewById(R.id.username);
            descrizione=itemView.findViewById(R.id.descrizione);
            profilo=itemView.findViewById(R.id.profilo);
        }
    }
}
