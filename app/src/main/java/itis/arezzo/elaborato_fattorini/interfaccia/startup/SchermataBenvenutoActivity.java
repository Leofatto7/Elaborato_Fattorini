package itis.arezzo.elaborato_fattorini.interfaccia.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import itis.arezzo.elaborato_fattorini.interfaccia.MainActivity;
import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.interfaccia.autenticazione.LoginActivity;

public class SchermataBenvenutoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schermata_benvenuto);

        Button button= findViewById(R.id.bottone_continua);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SchermataBenvenutoActivity.this, LoginActivity.class));
            }
        });
    }
}