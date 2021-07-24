package fpt.life.finalproject.screen.matched;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.ChatAdapter;
import fpt.life.finalproject.adapter.MatchedAdapter;
import fpt.life.finalproject.adapter.OnInforAnotherUserChange;
import fpt.life.finalproject.adapter.ProfileMatchAdapter;
import fpt.life.finalproject.dto.MatchedProfile;
import fpt.life.finalproject.dto.Profile;
import fpt.life.finalproject.model.Hobby;
import fpt.life.finalproject.screen.chat.ChatActivity;
import fpt.life.finalproject.screen.chat.ChatActivity;
import fpt.life.finalproject.service.MatchedService;

import static android.app.Activity.RESULT_OK;

public class MatchedFragment extends Fragment implements MatchedAdapter.OnItemListener, ChatAdapter.OnItemListener ,ProfileMatchAdapter.OnItemListener{
    private ArrayList<MatchedProfile> profileList = new ArrayList<>();
    private ArrayList<MatchedProfile> profileChattedList = new ArrayList<>();
    private ArrayList<MatchedProfile> profileMatchedList = new ArrayList<>();
    private MatchedService matchedService;
    private RecyclerView rvMatched;
    private MatchedAdapter matchedAdapter;
    private ChatAdapter chatAdapter;
    private ProfileMatchAdapter profileMatchAdapter;
    private RecyclerView rvChatted;
    private String uid;
    private String name;
    private View rootView;
    private SearchView searchProfile;

    private TextView txtTitleChat;
    private TextView txtTitleMatch;
    private TextView txtNoProfile;

