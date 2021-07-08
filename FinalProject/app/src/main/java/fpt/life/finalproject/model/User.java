package fpt.life.finalproject.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    private String uid;
    private String name;
    private Date birthday;
    private String gender;
    private String showMeGender;
    private ArrayList<String> hobbies;
    private String bio;
    private ArrayList<String> photoUrls;
    private GeoPoint location;
    private String city;
    private Map<String, Integer> rangeAge = new HashMap<>();
    private int rangeDistance;

    private ArrayList<String> userLiked;
    private ArrayList<String> userDisliked;
}
