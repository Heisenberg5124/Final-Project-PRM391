package fpt.life.finalproject.screen.homepage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.firebase.auth.FirebaseAuth;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;

import at.markushi.ui.CircleButton;
import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HomePageCardStackAdapter;
import fpt.life.finalproject.adapter.InformationHomePageClickedListener;
import fpt.life.finalproject.dto.HomePageProfile;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.screen.viewOtherProfile.ViewOtherProfile_Activity;
import fpt.life.finalproject.service.LocationService;
import fpt.life.finalproject.service.OnChangeService;
import fpt.life.finalproject.service.OnUpdateLocationFirebaseListener;
import fpt.life.finalproject.service.SwipeService;

public class HomepageFragment extends Fragment implements InformationHomePageClickedListener, OnUpdateLocationFirebaseListener {

    private SwipeService swipeService;
    private OnChangeService onChangeService;

    private User currentUser;
    private CardStackLayoutManager cardManager;
    private HomePageCardStackAdapter cardAdapter;
    private CardStackView cardStackView;
    private CircleButton likeButton, nopeButton, refreshButton;
    private CountDownTimer countDownTimer;
    private ProgressDialog progressDialog;
    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_homepage, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        currentUser = ((MainActivity)getActivity()).getCurrentUser();
        swipeService = new SwipeService(currentUser);
        onChangeService = new OnChangeService();
        cardStackView = root.findViewById(R.id.homepage_view_card_stack);
        cardManager = new CardStackLayoutManager(this.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right) {
                    swipeService.swipeRight();
                    checkEmptyHomePageProfileList(swipeService.getHomePageProfileList());
                }
                if (direction == Direction.Left) {
                    swipeService.swipeLeft();
                    checkEmptyHomePageProfileList(swipeService.getHomePageProfileList());
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
        cardAdapter = new HomePageCardStackAdapter(swipeService.getHomePageProfileList(),this);
        cardStackView.setLayoutManager(cardManager);
        cardStackView.setAdapter(cardAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        //like button
        likeButton = root.findViewById(R.id.like_btn);
        likeButton.setOnClickListener(v -> autoLike());
        //nope button
        nopeButton = root.findViewById(R.id.nope_btn);
        nopeButton.setOnClickListener(v -> autoNope());
        //refresh button
        refreshButton = root.findViewById(R.id.refresh_btn);
        refreshButton.setOnClickListener(v -> {
            currentUser = ((MainActivity)getActivity()).getCurrentUser();
            swipeService.setCurrentUser(currentUser);
            loadingProfile();
        });

        loadingProfile();

    }

    private void checkEmptyHomePageProfileList(List<HomePageProfile> homePageProfileList) {
        LinearLayout refreshLayout = root.findViewById(R.id.layout_refresh);
        if (homePageProfileList.isEmpty()) {
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    private void loadingProfile() {
        LocationService locationService = new LocationService(getActivity(), FirebaseAuth.getInstance().getUid(),this);
        locationService.getLastKnownLocation();
//        loadProgressDialog();
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

    @Override
    public void onInformationHomePageClickListener(String otherUid) {
        Intent i = new Intent(getContext(), ViewOtherProfile_Activity.class);
        i.putExtra("otherUid",otherUid);
        startActivity(i);
    }

    @Override
    public void onCompleteUpdateLocation() {
        loadProgressDialog();
        swipeService.setUserList(null);
        //khoi tao countdown
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (swipeService.getUserList() == null) {
                    countDownTimer.start();
                } else {
                    swipeService.loadProfiles();
                    onChangeService.listenDataUsersOnChange(swipeService,cardAdapter);
                    checkEmptyHomePageProfileList(swipeService.getHomePageProfileList());
                    cardAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }
            }
        };
        swipeService.getAllDocument(countDownTimer);
    }
}