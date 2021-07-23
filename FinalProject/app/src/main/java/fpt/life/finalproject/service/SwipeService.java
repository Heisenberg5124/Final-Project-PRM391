package fpt.life.finalproject.service;

import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fpt.life.finalproject.dto.HomePageProfile;
import fpt.life.finalproject.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SwipeService {
    private TreeMap<String, User> userList;
    private List<HomePageProfile> homePageProfileList = new ArrayList<>();
    private User currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SwipeService(User currentUser) {
        this.currentUser = currentUser;
    }

    public void getAllDocument(CountDownTimer countDownTimer) {
        countDownTimer.start();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        TreeMap<String, User> users = new TreeMap<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uid = document.getId();
                                User user = document.toObject(User.class);
                                users.put(uid, user);
                            }
                            userList = users; //TODO: co the bi bug
                            countDownTimer.onFinish();
                            countDownTimer.cancel();
                        } else {
                            Log.d("getAll", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateUserlist(String key, User value) {
        userList.replace(key, value);
    }

    public void addItemUserlist(String key, User value) {
        userList.put(key, value);
    }

    public void loadProfiles() {
//        getCurrentUser(currentUserId);
        filterProfiles();
    }

    public void filterProfiles() {
        homePageProfileList.clear();
        List<HomePageProfile> homePageProfileList = new ArrayList<>();
        Log.d("size", userList.size() + "");
        for (Map.Entry<String, User> entry : userList.entrySet()) {
            if (checkValidUser(entry.getValue())) {
                String uid = entry.getValue().getUid();
                String image = entry.getValue().getPhotoUrls().get(0);
                String name = entry.getValue().getName();
                int age = calculateAge(entry.getValue().getBirthday());
                String city = entry.getValue().getCity();
                double distance = getDistance(currentUser.getLocation(), entry.getValue().getLocation());
                HomePageProfile homePageProfile = HomePageProfile.builder()
                        .uid(uid)
                        .image(image)
                        .name(name)
                        .age(age)
                        .city(city)
                        .distance(distance)
                        .build();
                homePageProfileList.add(homePageProfile);
            }
        }
        this.homePageProfileList.addAll(homePageProfileList);
    }

    private boolean checkValidUser(User anotherUser) {
        return !isDuplicateCurrentId(anotherUser)
                && isGenderIWant(anotherUser)
                && isGenderTheyWant(anotherUser)
                && isAgeIWant(anotherUser)
                && !isLocationNull(anotherUser)
                && isInDistanceIWant(anotherUser)
                && !isInLikedList(anotherUser)
                && !isInDislikedList(anotherUser);

    }

    private boolean isLocationNull(User anotherUser) {
        return anotherUser.getLocation() == null;
    }

    private boolean isDuplicateCurrentId(User anotherUser) {
        return currentUser.getUid().equals(anotherUser.getUid());
    }

    private boolean isGenderIWant(User anotherUser) {
        if (currentUser.getShowMeGender().equalsIgnoreCase("Everyone")) {
            return true;
        } else return currentUser.getShowMeGender().equalsIgnoreCase(anotherUser.getGender());
    }

    private boolean isGenderTheyWant(User anotherUser) {
        if (anotherUser.getShowMeGender().equalsIgnoreCase("Everyone")) {
            return true;
        } else return anotherUser.getShowMeGender().equalsIgnoreCase(currentUser.getGender());
    }

    private boolean isAgeIWant(User anotherUser) {
        int anotherUserAge = calculateAge(anotherUser.getBirthday());
        //check range age
        return currentUser.getRangeAge().get("min") < anotherUserAge
                && anotherUserAge < currentUser.getRangeAge().get("max");
    }

    private boolean isInDistanceIWant(User anotherUser) {
        return getDistance(currentUser.getLocation(), anotherUser.getLocation()) < currentUser.getRangeDistance();
    }

    private boolean isInLikedList(User anotherUser) {
        return currentUser.getUserLiked().contains(anotherUser.getUid());
    }

    private boolean isInDislikedList(User anotherUser) {
        return currentUser.getUserDisliked().contains(anotherUser.getUid());
    }

    private int calculateAge(Date anotherUserBirthday) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar anotherUserBirthdayCalendar = Calendar.getInstance();
        anotherUserBirthdayCalendar.setTime(anotherUserBirthday);
        int anotherUserYear = anotherUserBirthdayCalendar.get(Calendar.YEAR);
        return currentYear - anotherUserYear;
    }

    private double getDistance(GeoPoint gp1, GeoPoint gp2) {
        Location loc1 = new Location("");
        loc1.setLatitude(gp1.getLatitude());
        loc1.setLongitude(gp1.getLongitude());

        Location loc2 = new Location("");
        loc2.setLatitude(gp2.getLatitude());
        loc2.setLongitude(gp2.getLongitude());

        return Math.round(loc1.distanceTo(loc2) / 100) / 10.0;
    }

    public void swipeRight() {
        currentUser.getUserLiked().add(homePageProfileList.get(0).getUid());
        updateOnSwipe("userLiked", currentUser.getUserLiked());
        checkMatch(homePageProfileList.get(0).getUid());
        Log.d("checkswiperight", "1");
        homePageProfileList.remove(0);
    }

    public void swipeLeft() {
        currentUser.getUserDisliked().add(homePageProfileList.get(0).getUid());
        updateOnSwipe("userDisliked", currentUser.getUserDisliked());
        homePageProfileList.remove(0);
    }

    private void checkMatch(String otherLikedUserUid) {
        User otherLikedUser = userList.get(otherLikedUserUid);
        if (otherLikedUser != null) {
            if (otherLikedUser.getUserLiked().contains(currentUser.getUid())) {
                createMatchedUser(otherLikedUserUid);
            }
        }
        Log.d("checkswiperight", "1");
    }

    private void createMatchedUser(String otherLikedUserUid) {
        //create data
        Map<String, Object> matchedUserData = new HashMap<>();
        ArrayList<String> userMatchedList = new ArrayList<>();
        Map<String, Boolean> isKnownMap = new HashMap<>();
        Map<String, Boolean> isNotifyMap = new HashMap<>();
        isKnownMap.put(currentUser.getUid(), false);
        isKnownMap.put(otherLikedUserUid, false);
        isNotifyMap.put(currentUser.getUid(), false);
        isNotifyMap.put(otherLikedUserUid, false);
        userMatchedList.add(currentUser.getUid());
        userMatchedList.add(otherLikedUserUid);
        //put data to field
        matchedUserData.put("lastMessage", "0000");
        matchedUserData.put("isKnown", isKnownMap);
        matchedUserData.put("isNotify", isNotifyMap);
        matchedUserData.put("sender", userMatchedList);
        matchedUserData.put("sendTime", Timestamp.now());
        //format matchedUid
        String matchedUid = currentUser.getUid().compareTo(otherLikedUserUid) <= 0
                ? currentUser.getUid() + "_" + otherLikedUserUid
                : otherLikedUserUid + "_" + currentUser.getUid();
        //create document
        CollectionReference matchedUserReference = db.collection("matched_users");
        DocumentReference coupleReference = matchedUserReference.document(matchedUid);
        coupleReference.collection("messages")
                .document("0000")
                .set(new HashMap<>())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        coupleReference.set(matchedUserData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        Log.d("checkMatched", currentUser.getUid() + "_" + otherLikedUserUid);
                                    }
                                });
                    }
                });
        Log.d("checkswiperight", "1");
    }


    private void updateOnSwipe(String field, List<String> list) {
        DocumentReference currentUserRef = db.collection("users").document(currentUser.getUid());
        currentUserRef.update(field, list);
    }

}
