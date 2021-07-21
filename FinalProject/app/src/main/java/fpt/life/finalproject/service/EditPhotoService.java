package fpt.life.finalproject.service;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.Photo;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.screen.myprofile.EditPhoto_Activity;

public class EditPhotoService {

    private StorageReference storageReference;
    private CollectionReference collectionReference;
    private String uid;
    private ArrayList<String> savedPhotoUrls = new ArrayList<>();
    int counter;

    public EditPhotoService(String uid) {
        this.uid = uid;
        this.storageReference = FirebaseStorage.getInstance().getReference("profile_photos");
        this.collectionReference = FirebaseFirestore.getInstance()
                .collection("users");
    }

    public void uploadEditedImages(ArrayList<Photo> photos, ProgressDialog progressDialog) {
        savedPhotoUrls.clear();
        counter =0;
        for (int i = 0; i < photos.size(); i++) {
            savedPhotoUrls.add("");
        }

        for (int i = 0; i < photos.size(); i++) {
            int index = i;
            String photoUri = photos.get(i).getPhotoUri();
            Uri uri = Uri.parse(photoUri);
            StorageReference childReference = storageReference
                    .child(uid + "/" + uri.getLastPathSegment());
            if (photos.get(index).isExistFireBase()){
                counter++;
                savedPhotoUrls.set(i,photoUri);
                if (counter == photos.size()) {
                    saveImageDataToFireStore(progressDialog);
                }
            } else {
                childReference.putFile(uri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SaveUser", "onSuccessSaveImage: " + uri.toString());
                        childReference.getDownloadUrl().addOnCompleteListener(downloadTask -> {
                            counter++;
                            if (downloadTask.isSuccessful()) {
                                savedPhotoUrls.set(index,downloadTask.getResult().toString());
                                Log.d("SaveUser", "onSuccessGetUrl: " + downloadTask.getResult().toString());
                            } else {
                                childReference.delete();
                                Log.d("SaveUser", "onFailureGetUrl: ");
                            }
                            if (counter == photos.size()) {
                                saveImageDataToFireStore(progressDialog);
                            }
                        });
                    } else {
                        counter++;
                        Log.d("SaveUser", "onFailureSaveImage: " + uri.toString());
                    }
                });
            }
        }

    }

    private void saveImageDataToFireStore(ProgressDialog progressDialog) {
        collectionReference.document(uid)
                .update("photoUrls", savedPhotoUrls);
    }


}
