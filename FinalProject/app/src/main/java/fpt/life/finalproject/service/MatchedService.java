package fpt.life.finalproject.service;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import fpt.life.finalproject.dto.MatchedProfile;
import fpt.life.finalproject.adapter.OnInforAnotherUserChange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MatchedService {
    private String currentUserID;
    private String currentUserName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<MatchedProfile> matchedProfileList = new ArrayList<>();
    private ArrayList<MatchedProfile> chatProfileList = new ArrayList<>();
    private CollectionReference colRef;
    private DocumentReference docRefGetMessages;
    private Boolean isChatted;

    public MatchedService(String currentUserID, String currentUserName) {
        this.currentUserID = currentUserID;
        this.currentUserName = currentUserName;
        colRef = db.collection("matched_users");
    }

    public void snapshotMatchUser(OnInforAnotherUserChange onInforAnotherUserChange) {
        colRef.orderBy("sendTime", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            for (DocumentChange document : value.getDocumentChanges()) {
                ArrayList<String> sender = (ArrayList<String>) document.getDocument().get("sender");
                if (sender.get(0).equals(currentUserID) || sender.get(1).equals(currentUserID)) {
                    String otherUid = sender.get(0).equals(currentUserID) ? sender.get(1) : sender.get(0);
                    Map<String, Object> lastMessageMap = (Map<String, Object>) document.getDocument().get("lastMessage");
                    if (!(lastMessageMap.get("id").toString().equals("0000"))) {
                        String lastMessage = (lastMessageMap.get("image").equals("")) ? lastMessageMap.get("content").toString() : "Send a image to you";
                        Log.d("checkRemove", lastMessageMap.get("id").toString());
                        getAnotherMatchedInfo(onInforAnotherUserChange, otherUid, lastMessage, lastMessageMap.get("id").toString(), (Timestamp) lastMessageMap.get("sendTime"), (Boolean) lastMessageMap.get("isSeen"), lastMessageMap.get("sender").toString());
                    } else {
                        getAnotherMatchedInfo(onInforAnotherUserChange, otherUid, null, null, null, null, null);
                    }
                }
            }
            onInforAnotherUserChange.finishLoad();
        });
    }

    private void getAnotherMatchedInfo(OnInforAnotherUserChange onInforAnotherUserChange, String uid, String lastMessage, String lastMessageID, Timestamp timeLastMessage, Boolean isSeen, String sender) {
        DocumentReference docRefGetChatInfo = db.collection("users").document(uid);
        docRefGetChatInfo.addSnapshotListener((valueGetChatInfo, error1) -> {

            int posInChattedProfile = checkExistProfile(uid, chatProfileList);
            int posInMatchedProfile = checkExistProfile(uid, matchedProfileList);

            MatchedProfile isMatchedProfile = posInMatchedProfile != -1 ? matchedProfileList.get(posInMatchedProfile) : null;
            MatchedProfile isChattedProfile = posInChattedProfile != -1 ? chatProfileList.get(posInChattedProfile) : null;
            ArrayList<String> photoUrls = (ArrayList<String>) valueGetChatInfo.get("photoUrls");
            MatchedProfile Profile = new MatchedProfile(uid, (String) valueGetChatInfo.get("name"), photoUrls.get(0), lastMessage, lastMessageID, timeLastMessage, (Boolean) valueGetChatInfo.get("onlineStatus"), isSeen, sender);

            if (isMatchedProfile != null) {
                // check this uid had change they online status or name
                if (checkChangeOnlineStatus((Boolean) valueGetChatInfo.get("onlineStatus"), isMatchedProfile))
                    onInforAnotherUserChange.onIsOnlineChange(posInMatchedProfile, (Boolean) valueGetChatInfo.get("onlineStatus"), "Matched");
                else if (checkChangeName((String) valueGetChatInfo.get("name"), isMatchedProfile))
                    onInforAnotherUserChange.onNameChange(posInMatchedProfile, (String) valueGetChatInfo.get("name"), "Matched");
                else if (lastMessageID != null) {
                    onInforAnotherUserChange.onNewChattedProfile(posInMatchedProfile, Profile);
                    matchedProfileList.remove(posInMatchedProfile);
                    chatProfileList.add(0, Profile);
                }

            } else
                //check uid exist in chatted profile
                if (isChattedProfile != null) {
                    //check this uid had change they name or online
                    if (checkChangeOnlineStatus((Boolean) valueGetChatInfo.get("onlineStatus"), isChattedProfile))
                        onInforAnotherUserChange.onIsOnlineChange(posInChattedProfile, (Boolean) valueGetChatInfo.get("onlineStatus"), "Chatted");
                    else if (checkChangeName((String) valueGetChatInfo.get("name"), isChattedProfile))
                        onInforAnotherUserChange.onNameChange(posInChattedProfile, (String) valueGetChatInfo.get("name"), "Chatted");
                        //check this uid first message
                    else if (checkChangeLastMessageByID(lastMessageID, isChattedProfile)) {
                        isChattedProfile.setLastMessage(lastMessage);
                        isChattedProfile.setLastMessageID(lastMessageID);
                        isChattedProfile.setTimeLastMessage(timeLastMessage);
                        onInforAnotherUserChange.onMessageChange(posInChattedProfile, isChattedProfile);
                        chatProfileList.remove(isChattedProfile);
                        chatProfileList.add(0, Profile);
                    }
                } else if (Profile.getLastMessage() == null) {
                    onInforAnotherUserChange.onNewMatchedProfile(Profile);
                    matchedProfileList.add(Profile);
                } else {
                    onInforAnotherUserChange.onNewChattedProfile(-1, Profile);
                    chatProfileList.add(0, Profile);
                }
        });
    }

    public void unmatch(String typeUser, int pos){
        if (typeUser.equals("Chat")){
            chatProfileList.remove(pos);
        }else {
            matchedProfileList.remove(pos);
        }
    }
    public int checkExistProfile(String uid, ArrayList<MatchedProfile> ProfileList) {
        for (int i = 0; i < ProfileList.size(); i++) {
            if (ProfileList.get(i).getOtherUid().equals(uid)) return i;
        }
        return -1;
    }

    private Boolean checkChangeName(String newName, MatchedProfile matchedProfile) {
        return !matchedProfile.getOtherUserName().equals(newName);
    }

    private Boolean checkChangeLastMessageByID(String newMessageID, MatchedProfile matchedProfile) {
        return !matchedProfile.getLastMessageID().equals(newMessageID);
    }

    private Boolean checkChangeOnlineStatus(Boolean newOnlineStatus, MatchedProfile Profile) {
        return Profile.getOnlineStatus() != newOnlineStatus;
    }
}

