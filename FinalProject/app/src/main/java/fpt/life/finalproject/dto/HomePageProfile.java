package fpt.life.finalproject.dto;

import com.google.firebase.firestore.GeoPoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HomePageProfile {
    private String uid;
    private String image;
    private String name;
    private int age;
    private String city;
    private double distance;
}
