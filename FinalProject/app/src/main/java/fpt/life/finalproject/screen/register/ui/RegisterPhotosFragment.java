package fpt.life.finalproject.screen.register.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.PhotoAdapter;
import fpt.life.finalproject.adapter.PhotoElementClickedListener;
import fpt.life.finalproject.adapter.RecyclerItemSelectedListener;
import fpt.life.finalproject.adapter.drapdrop.ItemTouchHelperCallBack;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.Photo;
import fpt.life.finalproject.service.RegisterService;
import fpt.life.finalproject.util.ButtonUtil;

public class RegisterPhotosFragment extends Fragment implements PhotoElementClickedListener, RecyclerItemSelectedListener {

    private static final int MAX_NUM_OF_PHOTOS = 9;
    private static final int NUM_OF_COLUMNS = 3;

    private View view;

    private RecyclerView recyclerViewPhotos;
    private Button buttonDone;
    private PhotoAdapter photoAdapter;
    private ItemTouchHelper itemTouchHelper;
    private ProgressDialog progressDialog;

    private ChoosePhotoHelper choosePhotoHelper;

    private ArrayList<Photo> photos = new ArrayList<>();

    private RegistrationProfile registrationProfile;

    private RegisterService registerService;

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
        initFireBase();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onClickButtonDone();
    }

    private void initFireBase() {
        registerService = new RegisterService(registrationProfile);
    }

    private void onClickButtonDone() {
        final NavController navController = Navigation.findNavController(view);
        registerService.setNavController(navController);

        buttonDone.setOnClickListener(v -> {
            registrationProfile.setPhotoUrls(registerPhotos());
            //registerService.navigateLocation();
            loadProgressDialog();
            registerService.saveUserDataToFireStore(progressDialog);
        });
        //Log.d("Upload Image", "UID: " + registrationProfile.getUid());
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private ArrayList<String> registerPhotos() {
        ArrayList<String> registerPhotos = new ArrayList<>();
        for (Photo photo : photos)
            if (!photo.isEmpty())
                registerPhotos.add(photo.getPhotoUri());
        return registerPhotos;
    }

    private void initRecyclerView() {
        photoAdapter = new PhotoAdapter(photos, getActivity(), this, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        }, this, buttonDone);

        recyclerViewPhotos.setAdapter(photoAdapter);
        recyclerViewPhotos.setLayoutManager(new GridLayoutManager(getContext(), NUM_OF_COLUMNS));

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(photoAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewPhotos);
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
                    .isEmpty(true)
                    .build();
            photos.add(photo);
        }
    }

    @Override
    public void onPhotoClickListener(int position) {
        onPlusClick(position);
    }

    @Override
    public void onItemClick(Object object) {
        onPlusClick(getFirstIndexOfNull());
    }

    private void onPlusClick(int position) {
        Photo photo = photos.get(position);
        ButtonUtil buttonUtil = ButtonUtil.builder().button(buttonDone).build();
        choosePhotoHelper = ChoosePhotoHelper.with(RegisterPhotosFragment.this)
                .asUri()
                .build(uri -> {
                    photo.setPhotoUri(uri.toString());
                    photo.setEmpty(false);
                    photoAdapter.notifyItemChanged(position);
                    Log.d("CheckPhotoPlus", photos.toString() + " " + position);
                    setButtonDone(buttonUtil);
                });
        choosePhotoHelper.showChooser();
        setButtonDone(buttonUtil);
    }

    private void setButtonDone(ButtonUtil buttonUtil) {
        buttonUtil.setFilled(!photos.get(0).isEmpty());
        buttonUtil.setButtonWhenFilled();
    }

    private int getFirstIndexOfNull() {
        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            if (photo.isEmpty())
                return i;
        }
        return -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}