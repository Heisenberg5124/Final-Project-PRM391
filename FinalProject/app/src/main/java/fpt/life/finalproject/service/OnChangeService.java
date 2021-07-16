package fpt.life.finalproject.service;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.adapter.HomePageCardStackAdapter;
import fpt.life.finalproject.dto.MatchedProfile;
import fpt.life.finalproject.model.User;

public class OnChangeService {

    private static final String CHECK_ONLINE_STATUS_TASK = "CHECK_ONLINE_STATUS_TASK";
    private static final String CHECK_UPDATE_DATA_TASK = "CHECK_UPDATE_DATA_TASK";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    public void upDateStatus(boolean status) {
        documentReference = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        documentReference.update("onlineStatus", status);
        if (!status) {
            Timestamp timeStamp = Timestamp.now();
            documentReference.update("lastTimeOnline", timeStamp);
        }
    }

    public void listenMatchedUsersOnChange() {
        db.collection("matched_users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("checkOnline", error + "");
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            if (dc.getDocument().getId().contains(FirebaseAuth.getInstance().getUid())) {
                                notifyMatch();
                            }
                            break;
                    }
                }
            }
        });
    }

    private void notifyMatch() {
        Log.d("checkMatched", "You have new matched");
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
                            break;
                    }
                }
            }
        });
    }

    public void listenOnlineUsersOnChange() {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

}
