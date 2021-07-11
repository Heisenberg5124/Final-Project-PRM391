package fpt.life.finalproject.dto;

import android.os.Parcel;
import android.os.Parcelable;

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
public class MyProfile implements Parcelable {
    private String uid;
    private String name;
    private Date birthday;
    private String gender;
    private String showMeGender;
    private ArrayList<String> hobbies;
    private String avt;

    protected MyProfile(Parcel in) {
        uid = in.readString();
        name = in.readString();
        gender = in.readString();
        showMeGender = in.readString();
        hobbies = in.createStringArrayList();
        avt = in.readString();
    }

    public static final Creator<MyProfile> CREATOR = new Creator<MyProfile>() {
        @Override
        public MyProfile createFromParcel(Parcel in) {
            return new MyProfile(in);
        }

        @Override
        public MyProfile[] newArray(int size) {
            return new MyProfile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(showMeGender);
        dest.writeStringList(hobbies);
        dest.writeString(avt);
    }
}
