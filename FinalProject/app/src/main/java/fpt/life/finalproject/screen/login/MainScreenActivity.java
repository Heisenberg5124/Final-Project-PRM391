package fpt.life.finalproject.screen.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fpt.life.finalproject.R;

public class MainScreenActivity extends AppCompatActivity {
    private Button LoginGG,LoginFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        LoginGG = (Button) findViewById(R.id.btnGG);
        LoginFB = (Button) findViewById(R.id.btnFB);

        LoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreenActivity.this,Login_GG_Activity.class);
                startActivity(intent);
                return;
            }
        });
        LoginFB.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                Intent intent = new Intent(MainScreenActivity.this,Login_FB_Activity.class);
               startActivity(intent);
                return;
            }
        });
    }
}