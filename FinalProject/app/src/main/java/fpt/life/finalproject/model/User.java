package fpt.life.finalproject.model;

import java.util.ArrayList;
import java.util.Date;

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
    private Location location;
    private String city;

    private ArrayList<User> userLiked;
    private ArrayList<User> userDisliked;
    private ArrayList<User> userMatched;
}
