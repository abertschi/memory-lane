package io.memorylane.model;

import java.io.File;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Asset extends RealmObject {

    private String path;

    @Ignore
    private File file;

    public Asset() {}

    public Asset(File file) {
        this.path = file.getPath();
        this.file = file;
    }

    public Asset(String file) {
        this(new File(file));
    }

    public Date getCreateDate() {
        return new Date(file.lastModified());
    }
}
