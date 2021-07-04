package fpt.life.finalproject.screen.register.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.PhotoAdapter;
import fpt.life.finalproject.adapter.PhotoElementClickedListener;
import fpt.life.finalproject.adapter.RecyclerItemSelectedListener;
import fpt.life.finalproject.adapter.drapdrop.ItemTouchHelperCallBack;
import fpt.life.finalproject.adapter.drapdrop.OnStartDragListener;
import fpt.life.finalproject.dto.register.RegistrationProfile;
import fpt.life.finalproject.model.Photo;

public class RegisterPhotosFragment extends Fragment implements PhotoElementClickedListener, RecyclerItemSelectedListener {

    private static final int MAX_NUM_OF_PHOTOS = 9;
    private static final int NUM_OF_COLUMNS = 3;

    private View view;

    private RecyclerView recyclerViewPhotos;
    private Button buttonDone;
    private PhotoAdapter photoAdapter;
    private ItemTouchHelper itemTouchHelper;

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
        photoAdapter = new PhotoAdapter(photos, getActivity(), this, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        }, this);

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
            /*String uri = i > 4 ? null : "https://cdn.discordapp.com/attachments/684269879892901945/858280605409214474/unknown.png";
            uri = i % 2 == 0 && i <= 4? "https://cdn.discordapp.com/attachments/684269879892901945/859705192961146880/200934929_4041846202597440_3043409035639173606_n.png" : uri;*/
            //String uri = "https://cdn.discordapp.com/attachments/684269879892901945/858280605409214474/unknown.png";
            Photo photo = Photo.builder()
                    //.photoUri(uri)
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
        choosePhotoHelper = ChoosePhotoHelper.with(RegisterPhotosFragment.this)
                .asUri()
                .build(uri -> {
                    photo.setPhotoUri(uri.toString());
                    photo.setEmpty(false);
                    photoAdapter.notifyItemChanged(position);
                    Log.d("CheckPhotoPlus", photos.toString() + " " + position);
                });
        choosePhotoHelper.showChooser();
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