    public MatchedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_matched, container, false);
        uid = getArguments().getString("currentUserId");
        name = getArguments().getString("currentUserName");
        matchedService = new MatchedService(uid, name);
        init();
        getData();
        searchProfileOnclick();
        return rootView;
    }

    public void init() {
        profileChattedList.clear();
        profileMatchedList.clear();
        searchProfile = rootView.findViewById(R.id.search_view_my_profile);
        //Match RecyclerView
        rvMatched = rootView.findViewById(R.id.RecyclerView_Matched_Matched);
        LinearLayoutManager layoutManagerMatched = new LinearLayoutManager(getActivity());
        layoutManagerMatched.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvMatched.setLayoutManager(layoutManagerMatched);
        matchedAdapter = new MatchedAdapter(profileMatchedList, this);
        rvMatched.setAdapter(matchedAdapter);
        //Chat RecyclerView
        rvChatted = (RecyclerView) rootView.findViewById(R.id.RecyclerView_Matched_Chat);
        LinearLayoutManager layoutManagerChatted = new LinearLayoutManager(getActivity());
        rvChatted.setLayoutManager(layoutManagerChatted);
        chatAdapter = new ChatAdapter(profileChattedList, this, name);
        rvChatted.setAdapter(chatAdapter);
        //Profile Adapter
        profileMatchAdapter = new ProfileMatchAdapter(profileList,this);
        //Title
        txtTitleChat = rootView.findViewById(R.id.txt_title_chat);
        txtTitleMatch = rootView.findViewById(R.id.txt_title_match);
        txtNoProfile = rootView.findViewById(R.id.txt_no_profile);

    }

    private void searchProfileOnclick(){
        searchProfile.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    rvMatched.setAdapter(matchedAdapter);
                    rvChatted.setAdapter(chatAdapter);
                    txtTitleChat.setVisibility(View.VISIBLE);
                    txtTitleMatch.setVisibility(View.VISIBLE);
                }else{
                    txtTitleChat.setVisibility(View.GONE);
                    txtTitleMatch.setVisibility(View.GONE);
                    rvMatched.setAdapter(null);
                    if (newText.length()==1){
                        profileList.clear();
                        profileList.addAll(profileMatchedList);
                        profileList.addAll(profileChattedList);
                        rvChatted.setAdapter(profileMatchAdapter);
                    }
                    List<MatchedProfile> filteredProfileList = filterProfile(newText);
                    profileMatchAdapter.filterProfileList((ArrayList<MatchedProfile>) filteredProfileList);
                }
                return false;
            }
        });
    }

    private List<MatchedProfile> filterProfile(String text) {
        String lowerCaseQuery = text.toLowerCase();
        List<MatchedProfile> filteredProfileList = new ArrayList<>();
        for (MatchedProfile profile : profileList) {
            String hobbyText = profile.getOtherUserName().toLowerCase();
            if (hobbyText.contains(lowerCaseQuery))
                filteredProfileList.add(profile);
        }
        return filteredProfileList;
    }
    @Override
    public void onItemClick(String otherUid) {
        Intent intent = new Intent((MainActivity)getActivity(), ChatActivity.class);
        intent.putExtra("currentUid", uid);
        intent.putExtra("otherUid", otherUid);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void getData() {
        OnInforAnotherUserChange onInforAnotherUserChange = new OnInforAnotherUserChange() {
            @Override
            public void onNewMatchedProfile(MatchedProfile matchedProfile) {
                profileMatchedList.add(matchedProfile);
                txtTitleMatch.setVisibility(TextView.VISIBLE);
                txtNoProfile.setVisibility(View.GONE);
                matchedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNameChange(int pos, String newName, String typeUser) {
                if (typeUser.equals("Chatted")) {
                    profileChattedList.get(pos).setOtherUserName(newName);
                    chatAdapter.notifyItemChanged(pos);
                } else {
                    profileMatchedList.get(pos).setOtherUserName(newName);
                    matchedAdapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onIsOnlineChange(int pos, Boolean isOnline, String typeUser) {
                if (typeUser.equals("Chatted")) {
                    profileChattedList.get(pos).setOnlineStatus(isOnline);
                    chatAdapter.notifyItemChanged(pos);
                } else {
                    profileMatchedList.get(pos).setOnlineStatus(isOnline);
                    matchedAdapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onMessageChange(int pos, MatchedProfile matchedProfile) {
                profileChattedList.remove(pos);
                profileChattedList.add(0, matchedProfile);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNewChattedProfile(int posInMatched, MatchedProfile matchedProfile) {
                Log.d("CheckSize",profileMatchedList.size()+ " ");
                if (posInMatched != -1) {
                    profileMatchedList.remove(posInMatched);
                    matchedAdapter.notifyDataSetChanged();
                    if (profileMatchedList.size() == 0){
                        txtTitleMatch.setVisibility(TextView.GONE);
                    }
                }
                profileChattedList.add(0, matchedProfile);
                txtTitleChat.setVisibility(TextView.VISIBLE);
                txtNoProfile.setVisibility(View.GONE);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void finishLoad() {
                Log.d("CheckSize","Loaded");
                if (profileMatchedList.size() == 0 && profileChattedList.size() == 0){
                    txtNoProfile.setVisibility(View.VISIBLE);
                }
                if (profileMatchedList.size() == 0){
                    txtTitleMatch.setVisibility(TextView.GONE);
                }
                if (profileChattedList.size() == 0){
                    txtTitleChat.setVisibility(TextView.GONE);
                }
            }
        };
        matchedService.snapshotMatchUser(onInforAnotherUserChange);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                Log.d("Intent", "onActivityResult: " + data.getSerializableExtra("unmatchedUid"));
                String unmatchedUid = (String) data.getSerializableExtra("unmatchedUid");
                int posChat = matchedService.checkExistProfile(unmatchedUid,profileChattedList);
                int posMatch = matchedService.checkExistProfile(unmatchedUid,profileMatchedList);
                if (posChat != -1){
                    profileChattedList.remove(posChat);
                    matchedService.unmatch("Chat",posChat);
                    chatAdapter.notifyDataSetChanged();
                }else {
                    profileMatchedList.remove(posMatch);
                    matchedService.unmatch("Match",posMatch);
                    matchedAdapter.notifyDataSetChanged();
                }
            }

    }
}
