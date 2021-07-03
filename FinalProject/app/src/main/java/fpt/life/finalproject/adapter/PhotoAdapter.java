package fpt.life.finalproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import fpt.life.finalproject.R;
import fpt.life.finalproject.adapter.drapdrop.ItemTouchHelperAdapter;
import fpt.life.finalproject.adapter.drapdrop.OnStartDragListener;
import fpt.life.finalproject.model.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private ArrayList<Photo> photos;
    private FragmentActivity fragmentActivity;
    private PhotoElementClickedListener photoElementClickedListener;
    private OnStartDragListener onStartDragListener;
    private RecyclerItemSelectedListener recyclerItemSelectedListener;

    private int height;

    public PhotoAdapter(ArrayList<Photo> photos, FragmentActivity fragmentActivity, PhotoElementClickedListener photoElementClickedListener, OnStartDragListener onStartDragListener, RecyclerItemSelectedListener recyclerItemSelectedListener) {
        this.photos = photos;
        this.fragmentActivity = fragmentActivity;
        this.photoElementClickedListener = photoElementClickedListener;
        this.onStartDragListener = onStartDragListener;
        this.recyclerItemSelectedListener = recyclerItemSelectedListener;
    }

    @Override
    public int getItemViewType(int position) {
        return photos.get(position).isEmpty() ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView;
        height = parent.getMeasuredHeight() / 3;

        if (viewType == 0) {
            photoView = inflater.inflate(R.layout.item_recycler_view_photo_plus, parent, false);
            return new ViewHolderPlus(photoView);
        }

        photoView = inflater.inflate(R.layout.item_recycler_view_photo_delete, parent, false);
        return new ViewHolderDelete(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        if (photo.isEmpty()) {
            ViewHolderPlus viewHolderPlus = (ViewHolderPlus) holder;
            RelativeLayout layoutPhoto = viewHolderPlus.item;
            ViewGroup.LayoutParams layoutParams = layoutPhoto.getLayoutParams();
            layoutParams.height = height;
            layoutPhoto.setLayoutParams(layoutParams);

            viewHolderPlus.item.setOnLongClickListener(v -> {
                onStartDragListener.onStartDrag(holder);
                return false;
            });
        } else {
            ViewHolderDelete viewHolderDelete = (ViewHolderDelete) holder;
            RelativeLayout layoutPhoto = viewHolderDelete.item;
            ViewGroup.LayoutParams layoutParams = layoutPhoto.getLayoutParams();
            layoutParams.height = height;
            layoutPhoto.setLayoutParams(layoutParams);

            ImageView imageViewPhoto = viewHolderDelete.itemImageViewPhoto;
            ImageView imageViewDelete = viewHolderDelete.itemImageViewDeletePhoto;

            imageViewDelete.setOnClickListener(v -> removePhoto(photo));

            imageViewPhoto.setOnClickListener(v -> photoElementClickedListener.onPhotoClickListener(position));

            Glide.with(fragmentActivity)
                    .load(photo.getPhotoUri())
                    .centerCrop()
                    .placeholder(R.drawable.background_add_photo)
                    .into(imageViewPhoto);

            viewHolderDelete.item.setOnLongClickListener(v -> {
                onStartDragListener.onStartDrag(holder);
                return false;
            });
        }
    }

    private void removePhoto(Photo photo) {
        int position = photos.indexOf(photo);
        photos.remove(position);
        notifyItemRemoved(position);
        photos.add(Photo.builder().isEmpty(true).build());
        notifyItemInserted(photos.size() - 1);
        //notifyDataSetChanged();
        Log.d("CheckPhotoDelete", photos.toString() + " " + position);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.d("CheckPhotoMove", photos.toString() + " " + fromPosition + " " + toPosition);
        Collections.swap(photos, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Log.d("CheckPhotoMove", photos.toString() + " " + fromPosition + " " + toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        photos.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolderDelete extends RecyclerView.ViewHolder {

        public ImageView itemImageViewPhoto, itemImageViewDeletePhoto;
        public RelativeLayout item;

        public ViewHolderDelete(@NonNull View itemView) {
            super(itemView);

            itemImageViewPhoto = itemView.findViewById(R.id.item_image_view_photo);
            itemImageViewDeletePhoto = itemView.findViewById(R.id.item_image_view_button_delete_photo);
            item = itemView.findViewById(R.id.layout_photo_delete);
        }
    }

    public class ViewHolderPlus extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout item;

        public ViewHolderPlus(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.layout_photo_plus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerItemSelectedListener.onItemClick(photos.get(getAdapterPosition()));
        }
    }
}
