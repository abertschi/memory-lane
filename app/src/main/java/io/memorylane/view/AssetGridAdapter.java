package io.memorylane.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

import io.memorylane.R;
import io.memorylane.model.Asset;
import io.memorylane.model.AssetsModel;

public class AssetGridAdapter extends RecyclerView.Adapter<AssetGridAdapter.AssetViewHolder> {

    private final Activity _mActivity;
    private AssetsModel assetsModel;
    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

    public AssetGridAdapter(Activity _mActivity, AssetsModel assetsModel) {
        this._mActivity = _mActivity;
        this.assetsModel = assetsModel;
    }

    @Override
    public AssetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_card, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onViewRecycled(AssetGridAdapter.AssetViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.imageView);
        holder.imageView = null;
    }

    @Override
    public void onViewDetachedFromWindow(AssetGridAdapter.AssetViewHolder holder) {
        Glide.clear(holder.imageView);
        holder.imageView = null;
    }

    @Override
    public void onBindViewHolder(AssetGridAdapter.AssetViewHolder holder, int position) {
        Asset asset = assetsModel.getAssets().get(position);

        holder.numberLabel.setText("#" + ++position);
        holder.dateLabel.setText(fmt.format(asset.getCreateDate()));
        Log.i("TAG", "Loading image " + position);
        Log.i("TAG", asset.getPath());

        Glide
                .with(_mActivity)
                .load(asset.getPath())
                .centerCrop()
                .placeholder(R.mipmap.spinner)
                .crossFade()
                .into(holder.imageView);

//        Glide.with(_mActivity)
//                .load(asset.getPath())
//                .centerCrop()
//                .crossFade()
//                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return assetsModel.getAssets().size();
    }


    public class AssetViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView numberLabel;
        TextView dateLabel;

        public AssetViewHolder(final View parent) {
            super(parent);
            imageView = ((ImageView) parent.findViewById(R.id.asset_thumbnail));
            numberLabel = ((TextView) parent.findViewById(R.id.album_card_text));
            dateLabel = ((TextView) parent.findViewById(R.id.asset_card_date));
        }

    }
}
