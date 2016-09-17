package io.memorylane.model;

import java.io.File;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Asset extends RealmObject {

    @PrimaryKey
    private long id;

    private String path;

    private Date createDate;

    @Ignore
    private File file;

    private Boolean isPicture;

    public Asset() {}

    public Asset(File file) {
    }

    public Asset(Boolean isPicutre, String path, Date createDate) {
        this.file = new File(path);
        this.path = this.file.getPath();
        this.createDate = createDate;
        this.isPicture = isPicutre;
    }

    public Asset(long id, String path, Date createDate, Boolean isPicture) {
        this.id = id;
        this.path = path;
        this.file = new File(path);
        this.createDate = createDate;
        this.isPicture = isPicture;
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
        return new File(path);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean isPicutre() {
        return isPicture;
    }

    public void setPicutre(Boolean picutre) {
        isPicture = picutre;
    }
}
