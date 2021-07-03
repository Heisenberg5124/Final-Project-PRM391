package fpt.life.finalproject.dto;

import java.util.ArrayList;
import java.util.Date;

import fpt.life.finalproject.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MyProfile {
    private String uid;
    private String name;
    private Date birthday;
    private String gender;
    private String showMeGender;
    private ArrayList<String> hobbies;
    private String avt;
}
