package io.memorylane.model;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Asset extends RealmObject {

    private Long id;

    private String path;

    private Date createDate;

    @Ignore
    private File file;

    private Boolean isPicture;

    private Boolean isEnded;

    public Asset() {}

    public Asset(File file) {
    }

    public Asset(Boolean isPicutre, String path, Date createDate) {
        this.file = new File(path);
        this.path = this.file.getPath();
        this.createDate = createDate;
        this.isPicture = isPicutre;
        isEnded = false;
    }

    public Asset(Long id, String path, Date createDate, Boolean isPicture) {
        this(isPicture, path, createDate);
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isPicutre() {
        return isPicture;
    }

    public void setPicutre(Boolean picutre) {
        isPicture = picutre;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(path, ((Asset)o).getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}
