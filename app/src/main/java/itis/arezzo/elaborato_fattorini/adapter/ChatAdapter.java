package itis.arezzo.elaborato_fattorini.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.modello.chat.Chat;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> list;
    private Context context;
    public static final int MSG_TIPO_SINISTRA=0;
    public static final int MSG_TIPO_DESTRA=1;
    private FirebaseUser firebaseUser;

    public ChatAdapter(List<Chat> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TIPO_SINISTRA){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_messaggio_sinistra,parent,false);
            return new ViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.chat_messaggio_destra,parent,false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messaggio;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            messaggio= itemView.findViewById(R.id.text_messaggio);
        }
        void bind(Chat chat){
            messaggio.setText(chat.getMessaggio());
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getMittente().equals(firebaseUser.getUid())){
            return MSG_TIPO_DESTRA;
        }else{
            return MSG_TIPO_SINISTRA;
        }
    }
}
