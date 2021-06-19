package fpt.life.finalproject.screen.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fpt.life.finalproject.R;

public class Profile_FB_Activity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private TextView name, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fb);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        if (user != null) {

            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                openLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {

            openLogin();
        }
    }
    private void openLogin() {
        startActivity(new Intent(this,Login_FB_Activity.class));
        finish();
    }
}