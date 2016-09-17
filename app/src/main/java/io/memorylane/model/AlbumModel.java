package io.memorylane.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumModel {

    private List<Album> mAlbums = new ArrayList<>();


    public static AlbumModel createData() {
        AlbumModel m = new AlbumModel();
        Album a = new Album().setName("Trip to Zurich");
        m.addAlbum(a);
        a = new Album().setName("Trip to Bern");
        m.addAlbum(a);
        a = new Album().setName("Trip to Poland");
        m.addAlbum(a);
        return m;
    }

    public AlbumModel addAlbum(Album a) {
        this.mAlbums.add(a);
        return this;
    }


    public List<Album> getAlbums() {
        return mAlbums;
    }

    public AlbumModel setAlbums(List<Album> albums) {
        this.mAlbums = albums;
        return this;
    }
}
