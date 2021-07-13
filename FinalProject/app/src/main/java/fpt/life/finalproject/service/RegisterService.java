package fpt.life.finalproject.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.navigation.NavController;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpt.life.finalproject.MainActivity;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.User;
import fpt.life.finalproject.screen.register.ui.RegisterPhotosFragmentDirections;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class RegisterService {

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private RegistrationProfile registrationProfile;
    private LocationService locationService;

    private NavController navController;
    private Context context;

    private ArrayList<String> savedPhotoUrls = new ArrayList<>();
    private int counter;

    public RegisterService(RegistrationProfile registrationProfile, Context context) {
        this.registrationProfile = registrationProfile;
        this.context = context;
        this.storageReference = FirebaseStorage.getInstance().getReference("profile_photos");
        this.collectionReference = FirebaseFirestore.getInstance()
                .collection("users");
        this.locationService = new LocationService(context, registrationProfile.getUid());
    }

    @SneakyThrows
    public void saveUserDataToFireStore(ProgressDialog progressDialog) {
        Map<String, Integer> rangeAge = new HashMap<>();
        rangeAge.put("max", 50);
        rangeAge.put("min", 18);

        User user = User.builder()
                .uid(registrationProfile.getUid())
                .name(registrationProfile.getName())
                .birthday(new SimpleDateFormat("dd-MM-yyyy")
                        .parse(registrationProfile.getBirthday()))
                .city(registrationProfile.getCity())
                .bio(registrationProfile.getBio())
                .gender(registrationProfile.getGender())
                .showMeGender(registrationProfile.getShowMeGender())
                .hobbies((ArrayList<String>) registrationProfile.getHobbies())
                .rangeAge(rangeAge)
                .rangeDistance(5)
                .userDisliked(new ArrayList<>())
                .userLiked(new ArrayList<>())
                .build();
        Log.d("SaveUser", "onSaveUser: " + user.getUid());

        collectionReference.document(user.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SaveUser", "onSuccessSaveUser: " + user.getUid());
                        uploadImages(progressDialog);
                    }
                });
    }

    public void uploadImages(ProgressDialog progressDialog) {
        List<String> photos = registrationProfile.getPhotoUrls();
        counter = 0;
        savedPhotoUrls.clear();

        for (int i = 0; i < photos.size(); i++) {
            String photoUri = photos.get(i);
            Uri uri = Uri.parse(photoUri);
            Log.d("SaveUser", "uploadImages: " + registrationProfile.getUid());

            StorageReference childReference = storageReference
                    .child(registrationProfile.getUid() + "/" + uri.getLastPathSegment());
            Log.d("SaveUser", "uploadImages: " + uri.getLastPathSegment());
            childReference.putFile(uri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("SaveUser", "onSuccessSaveImage: " + uri.toString());
                    childReference.getDownloadUrl().addOnCompleteListener(downloadTask -> {
                        counter++;
                        if (downloadTask.isSuccessful()) {
                            savedPhotoUrls.add(downloadTask.getResult().toString());
                            Log.d("SaveUser", "onSuccessGetUrl: " + downloadTask.getResult().toString());
                        } else {
                            childReference.delete();
                            Log.d("SaveUser", "onFailureGetUrl: ");
                        }
                        Log.d("SaveUser", "uploadImages: " + counter + " - " + photos.size());
                        if (counter == photos.size()) {
                            saveImageDataToFireStore(progressDialog);
                        }
                    });
                } else {
                    counter++;
                    Log.d("SaveUser", "onFailureSaveImage: " + uri.toString());
//                    Toast.makeText(, "Couldn't upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImageDataToFireStore(ProgressDialog progressDialog) {
        collectionReference.document(registrationProfile.getUid())
                .update("photoUrls", savedPhotoUrls).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Log.d("SaveUser", "onSuccessSaveUrl: " + savedPhotoUrls.size());

            if (locationService.isProviderEnabled()) {
                locationService.getLastKnownLocation();
                navigateHomePage();
            } else
                navigateLocation();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Log.d("SaveUser", "onFailureSaveUrl: ");
        });
    }

    private void navigateLocation() {
        RegisterPhotosFragmentDirections.ActionFragmentRegisterPhotosToFragmentRegisterLocation action
                = RegisterPhotosFragmentDirections.actionFragmentRegisterPhotosToFragmentRegisterLocation(registrationProfile.getUid());
        navController.navigate(action);
    }

    private void navigateHomePage() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("uid", registrationProfile.getUid());
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
