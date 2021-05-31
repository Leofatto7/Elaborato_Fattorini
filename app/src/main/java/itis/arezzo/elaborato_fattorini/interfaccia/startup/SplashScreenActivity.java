package itis.arezzo.elaborato_fattorini.interfaccia.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.interfaccia.MainActivity;
import itis.arezzo.elaborato_fattorini.interfaccia.autenticazione.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
            },3000);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, SchermataBenvenutoActivity.class));
                    finish();
                }
            },3000);
        }

    }
}