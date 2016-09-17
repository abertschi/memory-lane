package io.memorylane.model;

/**
 * Created by abertschi on 17/09/16.
 */
public class Album {

    private String mName = "";

    public String getName() {
        return mName;
    }

    public Album setName(String name) {
        this.mName = name;
        return this;
    }
}
