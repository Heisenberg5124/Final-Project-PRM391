package fpt.life.finalproject;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import fpt.life.finalproject.dto.MyProfile;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.screen.homepage.HomepageFragment;
import fpt.life.finalproject.screen.matched.MatchedFragment;
import fpt.life.finalproject.screen.myprofile.MyProfileFragment;
import fpt.life.finalproject.service.LocationService;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private LocationService locationService;
    private ImageView profileImageView;
    private ImageView matchedImageView;
    private ImageView logoImageView;
    private User currentUser;

    private MyProfileFragment myProfileFragment;
    private MatchedFragment matchedFragment;
    private HomepageFragment homepageFragment;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        loadProgressDialog();
        getCurrentUser(FirebaseAuth.getInstance().getUid());

        profileImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, myProfileFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(253, 76, 103));
            matchedImageView.setColorFilter(Color.rgb(87, 87, 87));
        });

        matchedImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, matchedFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(87, 87, 87));
            matchedImageView.setColorFilter(Color.rgb(253, 76, 103));
        });

        logoImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, homepageFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(87, 87, 87));
            matchedImageView.setColorFilter(Color.rgb(87, 87, 87));
        });
    }

    private void findView() {
        profileImageView = findViewById(R.id.image_view_profile);
        matchedImageView = findViewById(R.id.image_view_matched);
        logoImageView = findViewById(R.id.image_view_logo);
    }

    private void initFragment() {
        myProfileFragment = new MyProfileFragment();
        matchedFragment = new MatchedFragment();
        homepageFragment = new HomepageFragment();
        sendDataToHomePage();
        sendDataToMatched();
        sendDataToMyProfile();
        locationService = new LocationService(getApplicationContext(), FirebaseAuth.getInstance().getUid());
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.frame_layout_main_fragment, homepageFragment)
                .commit();
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void getCurrentUser(String currentUserId) {

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        currentUser = task.getResult().toObject(User.class);
                        progressDialog.dismiss();

                        initFragment();
                    }
                });
    }

    private void sendDataToHomePage(){
        Bundle bundle = new Bundle();
        bundle.putString("currentUserId",currentUser.getUid());
        homepageFragment.setArguments(bundle);
    }

    private void sendDataToMatched(){
        Bundle bundle = new Bundle();
        bundle.putString("currentUserId",currentUser.getUid());
        matchedFragment.setArguments(bundle);
    }

    private void sendDataToMyProfile(){
        Bundle bundle = new Bundle();
        MyProfile myProfile = MyProfile.builder()
                .uid(currentUser.getUid())
                .name(currentUser.getName())
                .birthday(currentUser.getBirthday())
                .gender(currentUser.getGender())
                .hobbies(currentUser.getHobbies())
                .showMeGender(currentUser.getShowMeGender())
                .avt(currentUser.getPhotoUrls().get(0))
                .build();
        bundle.putParcelable("myProfile", myProfile);
        myProfileFragment.setArguments(bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}