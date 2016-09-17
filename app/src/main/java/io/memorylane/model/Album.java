package io.memorylane.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Album extends RealmObject {

    private String name;

    private Date statDate;

    private Date endDate;

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

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
