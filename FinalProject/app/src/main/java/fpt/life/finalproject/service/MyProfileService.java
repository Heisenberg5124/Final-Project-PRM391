package fpt.life.finalproject.service;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lombok.SneakyThrows;

public class MyProfileService {
    private String userID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;

    public MyProfileService(String userID) {
        this.userID = userID;
        docRef = db.collection("users").document(userID);
    }

    //Update data to a field in firebase
    public void updateField(String field, String data) {
        docRef.update(
                field, data
        );
    }

    //Update distance
    public void updateDistance(String field, int distance){
        docRef.update(
                field, distance
        );
    }

    // Update map rangeAge in firebase
    public void updateRangeAge(int min, int max) {
        docRef.update(
                "rangeAge.max" , max,
                "rangeAge.min", min
        );
    }

    //Update timestamp birthday in firebase
    public void updateBirthDay(String field, Timestamp timestamp){
        docRef.update(
                field, timestamp
        );
    }

    //Converse date to timestamp
    @SneakyThrows
    public Timestamp dateToTimeStamp( int year, int monthOfYear, int dayOfMonth){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse(dayOfMonth + "/" + monthOfYear + "/" + year);
        return new Timestamp(date);
    }

    //Update array hobbies in firebase
    public void updateHobbies(ArrayList<String> hobbies){
        //Delete old data in hobbies
        docRef.update(
                "hobbies", FieldValue.delete()
        );
        //Add new data to hobbies
        for (String s: hobbies) {
            docRef.update(
                    "hobbies", FieldValue.arrayUnion(s)
            );
        }
    }
}
