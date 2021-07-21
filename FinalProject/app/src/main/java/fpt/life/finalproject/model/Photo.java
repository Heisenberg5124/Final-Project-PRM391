package fpt.life.finalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Photo {

    private String photoUri;
    private boolean isEmpty;
    private boolean isExistFireBase;
}
