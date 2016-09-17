package io.memorylane;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.memorylane.model.Album;
import io.memorylane.model.Asset;
import io.realm.Realm;
import io.realm.RealmList;

public class Utils {
    public static Date getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static Asset deepCopyAsset (Asset originalAsset) {
        return new Asset(originalAsset.getId(), originalAsset.getPath(), originalAsset.getCreateDate(),
                originalAsset.isPicutre());
    }

    public static Album deepCopy(final Album originalAlbum) {
        Album ret = new Album();
        ret.setId(originalAlbum.getId());
        ret.setStatDate(originalAlbum.getStatDate());
        ret.setEndDate(originalAlbum.getEndDate());
        ret.setName(originalAlbum.getName());

        RealmList<Asset> assets = new RealmList<>();
        for (Asset a: originalAlbum.getAssets()) {
            assets.add(deepCopyAsset(a));
        }
        ret.setAssets(assets);

        return ret;
    }

    public static Asset deepCopyAssetToRealm(Realm realm, Asset originalAsset) {
        Asset ret = realm.createObject(Asset.class);

        ret.setId(originalAsset.getId());
        ret.setFile(originalAsset.getFile());
        ret.setPath(originalAsset.getPath());
        ret.setCreateDate(originalAsset.getCreateDate());
        ret.setPicutre(originalAsset.isPicutre());
        return ret;
    }

    public static Album deepCopyToRealm(Realm realm, final Album originalAlbum) {
        Album ret = realm.createObject(Album.class);
        ret.setId(originalAlbum.getId());
        ret.setStatDate(originalAlbum.getStatDate());
        ret.setEndDate(originalAlbum.getEndDate());
        ret.setName(originalAlbum.getName());

        RealmList<Asset> assets = new RealmList<>();
        for (Asset a: originalAlbum.getAssets()) {
            assets.add(deepCopyAssetToRealm(realm, a));
        }
        ret.setAssets(assets);

        return ret;
    }

    public static RealmList<Asset> deepCopyToRealm(Realm realm, final List<Asset> assets) {

        RealmList<Asset> ret = new RealmList<>();

        for (Asset a:assets) {
            ret.add(deepCopyAssetToRealm(realm, a));
        }

        return ret;
    }
}
