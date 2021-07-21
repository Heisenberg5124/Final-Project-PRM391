package fpt.life.finalproject.service;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import fpt.life.finalproject.dto.MatchedProfile;
import fpt.life.finalproject.screen.matched.OnInforAnotherUserChange;
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
        colRef.whereArrayContains("sender", currentUserID).addSnapshotListener((value, error) -> {
            for (DocumentChange document : value.getDocumentChanges()) {
                ArrayList<String> sender = (ArrayList<String>) document.getDocument().get("sender");
                String otherUid = sender.get(0).equals(currentUserID) ? sender.get(1) : sender.get(0);
                if (document.getType() == DocumentChange.Type.REMOVED) {
                    //Todo:Remove chưa handle được
                    Log.d("checkRemove", otherUid);
                    String typeUser = (checkExistProfile(otherUid,chatProfileList)!= -1) ? "Chatted" : "Matched";
                    MatchedProfile removedProfile = typeUser.equals("Chatted") ? chatProfileList.get(checkExistProfile(otherUid,chatProfileList)):
                            matchedProfileList.get(checkExistProfile(otherUid,matchedProfileList));
                    onInforAnotherUserChange.unMatched(otherUid,typeUser,removedProfile);
                } else {
                    Map<String, Object> lastMessageMap = (Map<String, Object>) document.getDocument().get("lastMessage");
                    if (!(lastMessageMap.get("id").toString().equals("0000"))) {
//                        docRefGetMessages = db.collection("matched_users").document(document.getDocument().getId()).collection("messages")
//                                .document((String) document.getDocument().get("lastMessage"));
//                        docRefGetMessages.addSnapshotListener((valueUser, errorUser) -> {
                        String lastMessage = (lastMessageMap.get("image").equals("")) ? lastMessageMap.get("content").toString() : "Send a image to you";
                        Log.d("checkRemove", lastMessageMap.get("id").toString());
                        getAnotherMatchedInfo(onInforAnotherUserChange,otherUid, lastMessage, lastMessageMap.get("id").toString(), timestampToString((Timestamp) lastMessageMap.get("sendTime")), lastMessageMap.get("sender").toString());
                    } else {
                        getAnotherMatchedInfo(onInforAnotherUserChange, otherUid,null, null, null, null);
                    }
                }
            }

        });
    }

    public String timestampToString(Timestamp timestamp) {
        Long distanceTime = System.currentTimeMillis() / 1000 - timestamp.getSeconds();
        SimpleDateFormat formatter;
        if (distanceTime < 86400) {
            formatter = new SimpleDateFormat("HH:mm");
        } else {
            formatter = new SimpleDateFormat("dd MMM");
        }
        Log.d("CheckDate",timestamp.getSeconds()+" ");
        Log.d("CheckDate",timestamp.toString());
        return formatter.format(new Date(timestamp.getSeconds()));
    }

    private void getAnotherMatchedInfo(OnInforAnotherUserChange onInforAnotherUserChange,String uid, String lastMessage, String lastMessageID, String timeLastMessage, String sender) {
        DocumentReference docRefGetChatInfo = db.collection("users").document(uid);
        docRefGetChatInfo.addSnapshotListener((valueGetChatInfo, error1) -> {

            int posInChattedProfile = checkExistProfile(uid, chatProfileList);
            int posInMatchedProfile = checkExistProfile(uid, matchedProfileList);

            MatchedProfile isMatchedProfile = posInMatchedProfile != -1 ? matchedProfileList.get(posInMatchedProfile) : null;
            MatchedProfile isChattedProfile = posInChattedProfile != -1 ? chatProfileList.get(posInChattedProfile) : null;
            String senderName = null;
            if (sender != null) {
                senderName = sender.equals(currentUserID) ? currentUserName : (String) valueGetChatInfo.get("name");
            }
            ArrayList<String> photoUrls = (ArrayList<String>) valueGetChatInfo.get("photoUrls");
            MatchedProfile Profile = new MatchedProfile(uid, (String) valueGetChatInfo.get("name"), photoUrls.get(0), lastMessage, lastMessageID, timeLastMessage, (Boolean) valueGetChatInfo.get("onlineStatus"), senderName);

            if (isMatchedProfile != null) {
                // check this uid had change they online status or name
                if (checkChangeOnlineStatus((Boolean) valueGetChatInfo.get("onlineStatus"), isMatchedProfile))
                    onInforAnotherUserChange.onIsOnlineChange(posInMatchedProfile, (Boolean) valueGetChatInfo.get("onlineStatus"), "Matched");
                else if (checkChangeName((String) valueGetChatInfo.get("name"), isMatchedProfile))
                    onInforAnotherUserChange.onNameChange(posInMatchedProfile, (String) valueGetChatInfo.get("name"), "Matched");
                    else if (lastMessageID != null) {
                    onInforAnotherUserChange.onNewChattedProfile(posInMatchedProfile,Profile);
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
                        Log.d("CheckChange","Change");
                        isChattedProfile.setLastMessage(lastMessage);
                        isChattedProfile.setLastMessageID(lastMessageID);
                        isChattedProfile.setTimeLastMessage(timeLastMessage);
                        onInforAnotherUserChange.onMessageChange(posInChattedProfile, isChattedProfile);
                        chatProfileList.remove(isChattedProfile);
                        chatProfileList.add(0, Profile);
                    }
                }
                else if (Profile.getLastMessage()==null){
                    onInforAnotherUserChange.onNewMatchedProfile(Profile);
                    matchedProfileList.add(Profile);
                }else {
                   onInforAnotherUserChange.onNewChattedProfile(-1,Profile);
                    chatProfileList.add(0,Profile);
                    Log.d("CheckSize", " " + chatProfileList.size());
                }
        });
    }

    private int checkExistProfile(String uid, ArrayList<MatchedProfile> ProfileList) {
        for (int i=0;i<ProfileList.size();i++) {
            if (ProfileList.get(i).getOtherUid().equals(uid)) return i;
        }
        return -1;
    }

    private Boolean checkChangeName(String newName, MatchedProfile matchedProfile) {
        if (!matchedProfile.getOtherUserName().equals(newName)) return true;
        return false;
    }

    private Boolean checkChangeLastMessageByID(String newMessageID, MatchedProfile matchedProfile) {
        if (!matchedProfile.getLastMessageID().equals(newMessageID)) return true;
        return false;
    }

    private Boolean checkChangeOnlineStatus(Boolean newOnlineStatus, MatchedProfile Profile) {
        if (Profile.getOnlineStatus() != newOnlineStatus) return true;
        return false;
    }
}

