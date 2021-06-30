package fpt.life.finalproject.screen.homepage;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.os.CountDownTimer;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HomePageCardStackAdapter;
import fpt.life.finalproject.dto.HomePageProfile;
import fpt.life.finalproject.dto.Profile;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.service.SwipeService;
import lombok.val;

public class HomepageFragment extends Fragment {

    private Map<String, User> userList;
    private List<HomePageProfile> homePageProfileList = new ArrayList<>();
    private User currentUser;
    private SwipeService swipeService;

    private Button refreshButton;
    private CardStackLayoutManager cardManager;
    private HomePageCardStackAdapter cardAdapter;
    private CardStackView cardStackView;
    private CircleButton likeButton, nopeButton;
    private CountDownTimer countDownTimer;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_homepage, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        swipeService = new SwipeService();
        cardStackView = root.findViewById(R.id.homepage_view_card_stack);
        cardManager = new CardStackLayoutManager(this.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right) {
                    swipeRight();
                    checkEmptyHomePageProfileList(root);
                }
                if (direction == Direction.Left) {
                    swipeLeft();
                    checkEmptyHomePageProfileList(root);
                    Log.d("size after swipe", homePageProfileList.size() + "");
                }
                //TODO: Vi khong can turn back lai card nen se xoa luon moi lan swipe
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {
                //Log.d("sw", view.toString());
            }
        });
        cardManager.setStackFrom(StackFrom.None);//card chong len nhau khong thay card sau
//        cardManager.setVisibleCount(3);//thay duoc bao nhieu card
//        cardManager.setTranslationInterval(8.0f);//do ho doc cua card sau
//        cardManager.setScaleInterval(0.95f);//do ho ngang cua card sau
        cardManager.setSwipeThreshold(0.5f);//khoang trong de swipe
        cardManager.setMaxDegree(20.0f);//do nghieng
        cardManager.setDirections(Direction.HORIZONTAL);
        cardManager.setCanScrollHorizontal(true);
        cardManager.setCanScrollVertical(false);
        cardAdapter = new HomePageCardStackAdapter(homePageProfileList);
        cardStackView.setLayoutManager(cardManager);
        cardStackView.setAdapter(cardAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        //like button
        likeButton = root.findViewById(R.id.like_btn);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoLike();
            }
        });
        //nope button
        nopeButton = root.findViewById(R.id.nope_btn);
        nopeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoNope();
            }
        });
        //refresh button
        refreshButton = root.findViewById(R.id.refresh_btn);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProgressDialog();
                loadingProfile(root);
            }
        });

        loadProgressDialog();
        loadingProfile(root);

    }

    private void checkEmptyHomePageProfileList(View root) {
        LinearLayout refreshLayout = root.findViewById(R.id.layout_refresh);
        if (homePageProfileList.isEmpty()) {
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    private void loadingProfile(View root) {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (userList == null) {
                    progressDialog.dismiss();
                    Log.d("pro1", "timeout");
                } else {
                    currentUser = getCurrentUser("1YyOVbEZ9nbclrT9iX5GIRTCboA3");
                    homePageProfileList.clear();
                    homePageProfileList.addAll(filterProfiles(userList, currentUser));
                    checkEmptyHomePageProfileList(root);
                    Log.d("locloc2", homePageProfileList.toString());
                    cardAdapter.notifyDataSetChanged();
                    moreTimeToDismissDialog(progressDialog, 1000);
                }
            }
        }.start();
        getAllDocument(countDownTimer);
    }

    private void moreTimeToDismissDialog(ProgressDialog progressDialog, int time) {
        CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }
        }.start();
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private User getCurrentUser(String uid) {
        return userList.get(uid);
    }

    private void getAllDocument(CountDownTimer countDownTimer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, User> users = new HashMap<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uid = document.getId();
                                User user = document.toObject(User.class);
                                users.put(uid, user);
                            }
                            userList = users; //TODO: co the bi bug
                            countDownTimer.onFinish();
                            countDownTimer.cancel();
                        } else {
                            Log.d("getAll", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    private List<HomePageProfile> filterProfiles(Map<String, User> userList, User currentUser) {
        List<HomePageProfile> homePageProfileList = new ArrayList<>();
        for (Map.Entry<String, User> entry : userList.entrySet()) {
            if (checkValidUser(currentUser, entry.getValue())) {
                String uid = entry.getValue().getUid();
                String image = entry.getValue().getPhotoUrls().get(0);
                String name = entry.getValue().getName();
                Calendar anotherUserBirthday = Calendar.getInstance();
                anotherUserBirthday.setTime(entry.getValue().getBirthday());
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int age = currentYear - anotherUserBirthday.get(Calendar.YEAR);
                String city = entry.getValue().getCity();
                double distance = getDistance(currentUser.getLocation(), entry.getValue().getLocation());
                HomePageProfile homePageProfile = HomePageProfile.builder()
                        .uid(uid)
                        .image(image)
                        .name(name)
                        .age(age)
                        .city(city)
                        .distance(distance)
                        .build();
                homePageProfileList.add(homePageProfile);
            }
        }
        return homePageProfileList;
    }

    private boolean checkValidUser(User currentUser, User anotherUser) {
        //check trung id voi current
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentUser.getUid().equals(anotherUser.getUid())) {
            return false;
        }
        //check show me gender
        if (!currentUser.getShowMeGender().equals("Everyone")) {
            if (!currentUser.getShowMeGender().equals(anotherUser.getGender())) {
                return false;
            }
        }
        Calendar anotherUserBirthday = Calendar.getInstance();
        anotherUserBirthday.setTime(anotherUser.getBirthday());
        int anotherUserYear = anotherUserBirthday.get(Calendar.YEAR);
        int anotherUserAge = currentYear - anotherUserYear;
        //check range age
        if (anotherUserAge < currentUser.getRangeAge().get("min")
                || currentUser.getRangeAge().get("max") < anotherUserAge) {
            return false;
        }
        //check range location
        if (currentUser.getRangeDistance() < getDistance(currentUser.getLocation(), anotherUser.getLocation())) {
            return false;
        }
        //check in liked list
        if (currentUser.getUserLiked().contains(anotherUser.getUid())) {
            Log.d("bug", "5");
            return false;
        }
        //check in disliked list
        if (currentUser.getUserDisliked().contains(anotherUser.getUid())) {
            Log.d("bug", "6");
            return false;
        }
        return true;
    }

    private double getDistance(GeoPoint gp1, GeoPoint gp2) {
        Location loc1 = new Location("");
        loc1.setLatitude(gp1.getLatitude());
        loc1.setLongitude(gp1.getLongitude());

        Location loc2 = new Location("");
        loc2.setLatitude(gp2.getLatitude());
        loc2.setLongitude(gp2.getLongitude());

        return Math.round(loc1.distanceTo(loc2) / 100) / 10.0;
    }

    private void autoLike() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(600)
//                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
        Log.d("likebtn", "autoLike: ok");
    }

    private void autoNope() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(600)
