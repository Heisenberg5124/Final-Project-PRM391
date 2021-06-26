package fpt.life.finalproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fpt.life.finalproject.R;
import fpt.life.finalproject.model.Photo;
import fpt.life.finalproject.screen.register.ui.RegisterActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList<Photo> photos;
    private FragmentActivity fragmentActivity;

    private ChoosePhotoHelper choosePhotoHelper;

    private int height;

    public PhotoAdapter(ArrayList<Photo> photos, FragmentActivity fragmentActivity, ChoosePhotoHelper choosePhotoHelper) {
        this.photos = photos;
        this.fragmentActivity = fragmentActivity;
        this.choosePhotoHelper = choosePhotoHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View photoView = inflater.inflate(R.layout.item_recycler_view_photo, parent, false);
        height = parent.getMeasuredHeight() / 3;

        return new ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);

        RelativeLayout layoutPhoto = holder.layoutPhoto;
        ViewGroup.LayoutParams layoutParams = layoutPhoto.getLayoutParams();
        layoutParams.height = height;
        layoutPhoto.setLayoutParams(layoutParams);

        ImageView itemImageViewPhoto = holder.itemImageViewPhoto;
        ImageView itemImageViewButtonPlusPhoto = holder.itemImageViewPlusPhoto;
        ImageView itemImageViewButtonDeletePhoto = holder.itemImageViewDeletePhoto;

        if (!photo.isEmpty()) {
            itemImageViewPhoto.setBackgroundResource(0);
            Glide.with(fragmentActivity)
                    .load(photo.getPhotoUri())
                    .into(itemImageViewPhoto);
            itemImageViewButtonPlusPhoto.setVisibility(View.GONE);
            itemImageViewButtonDeletePhoto.setVisibility(View.VISIBLE);
        } else {
            itemImageViewButtonDeletePhoto.setVisibility(View.GONE);

            choosePhotoHelper = ChoosePhotoHelper.with(fragmentActivity)
                    .asFilePath()
                    .build(new ChoosePhotoCallback<String>() {
                        @Override
                        public void onChoose(String s) {
                            itemImageViewPhoto.setBackgroundResource(0);
                            Glide.with(fragmentActivity)
                                    .load(s)
                                    .into(itemImageViewPhoto);
                            notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImageViewPhoto, itemImageViewPlusPhoto, itemImageViewDeletePhoto;
        public RelativeLayout layoutPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageViewPhoto = itemView.findViewById(R.id.item_image_view_photo);
            itemImageViewPlusPhoto = itemView.findViewById(R.id.item_image_view_button_plus_photo);
            itemImageViewDeletePhoto = itemView.findViewById(R.id.item_image_view_button_delete_photo);
            layoutPhoto = itemView.findViewById(R.id.layout_photo);

            itemImageViewPhoto.setOnClickListener(v -> onAddClick());
            itemImageViewPlusPhoto.setOnClickListener(v -> onAddClick());
        }

        private void onAddClick() {
            choosePhotoHelper.showChooser();
        }
    }
}
