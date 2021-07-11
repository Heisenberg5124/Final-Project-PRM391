package fpt.life.finalproject.screen.register.ui;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import fpt.life.finalproject.R;

public class RegisterActivity extends AppCompatActivity {

    private ImageView buttonBack;
    private ImageView buttonClose;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();
    }

    private void initComponents() {
        buttonBack = findViewById(R.id.image_view_register_back);
        buttonClose = findViewById(R.id.image_view_register_close);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container_register);

        buttonBack.setOnClickListener(v -> {
            NavController navController = navHostFragment.getNavController();
            navController.popBackStack();
        });
    }
}