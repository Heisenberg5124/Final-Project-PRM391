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
import android.widget.ImageView;
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

//    private Map<String, User> userList;
//    private List<HomePageProfile> homePageProfileList = new ArrayList<>();
//    private User currentUser;
    private SwipeService swipeService;

    private Button refreshButton;
    private ImageView infoButton;
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
                    swipeService.swipeRight();
                    checkEmptyHomePageProfileList(root,swipeService.getHomePageProfileList());
                }
                if (direction == Direction.Left) {
                    swipeService.swipeLeft();
                    checkEmptyHomePageProfileList(root,swipeService.getHomePageProfileList());
                }
                cardAdapter.notifyItemRemoved(0);
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
        cardManager.setSwipeThreshold(0.5f);//khoang trong de swipe
        cardManager.setMaxDegree(20.0f);//do nghieng
        cardManager.setDirections(Direction.HORIZONTAL);
        cardManager.setCanScrollHorizontal(true);
        cardManager.setCanScrollVertical(false);
        cardAdapter = new HomePageCardStackAdapter(swipeService.getHomePageProfileList());
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
                loadingProfile(root);
            }
        });

        loadingProfile(root);

    }

    private void checkEmptyHomePageProfileList(View root, List<HomePageProfile> homePageProfileList) {
        LinearLayout refreshLayout = root.findViewById(R.id.layout_refresh);
        if (homePageProfileList.isEmpty()) {
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    private void loadingProfile(View root) {
        loadProgressDialog();
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (swipeService.getUserList() == null) {
                    progressDialog.dismiss();
                    Log.d("pro1", "timeout");
                } else {
                    swipeService.loadProfiles();

                    checkEmptyHomePageProfileList(root, swipeService.getHomePageProfileList());
                    cardAdapter.notifyDataSetChanged();
                    moreTimeToDismissDialog(progressDialog, 1000);
                }
            }
        }.start();
        swipeService.getAllDocument(countDownTimer);
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

    private void autoLike() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(600)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

    private void autoNope() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(600)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
    }

}