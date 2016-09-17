package io.memorylane.model;

import java.util.List;

public class AssetsModel {

    public List<Asset> assets;

    public AssetsModel() {
    }

    public AssetsModel(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}
