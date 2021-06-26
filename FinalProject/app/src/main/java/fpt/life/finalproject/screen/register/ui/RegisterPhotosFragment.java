package fpt.life.finalproject.screen.register.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aminography.choosephotohelper.ChoosePhotoHelper;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.PhotoAdapter;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.Photo;

public class RegisterPhotosFragment extends Fragment {

    private static final int MAX_NUM_OF_PHOTOS = 9;
    private static final int NUM_OF_COLUMNS = 3;

    private View view;

    private RecyclerView recyclerViewPhotos;
    private Button buttonDone;
    private PhotoAdapter photoAdapter;

    private ChoosePhotoHelper choosePhotoHelper;

    private ArrayList<Photo> photos = new ArrayList<>();

    private RegistrationProfile registrationProfile;

    public RegisterPhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RegisterPhotosFragmentArgs args = RegisterPhotosFragmentArgs.fromBundle(getArguments());
            registrationProfile = args.getRegistrationProfile();
            Log.i("Check Arguments", "onViewCreated: " + registrationProfile.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_photos, container, false);

        initComponents();
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        photoAdapter = new PhotoAdapter(photos, getActivity(), choosePhotoHelper);
        recyclerViewPhotos.setAdapter(photoAdapter);
        recyclerViewPhotos.setLayoutManager(new GridLayoutManager(getContext(), NUM_OF_COLUMNS));
    }

    private void initComponents() {
        createPhotos();
        recyclerViewPhotos = view.findViewById(R.id.recycler_view_register_photos);
        buttonDone = view.findViewById(R.id.button_register_done);
    }

    private void createPhotos() {
        photos.clear();
        for (int i = 0; i < MAX_NUM_OF_PHOTOS; i++) {
            Photo photo = Photo.builder()
                    .photoUri("https://cdn.discordapp.com/attachments/684269879892901945/858280605409214474/unknown.png")
                    .isEmpty(false).build();
            photos.add(photo);
        }
    }
}