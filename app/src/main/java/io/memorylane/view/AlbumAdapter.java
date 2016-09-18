package io.memorylane.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

import io.memorylane.AlbumContentActivity;
import io.memorylane.MovieCreatorActivity;
import io.memorylane.R;
import io.memorylane.model.Album;
import io.memorylane.model.AlbumModel;
import io.realm.Realm;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final Activity _mActivity;
    private AlbumModel mModel;
    private Realm mRealm;

    public AlbumAdapter(AlbumModel model, Activity a) {
        mModel = model;
        _mActivity = a;
        //mRealm = Realm.getDefaultInstance();
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
        holder.mImage.setImageBitmap(getCoverBitmap(album));
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

            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Bundle bundle = ActivityOptionsCompat.makeClipRevealAnimation(v, (int) parent.getX(), (int) parent.getY(), 0, 0).toBundle();

                    Intent intent = new Intent(v.getContext(), MovieCreatorActivity.class);
                    Long id = mModel.getAlbums().get(getAdapterPosition()).getId();
                    intent.putExtra("AlbumId", id);
                    ActivityCompat.startActivity(AlbumAdapter.this._mActivity, intent, bundle);
                    return true;
                }
            });


        }
    }

    public Bitmap getCoverBitmap(Album album) {

        if(album.getAssets() == null || album.getAssets().size() ==0) {
            return null;
        }

        String coverPath = "", extension = "";
        do {
            int idx = new Random().nextInt(album.getAssets().size());
            coverPath = album.getAssets().get(idx).getPath();
            extension = coverPath.substring(coverPath.lastIndexOf('.') + 1);
        } while (!"jpg".equals(extension));
        Bitmap coverBitmap = BitmapFactory.decodeFile(coverPath);
        return coverBitmap;
    }
}
