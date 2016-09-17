package io.memorylane.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
        RealmResults<Album> result = Realm.getDefaultInstance().where(Album.class).findAll();
        return result.subList(0, result.size());
    }

    public AlbumModel setAlbums(List<Album> albums) {
        this.mAlbums = albums;
        return this;
    }
}
