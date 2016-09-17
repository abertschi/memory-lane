package io.memorylane.model;

import java.io.File;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Asset extends RealmObject {

    private String path;

    private Date createDate;

    @Ignore
    private File file;

    public Asset() {}

    public Asset(File file) {
    }

    public Asset(String path, Date createDate) {
        this.file = new File(path);
        this.path = this.file.getPath();
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
