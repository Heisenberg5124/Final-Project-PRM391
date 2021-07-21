package fpt.life.finalproject.dto.chat;

import com.google.firebase.Timestamp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatRoom {

    private String myUid;
    private String otherUid;

    private String otherName;
    private String otherAvatarUrl;
    private boolean isOnline;
    private String lastTimeOnline;
}