//                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
        Log.d("Nopebtn", "autoNope: ok");
    }

    private void swipeRight() {
        currentUser.getUserLiked().add(homePageProfileList.get(0).getUid());
        updateOnSwipe("userLiked", currentUser.getUserLiked());
        homePageProfileList.remove(0);
        cardAdapter.notifyItemRemoved(0);
    }

    private void swipeLeft() {
        currentUser.getUserDisliked().add(homePageProfileList.get(0).getUid());
        updateOnSwipe("userDisliked", currentUser.getUserDisliked());
        homePageProfileList.remove(0);
        cardAdapter.notifyItemRemoved(0);
    }

    private void updateOnSwipe(String field, List<String> list) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference currentUserRef = db.collection("users").document(currentUser.getUid());
        currentUserRef.update(field, list);
    }

    private List<HomePageProfile> addList() {
        List<HomePageProfile> items = new ArrayList<>();
        items.add(new HomePageProfile("1", "https://scontent.fpnh22-2.fna.fbcdn.net/v/t1.6435-9/138221351_2809180635989149_4537883330565810986_n.jpg?_nc_cat=103&ccb=1-3&_nc_sid=8bfeb9&_nc_ohc=2mLPYnVgy0IAX-F92Jq&_nc_ht=scontent.fpnh22-2.fna&oh=1e00a8cac94f8217fd1a4ae235151af0&oe=60D7E3E6", "Huy", 22, "hoho", 5));
        items.add(new HomePageProfile("2", "https://scontent.fpnh22-1.fna.fbcdn.net/v/t31.18172-8/20933937_914475132035316_3232000478751406141_o.jpg?_nc_cat=107&ccb=1-3&_nc_sid=174925&_nc_ohc=_78c985Ntk8AX_VYzAF&_nc_ht=scontent.fpnh22-1.fna&oh=459e7a7406376693f1a2f637da941507&oe=60F0D43D", "Vinh cam cup", 20, "Danang", 10));
        items.add(new HomePageProfile("3", "https://scontent.fpnh22-3.fna.fbcdn.net/v/t1.6435-9/39572479_1151001905049303_5098586510651817984_n.jpg?_nc_cat=105&ccb=1-3&_nc_sid=174925&_nc_ohc=U9hJXo1dArsAX_va8rS&_nc_ht=scontent.fpnh22-3.fna&oh=7b8139639140136e9cae01ca48f56c5e&oe=60D75061", "Van la Vinh", 27, "Danang", 9));
        return items;
    }

}