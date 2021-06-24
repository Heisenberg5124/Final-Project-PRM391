package fpt.life.finalproject.screen.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import fpt.life.finalproject.R;

public class Profile_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView username = findViewById(R.id.username);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser()!=null){
            if(auth.getCurrentUser().getPhoneNumber()!=null){
                phone.setText("Phone: "+auth.getCurrentUser().getPhoneNumber());
            }
            if(auth.getCurrentUser().getDisplayName()!=null){
                username.setText("Username: "+auth.getCurrentUser().getDisplayName());
            }
            if(auth.getCurrentUser().getEmail()!=null){
                email.setText("Email: "+auth.getCurrentUser().getEmail());
            }
        }
        Button logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(v -> signOut());
    }
    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Profile_Activity.this,Login_Activity.class));
                    finish();
                });
        // [END auth_fui_signout]
    }
}