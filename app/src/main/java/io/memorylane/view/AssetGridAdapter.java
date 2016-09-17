package io.memorylane.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.memorylane.R;
import io.memorylane.model.Asset;
import io.memorylane.model.AssetsModel;

public class AssetGridAdapter extends RecyclerView.Adapter<AssetGridAdapter.AssetViewHolder> {

    private final Activity _mActivity;
    private AssetsModel assetsModel;

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
    public void onBindViewHolder(AssetGridAdapter.AssetViewHolder holder, int position) {
        Asset asset = assetsModel.getAssets().get(position);
        Glide.with(_mActivity)
                .load(asset.getPath())
                .centerCrop()
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return assetsModel.getAssets().size();
    }


    public class AssetViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public AssetViewHolder(final View parent) {
            super(parent);
            imageView = ((ImageView) parent.findViewById(R.id.asset_thumbnail));
        }

    }
}
