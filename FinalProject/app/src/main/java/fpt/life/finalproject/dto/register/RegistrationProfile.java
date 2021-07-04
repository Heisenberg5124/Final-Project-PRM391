package fpt.life.finalproject.dto.register;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import fpt.life.finalproject.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegistrationProfile implements Parcelable {

    private String name;
    private String birthday;
    private String bio;

    private String gender;
    private String showMeGender;

    private List<String> hobbies;

    private List<String> photos;

    private String city;

    private double latitude;
    private double longitude;

    protected RegistrationProfile(Parcel in) {
        name = in.readString();
        birthday = in.readString();
        bio = in.readString();
        gender = in.readString();
        showMeGender = in.readString();
        hobbies = in.createStringArrayList();
        photos = in.createStringArrayList();
        city = in.readString();
    }

    public static final Creator<RegistrationProfile> CREATOR = new Creator<RegistrationProfile>() {
        @Override
        public RegistrationProfile createFromParcel(Parcel in) {
            return new RegistrationProfile(in);
        }

        @Override
        public RegistrationProfile[] newArray(int size) {
            return new RegistrationProfile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(birthday);
        dest.writeString(bio);
        dest.writeString(gender);
        dest.writeString(showMeGender);
        dest.writeStringList(hobbies);
        dest.writeStringList(photos);
        dest.writeString(city);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
