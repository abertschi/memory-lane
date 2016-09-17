package io.memorylane.database;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Shodan on 17.09.16.
 */
public class DatabaseController {

    private DatabaseReference mDatabase;

    public DatabaseController(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addURL(Uri url){
            mDatabase.child("URL").setValue(url.toString());
    }

    public String getURL(){
        Query query = mDatabase.child("URL").limitToFirst(10);
        return query.toString();
    }

}
