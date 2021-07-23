package fpt.life.finalproject.service.chat;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fpt.life.finalproject.dto.chat.ChatRoom;
import fpt.life.finalproject.dto.chat.FireStoreMessage;
import fpt.life.finalproject.dto.chat.Message;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.util.TimestampUtil;
import lombok.Data;

@Data
public class ChatService {

    private OnFirebaseListener firebaseListener;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private ChatRoom chatRoom;
    private ArrayList<Message> messages;

    private String myUid;
    private String otherUid;

    public ChatService(OnFirebaseListener firebaseListener, String myUid, String otherUid) {
        this.firebaseListener = firebaseListener;
        this.myUid = myUid;
        this.otherUid = otherUid;
        chatRoom = new ChatRoom();
        messages = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("image_messages");
    }

    private String generateMatchedUid() {
        return myUid.compareTo(otherUid) <= 0
                ? myUid + "_" + otherUid
                : otherUid + "_" + myUid;
    }

    public void getChatRoomInfo() {
        firebaseFirestore.collection("users").document(otherUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            chatRoom = ChatRoom.builder()
                                    .myUid(myUid)
                                    .otherUid(otherUid)
                                    .otherName(user.getName())
                                    .otherAvatarUrl(user.getPhotoUrls().get(0))
                                    .isOnline(user.isOnlineStatus())
                                    .build();
                            chatRoom.setLastTimeOnline(TimestampUtil.getLastTimeOnline(user.getLastTimeOnline(), chatRoom.isOnline()));
                            firebaseListener.onCompleteLoadChatRoomInfo(chatRoom);
                        }
                    }
                });
    }

    public void uploadMessageImage(Uri uri) {
        StorageReference childReference = storageReference
                .child(generateMatchedUid() + "/" + uri.getLastPathSegment());
        childReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    childReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                                sendMessage("", task.getResult().toString());
                        }
                    });
                }
            }
        });
    }

    public void sendMessage(String messageContent, String imageUrl) {
        Map<String, Object> fireStoreMessage = new HashMap<>();
        fireStoreMessage.put("sender", myUid);
        fireStoreMessage.put("content", messageContent);
        fireStoreMessage.put("image", imageUrl);
        fireStoreMessage.put("isSeen", false);
        fireStoreMessage.put("sendTime", Timestamp.now());

        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists())
                    saveMessageToFireStore(fireStoreMessage);
                else
                    firebaseListener.onOtherUnmatched(chatRoom);
            }
        });
    }

    private void saveMessageToFireStore(Map<String, Object> message) {
        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .collection("messages").add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("SendMessage", "sendMessage: " + task.getResult().getId());
                    message.put("id", task.getResult().getId());
                    saveLastMessage(message);
                }
            }
        });
    }

    private void saveLastMessage(Map<String, Object> lastMessage) {
        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .update("lastMessage", lastMessage);
        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .update("sendTime", lastMessage.get("sendTime"));
        seenAllMessages();
    }

    public void getAllMessages() {
        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .collection("messages").orderBy("sendTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int i = 0;
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            switch (documentChange.getType()) {
                                case ADDED:
                                    FireStoreMessage fireStoreMessage = documentChange.getDocument().toObject(FireStoreMessage.class);
                                    Message message = Message.builder()
                                            .messageUid(documentChange.getDocument().getId())
                                            .senderUid(fireStoreMessage.getSender())
                                            .avatarUrl(chatRoom.getOtherAvatarUrl())
                                            .content(fireStoreMessage.getContent())
                                            .imageUrl(fireStoreMessage.getImage())
                                            .isSeen(documentChange.getDocument().getBoolean("isSeen"))
                                            .sendTime(fireStoreMessage.getSendTime())
                                            .isShowSendTime(false)
                                            .build();
                                    message.setSendTimeShow(TimestampUtil.getSendTime(message.getSendTime()));
                                    messages.add(message);

                                    Log.d("Seen", "setSeenStatus: " + message.getMessageUid() + " _ " + message.isSeen());
                                    break;
                                case MODIFIED:
                                    for (Message modifiedMessage : messages) {
                                        if (modifiedMessage.getMessageUid().equals(documentChange.getDocument().getId()))
                                            modifiedMessage.setSeen(documentChange.getDocument().getBoolean("isSeen"));
                                    }
                                    break;
                            }
                        }
                        firebaseListener.onCompleteLoadMessages(chatRoom);
                    }
                });
    }

    public void seenAllMessages() {
        firebaseFirestore.collection("matched_users").document(generateMatchedUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!((Map<String, Object>) task.getResult().get("lastMessage")).get("id").equals("0000")) {
                    Log.d("Message", "seenAllMessagesBefore: ");
                    firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                            .collection("messages").whereEqualTo("sender", otherUid)
                            .whereEqualTo("isSeen",false)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                                        .collection("messages").document(documentSnapshot.getId())
                                        .update("isSeen", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                Log.d("Message", "seenAllMessagesAfter: ");
                            }
                            setLastMessageSeen();
                        }
                    });
                }
            }
        });
    }

    private void setLastMessageSeen() {
        firebaseFirestore.collection("matched_users").document(generateMatchedUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String senderUid = ((Map<String, Object>) task.getResult().get("lastMessage")).get("sender").toString();
                if (senderUid.equals(otherUid))
                    firebaseFirestore.collection("matched_users").document(generateMatchedUid()).update("lastMessage.isSeen", true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("Message", "seenAllMessagesLastMessage: ");
                                }
                            });
            }
        });
    }

    public void onChangeChatRoomInfo() {
        firebaseFirestore.collection("users").document(otherUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                User user = value.toObject(User.class);
                chatRoom = ChatRoom.builder()
                        .myUid(myUid)
                        .otherUid(otherUid)
                        .otherName(user.getName())
                        .otherAvatarUrl(user.getPhotoUrls().get(0))
                        .isOnline(user.isOnlineStatus())
                        .build();
                Log.d("Timestamp", "CheckTimestamp: " + (user.getLastTimeOnline() == null));
                chatRoom.setLastTimeOnline(TimestampUtil.getLastTimeOnline(user.getLastTimeOnline(), chatRoom.isOnline()));
                firebaseListener.onChangeChatRoomInfo(chatRoom);
            }
        });
    }

    public void unmatched(){
        /*String matchedUid = generateMatchedUid();
        firebaseFirestore.collection("matched_users").document(matchedUid).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            firebaseListener.onCompleteUnmatched(chatRoom);
                    }
                });*/
        firebaseListener.onCompleteUnmatched(chatRoom);
        Log.d("Intent", "onActivityResult: ");
    }
}
