package fpt.life.finalproject.screen.matched;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.dto.MatchedProfile;

public class MatchedFragment extends Fragment implements MatchedAdapter.OnItemListener,ChatAdapter.OnItemListener{
    ArrayList<MatchedProfile> profileList = new ArrayList<>();
    ArrayList<MatchedProfile> profileChattedList = new ArrayList<>();
    ArrayList<MatchedProfile> profileMatchedList = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.fragment_matched, container, false);

        init(rootView);
        return rootView;
    }

        public void init(View root){
            profileChattedList.clear();
            profileMatchedList.clear();
            profileList.clear();
            setMockData();
            //Match RecyclerView
            RecyclerView rvMatched = (RecyclerView) root.findViewById(R.id.RecyclerView_Matched_Matched);
            LinearLayoutManager layoutManagerMatched = new LinearLayoutManager(getActivity());
            layoutManagerMatched.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvMatched.setLayoutManager(layoutManagerMatched);
            MatchedAdapter matchedAdapter =  new MatchedAdapter(profileMatchedList,this);
            rvMatched.setAdapter(matchedAdapter);
            //Chat RecyclerView
            RecyclerView rvChatted = (RecyclerView) root.findViewById(R.id.RecyclerView_Matched_Chat);
            LinearLayoutManager layoutManagerChatted = new LinearLayoutManager(getActivity());
            rvChatted.setLayoutManager(layoutManagerChatted);
            ChatAdapter chatAdapter = new ChatAdapter(profileChattedList,this);
            rvChatted.setAdapter(chatAdapter);
    }

    public void setMockData(){
        profileList.add(new MatchedProfile("1YyOVbEZ9nbclrT9iX5GIRTCboA3","Xaki","https://firebasestorage.googleapis.com/v0/b/prm391-final-project.appspot.com/o/profile_photos%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2%2Fbackground_image.png?alt=media&token=e66b3b2a-58f0-4890-a098-6c4eb17ba83d","MayDitFPT","12:00","online","Hung"));
        profileList.add(new MatchedProfile("AmfYFNPgJxbltQfOhOUEkmfvEx63","Vinhdh","https://firebasestorage.googleapis.com/v0/b/prm391-final-project.appspot.com/o/profile_photos%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2_0.jpg?alt=media&token=107639eb-9a8a-494a-a921-e3b9eb41ba25","BeoHamNghi","12:00","5 minutes ago","Tri"));
        profileList.add(new MatchedProfile("EqVdSFIZhmbfDdTVJSbXb1hB78l1","Hungnd","https://firebasestorage.googleapis.com/v0/b/prm391-final-project.appspot.com/o/profile_photos%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2_0.jpg?alt=media&token=107639eb-9a8a-494a-a921-e3b9eb41ba25",null,"12:00","online","Xaki"));
        profileList.add(new MatchedProfile("GzH63ZC55UeAOP5f316DiS08Hj82","Tri","https://firebasestorage.googleapis.com/v0/b/prm391-final-project.appspot.com/o/profile_photos%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2%2FnjZ3h3Mn4yUqWqZk0ZoSl2qGP5b2_0.jpg?alt=media&token=107639eb-9a8a-494a-a921-e3b9eb41ba25",null,"12:00","25 minutes ago","Vinh"));
        for(MatchedProfile profile: profileList){
            if (profile.getLastMessage() == null){
                profileMatchedList.add(profile);
            }else profileChattedList.add(profile);
        }
    }
    @Override
    public void onItemClick(String uid) {
//        Todo: intent to another screen
    }
}
