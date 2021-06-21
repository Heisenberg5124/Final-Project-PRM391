package fpt.life.finalproject.screen.homepage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HomePageCardStackAdapter;
import fpt.life.finalproject.dto.HomePageProfile;
import lombok.val;

public class HomepageFragment extends Fragment {

    private CardStackLayoutManager cardManager;
    private HomePageCardStackAdapter cardAdapter;
    private CardStackView cardStackView;
    private CircleButton likeButton,nopeButton;

    public HomepageFragment() {
        // Required empty public constructor
    }

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
        cardStackView = root.findViewById(R.id.homepage_view_card_stack);
        cardManager = new CardStackLayoutManager(this.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {

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
//        cardManager.setSwipeableMethod(SwipeableMethod.Manual);
//        cardManager.setOverlayInterpolator(new LinearInterpolator());
        cardAdapter = new HomePageCardStackAdapter(addList());
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

    }

    private void autoLike() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(700)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
        Log.d("likebtn", "autoLike: ok");
    }

    private void autoNope() {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(700)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        cardManager.setSwipeAnimationSetting(setting);
        cardStackView.swipe();
        Log.d("Nopebtn", "autoNope: ok");
    }

    private List<HomePageProfile> addList() {
        List<HomePageProfile> items = new ArrayList<>();
        items.add(new HomePageProfile("https://scontent.fpnh22-2.fna.fbcdn.net/v/t1.6435-9/154925008_2843796882527524_309424269142691995_n.jpg?_nc_cat=103&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=ObggujyRlAQAX_PmlV6&tn=Z_QSGcG2HB3iYDgM&_nc_ht=scontent.fpnh22-2.fna&oh=931eeee2ca1ed5bdafed6a72d27fa48b&oe=60CF0AF5","Huy",22,"hoho"));
        items.add(new HomePageProfile("https://scontent.fpnh22-1.fna.fbcdn.net/v/t31.18172-8/20933937_914475132035316_3232000478751406141_o.jpg?_nc_cat=107&ccb=1-3&_nc_sid=174925&_nc_ohc=_78c985Ntk8AX_VYzAF&_nc_ht=scontent.fpnh22-1.fna&oh=459e7a7406376693f1a2f637da941507&oe=60F0D43D", "Vinh cam cup", 20, "Danang"));
        items.add(new HomePageProfile("https://scontent.fpnh22-3.fna.fbcdn.net/v/t1.6435-9/39572479_1151001905049303_5098586510651817984_n.jpg?_nc_cat=105&ccb=1-3&_nc_sid=174925&_nc_ohc=urrI_oGoWWgAX8zDjxA&_nc_ht=scontent.fpnh22-3.fna&oh=74d692d7cb47e3d2c9c7c7867017bb41&oe=60CF6761", "Van la Vinh", 27, "Danang"));
        return items;
    }

}