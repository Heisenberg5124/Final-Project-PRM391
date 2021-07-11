package fpt.life.finalproject.service;

import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, User> userList;
    private List<HomePageProfile> homePageProfileList = new ArrayList<>();
    private User currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getCurrentUser(String uid) {
        currentUser = userList.get(uid);
    }

    public void getAllDocument(CountDownTimer countDownTimer) {

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, User> users = new HashMap<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uid = document.getId();
                                User user = document.toObject(User.class);
                                users.put(uid, user);
                            }
                            userList =users; //TODO: co the bi bug
                            countDownTimer.onFinish();
                            countDownTimer.cancel();
                        } else {
                            Log.d("getAll", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public void loadProfiles(String currentUserId){
//        getCurrentUser("1YyOVbEZ9nbclrT9iX5GIRTCboA3");
        getCurrentUser(currentUserId);
        homePageProfileList.clear();
        filterProfiles();
    }

    public void filterProfiles() {
        List<HomePageProfile> homePageProfileList = new ArrayList<>();
        Log.d("size", userList.size()+"");
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
                && isAgeIWant(anotherUser)
                && isInDistanceIWant (anotherUser)
                && !isInLikedList(anotherUser)
                && !isInDislikedList(anotherUser);
    }

    private boolean isDuplicateCurrentId(User anotherUser){
        return currentUser.getUid().equals(anotherUser.getUid());
    }

    private boolean isGenderIWant(User anotherUser){
        if (currentUser.getShowMeGender().equals("Everyone")) {
            return true;
        }else return currentUser.getShowMeGender().equals(anotherUser.getGender());
    }

    private boolean isAgeIWant(User anotherUser){
        int anotherUserAge = calculateAge(anotherUser.getBirthday());
        //check range age
        return  currentUser.getRangeAge().get("min") < anotherUserAge
                && anotherUserAge < currentUser.getRangeAge().get("max");
    }

    private boolean isInDistanceIWant(User anotherUser){
        return  getDistance(currentUser.getLocation(), anotherUser.getLocation()) < currentUser.getRangeDistance();
    }

    private boolean isInLikedList(User anotherUser){
        return currentUser.getUserLiked().contains(anotherUser.getUid());
    }

    private boolean isInDislikedList(User anotherUser){
        return currentUser.getUserDisliked().contains(anotherUser.getUid());
    }

    private int calculateAge(Date anotherUserBirthday){
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
        homePageProfileList.remove(0);
    }

    public void swipeLeft() {
        currentUser.getUserDisliked().add(homePageProfileList.get(0).getUid());
        updateOnSwipe("userDisliked", currentUser.getUserDisliked());
        homePageProfileList.remove(0);
    }

    private void updateOnSwipe(String field, List<String> list) {
        DocumentReference currentUserRef = db.collection("users").document(currentUser.getUid());
        currentUserRef.update(field, list);
    }

}
