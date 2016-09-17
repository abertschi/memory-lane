package io.memorylane.model;

import java.io.File;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class Asset extends RealmObject {

    private Long id;

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

    public Asset(Long id, String path, Date createDate, Boolean isPicture) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;

        if (id != asset.id) return false;
        if (path != null ? !path.equals(asset.path) : asset.path != null) return false;
        if (createDate != null ? !createDate.equals(asset.createDate) : asset.createDate != null)
            return false;
        if (file != null ? !file.equals(asset.file) : asset.file != null) return false;
        return isPicture != null ? isPicture.equals(asset.isPicture) : asset.isPicture == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (isPicture != null ? isPicture.hashCode() : 0);
        return result;
    }
}
