package io.memorylane.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by abertschi on 17/09/16.
 */
public class AlbumModel {

    private List<Album> mAlbums = new ArrayList<>();

    public AlbumModel addAlbum(Album a) {
        this.mAlbums.add(a);
        return this;
    }

    public List<Album> getAlbums() {
        ArrayList<Album> albums = new ArrayList<>();
        RealmResults<Album> result = Realm.getDefaultInstance().where(Album.class).findAll();
        Album a = new Album();

        result.subList(0, result.size());
        albums.addAll(result);
        return albums;
    }

    public AlbumModel setAlbums(List<Album> albums) {
        this.mAlbums = albums;
        return this;
    }
}
