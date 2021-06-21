package fpt.life.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HomePageProfile {
    private String image;
    private String name;
    private int age;
    private String city;
}
