package io.memorylane.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.memorylane.R;
import io.memorylane.model.Album;
import io.memorylane.model.AlbumModel;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private AlbumModel mModel;

    public AlbumAdapter(AlbumModel model) {
        mModel = model;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = mModel.getAlbums().get(position);
        Log.i("TAG", album.getName());
        holder.mLabel.setText(album.getName());
    }

    @Override
    public int getItemCount() {
        return mModel.getAlbums().size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView mLabel;
        private ImageView mImage;

        public AlbumViewHolder(View parent) {
            super(parent);
            this.setIsRecyclable(false);
            mLabel = (TextView) parent.findViewById(R.id.album_card_text);
            mImage = (ImageView) parent.findViewById(R.id.album_card_image);
        }
    }
}
