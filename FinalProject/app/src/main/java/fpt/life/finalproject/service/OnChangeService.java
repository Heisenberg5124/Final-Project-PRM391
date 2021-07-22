package fpt.life.finalproject.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.HomePageCardStackAdapter;
import fpt.life.finalproject.model.User;

public class OnChangeService {

    private static final String CHECK_ONLINE_STATUS_TASK = "CHECK_ONLINE_STATUS_TASK";
    private static final String CHECK_UPDATE_DATA_TASK = "CHECK_UPDATE_DATA_TASK";
    private static final String CHANNEL_ID = "Channel_1";
    private static final String DEFAULT_ID_LAST_MESSAGE = "0000";
    private static final String currentUserUid = FirebaseAuth.getInstance().getUid();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private Activity activity;

    public OnChangeService(Activity activity) {
        this.activity = activity;
    }

    public OnChangeService() {
    }

    public void upDateStatus(boolean status) {
        documentReference = db.collection("users").document(currentUserUid);
        documentReference.update("onlineStatus", status);
        if (!status) {
            Timestamp timeStamp = Timestamp.now();
            documentReference.update("lastTimeOnline", timeStamp);
        }
    }

    public void listenMatchedUsersNotify() {
        db.collection("matched_users").whereEqualTo("isNotify",false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("checkOnline", error + "");
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Log.d("checkSnapshot", dc.getType().toString());
                    switch (dc.getType()) {
                        case ADDED:
                            if (dc.getDocument().getId().contains(currentUserUid)) {
                                String matchedUid = dc.getDocument().getId();
                                notifyMatch();
                                listenMatchedUsersIsKnown();
                                updateIsNotify(matchedUid,"isNotify."+currentUserUid,true);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void updateIsNotify(String documentUID,String field, boolean data) {
        db.collection("matched_users").document(documentUID)
                .update(field, data);
    }

    public void listenMatchedUsersIsKnown() {
        db.collection("matched_users").whereEqualTo("isKnown."+currentUserUid,false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().size()>0){
                        ((MainActivity)activity).setNotifyCircleVisibility(true);
                        Log.d("checkMatch0", "ok");
                    }else {
                        listenChatIsSeen();
                    }
                }
            }
        });
    }

    private void listenChatIsSeen() {
        db.collection("matched_users")
                .whereEqualTo("isKnown."+currentUserUid,true)
                .whereEqualTo("lastMessage.isSeen",false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().size()>0){
                        ((MainActivity)activity).setNotifyCircleVisibility(true);
                        Log.d("checkChat0", task.getResult().getDocuments().toString());
                    }else {
                        ((MainActivity)activity).setNotifyCircleVisibility(false);
                    }
                }
            }
        });
    }

    public void updateMatchedUserIsKnown(){
        db.collection("matched_users")
                .whereEqualTo("isKnown."+currentUserUid,false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot dc : task.getResult()){
                        db.collection("matched_users").document(dc.getId())
                                .update("isKnown."+currentUserUid, true);
                    }
                    listenMatchedUsersIsKnown();
                }
            }
        });
    }

    private void notifyMatch() {
        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent appIntent = new Intent(activity, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(activity, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Matched Channel";
            String description = "This is Matched Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
//        Bitmap bitmapLogo = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo);
        Notification builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                .setContentTitle("New Matched!")
                .setContentText("Ting Ting! You have new matched. Let's see who it is!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();
        if (notificationManager !=null){
            int random = (int)new Date().getTime();
            notificationManager.notify(random,builder);
        }
        Log.d("checkMatched", "Ting Ting! You have new matched. Let's see who it is!");
    }

    public void listenDataUsersOnChange(SwipeService swipeService, HomePageCardStackAdapter cardAdapter) {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("checkDataChange:", error + "");
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Log.d("checkSnapshot", dc.getType().toString());
                    User user = dc.getDocument().toObject(User.class);
                    switch (dc.getType()) {
                        case MODIFIED:
                            swipeService.updateUserlist(user.getUid(),user);
                            swipeService.filterProfiles();
                            cardAdapter.notifyDataSetChanged();
                            Log.d("checkDataChange:", "true");
                            break;
                        case ADDED:
                            swipeService.addItemUserlist(user.getUid(),user);
                            swipeService.filterProfiles();
                            cardAdapter.notifyDataSetChanged();
//                            Log.d("checkDataChange:", user.getName());
                            break;
                    }
                }
            }
        });
    }

    public void listenOnlineUsersOnChange() {
        db.collection("users")
                .whereArrayContains("likedList",currentUserUid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("checkOnline", error + "");
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case MODIFIED:
                            User user = dc.getDocument().toObject(User.class);
                            checkOnlineStatus(user);
                            break;
                    }
                }
            }
        });
    }

    private void checkOnlineStatus(User user) {
        Log.d("checkOnline", user.getUid() + ": " + user.isOnlineStatus());
    }

    private void unmatch(String otherUid){
        String matchedUid = currentUserUid.compareTo(otherUid) <= 0
                ? currentUserUid + "_" + otherUid
                : otherUid + "_" + currentUserUid;
        db.collection("matched_users").document(matchedUid).delete();
    }

}
