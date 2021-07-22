package fpt.life.finalproject.adapter;

import fpt.life.finalproject.dto.MatchedProfile;

public interface OnInforAnotherUserChange {
        void onNewMatchedProfile(MatchedProfile matchedProfile);
        void onNameChange(int pos, String newName, String typeUser);
        void onIsOnlineChange(int pos, Boolean isOnline, String typeUser);
        void onMessageChange(int pos, MatchedProfile matchedProfile);
        void onNewChattedProfile(int posInMatched, MatchedProfile matchedProfile);
        void unMatched(String uid, String type, MatchedProfile Profile);
}
