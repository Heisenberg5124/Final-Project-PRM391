package fpt.life.finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
        onChangeService.upDateStatus(true);
//        loadProgressDialog();
//        getCurrentUserFromDatabase(FirebaseAuth.getInstance().getUid());
//        loadProgressDialog();
//        getCurrentUser(FirebaseAuth.getInstance().getUid());
//        getCurrentUserFromDatabase("1YyOVbEZ9nbclrT9iX5GIRTCboA3");

        profileImageView.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frame_layout_main_fragment, myProfileFragment)
                    .commit();
            profileImageView.setColorFilter(Color.rgb(253, 76, 103));
            matchedImageView.setColorFilter(Color.rgb(158, 158, 158));
        });

        matchedImageView.setOnClickListener(view -> {
            sendDataToMatched();
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

        db.collection("users").document(currentUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        currentUser = value.toObject(User.class);
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
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

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