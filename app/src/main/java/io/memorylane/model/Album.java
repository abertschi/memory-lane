package io.memorylane.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by abertschi on 17/09/16.
 */
public class Album extends RealmObject {

    private String name = "";
    private RealmList<Asset> assets;

    public Album() {}

    public String getName() {
        return name;
    }

    public Album setName(String name) {
        this.name = name;
        return this;
    }

    public RealmList<Asset> getAssets() {
        return assets;
    }

    public void setAssets(RealmList<Asset> assets) {
        this.assets = assets;
    }
}
