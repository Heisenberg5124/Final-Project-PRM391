package fpt.life.finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import fpt.life.finalproject.dto.MyProfile;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.screen.homepage.HomepageFragment;
import fpt.life.finalproject.screen.matched.MatchedFragment;
import fpt.life.finalproject.screen.myprofile.MyProfileFragment;
import fpt.life.finalproject.service.LocationService;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSION_LOCATION = 1000;

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
//        getCurrentUser(FirebaseAuth.getInstance().getUid());
        getCurrentUser("1YyOVbEZ9nbclrT9iX5GIRTCboA3");

        profileImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, myProfileFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(253, 76, 103));
            matchedImageView.setColorFilter(Color.rgb(87, 87, 87));
        });

        matchedImageView.setOnClickListener(view -> {
            sendDataToMatched();
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
        sendDataToMyProfile();
//        locationService = new LocationService(getApplicationContext(), FirebaseAuth.getInstance().getUid());
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
    public void setCurrentUserName(String currentUserName){
        currentUser.setName(currentUserName);
    }
    private void sendDataToHomePage(){
        Bundle bundle = new Bundle();
        bundle.putString("currentUserId",currentUser.getUid());
        homepageFragment.setArguments(bundle);
    }

    private void sendDataToMatched(){
        Bundle bundle = new Bundle();
        bundle.putString("currentUserId",currentUser.getUid());
        bundle.putString("currentUserName",currentUser.getName());
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
                .listImage(currentUser.getPhotoUrls())
                .rangeAge(currentUser.getRangeAge())
                .rangeDistance(currentUser.getRangeDistance())
                .build();
        bundle.putParcelable("myProfile", myProfile);
        myProfileFragment.setArguments(bundle);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}