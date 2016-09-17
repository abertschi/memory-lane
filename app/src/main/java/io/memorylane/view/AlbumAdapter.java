package io.memorylane.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.memorylane.AlbumContentActivity;
import io.memorylane.R;
import io.memorylane.model.Album;
import io.memorylane.model.AlbumModel;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final Activity _mActivity;
    private AlbumModel mModel;

    public AlbumAdapter(AlbumModel model, Activity a) {
        mModel = model;
        _mActivity = a;
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

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        private TextView mLabel;
        private ImageView mImage;

        public AlbumViewHolder(final View parent) {
            super(parent);
            this.setIsRecyclable(false);
            mLabel = (TextView) parent.findViewById(R.id.album_card_text);
            mImage = (ImageView) parent.findViewById(R.id.album_card_image);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = ActivityOptionsCompat.makeClipRevealAnimation(v, (int) parent.getX(), (int) parent.getY(), 0, 0).toBundle();
                    ActivityCompat.startActivity(AlbumAdapter.this._mActivity, new Intent(v.getContext(), AlbumContentActivity.class), bundle);
                }
            });
        }
    }
}
