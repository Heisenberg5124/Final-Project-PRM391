package fpt.life.finalproject.dto.chat;

import com.google.firebase.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FireStoreMessage {

    private String sender;

    private String content;
    private String image;

    private Timestamp sendTime;
    private boolean isSeen;
}
