package fpt.life.finalproject.screen.register.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import fpt.life.finalproject.R;
import fpt.life.finalproject.service.LocationService;

public class RegisterActivity extends AppCompatActivity {

    private final static int PERMISSION_LOCATION = 1000;

    private LocationService locationService;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationService.showLocation();
            } else {
                Log.d("CheckLocation", "onRequestPermissionsResult: ");
            }
        }
    }
}