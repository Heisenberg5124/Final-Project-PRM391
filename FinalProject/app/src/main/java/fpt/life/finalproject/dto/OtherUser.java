package fpt.life.finalproject.dto;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OtherUser {
    private String userID;
    private String name;
    private int age;
    private String city;
    private String bio;
    private ArrayList<String> hobbies;
    private ArrayList<String> listImage;
}
