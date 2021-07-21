package fpt.life.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import fpt.life.finalproject.service.OnChangeService;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

//    final String CHECK_ONLINE_STATUS_TASK = "CHECK_ONLINE_STATUS_TASK";
    private ImageView profileImageView;
    private ImageView matchedImageView;
    private ImageView logoImageView;
    private ImageView notifyCircleImageView;
    private User currentUser;
    private OnChangeService onChangeService;

    private MyProfileFragment myProfileFragment;
    private MatchedFragment matchedFragment;
    private HomepageFragment homepageFragment;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        onChangeService = new OnChangeService(this);
        onChangeService.listenOnlineUsersOnChange();
        onChangeService.listenMatchedUsersNotify();
        onChangeService.listenMatchedUsersIsKnown();
//        loadProgressDialog();
        getCurrentUserFromDatabase(FirebaseAuth.getInstance().getUid());
        profileImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, myProfileFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(253, 76, 103));
            matchedImageView.setColorFilter(Color.rgb(158, 158, 158));
        });

        matchedImageView.setOnClickListener(view -> {
            onChangeService.updateMatchedUserIsKnown();
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, matchedFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(158, 158, 158));
            matchedImageView.setColorFilter(Color.rgb(253, 76, 103));
        });

        logoImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, homepageFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(158, 158, 158));
            matchedImageView.setColorFilter(Color.rgb(158, 158, 158));
        });
    }



    private void findView() {
        profileImageView = findViewById(R.id.image_view_profile);
        matchedImageView = findViewById(R.id.image_view_matched);
        logoImageView = findViewById(R.id.image_view_logo);
        notifyCircleImageView = findViewById(R.id.image_view_notify_circle);
    }

    private void initFragment() {
        myProfileFragment = new MyProfileFragment();
        matchedFragment = new MatchedFragment();
        homepageFragment = new HomepageFragment();
        sendDataToHomePage();
        sendDataToMatched();
        sendDataToMyProfile();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.frame_layout_main_fragment, homepageFragment)
                .commit();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void getCurrentUserFromDatabase(String currentUserId) {

        db.collection("users").document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            currentUser = task.getResult().toObject(User.class);
                            initFragment();
                        }
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

    @Override
    protected void onResume() {
        onChangeService.upDateStatus(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        onChangeService.upDateStatus(false);
        super.onPause();
    }

    public void setNotifyCircleVisibility(boolean isNotify) {
        int visibility = isNotify ? View.VISIBLE : View.GONE;
        notifyCircleImageView.setVisibility(visibility);
    }
}