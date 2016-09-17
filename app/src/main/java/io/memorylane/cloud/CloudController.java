package io.memorylane.cloud;

import com.google.firebase.storage.*;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;

import io.memorylane.model.Asset;

public class CloudController {

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public CloudController(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://hackzurich-cea1b.appspot.com");
    }

    private UploadTask putAsset(String resource, Asset asset){
        Uri file = Uri.fromFile(asset.getFile());
        String fileName = Long.toString(System.currentTimeMillis()) + file.getLastPathSegment();

        StorageReference riversRef = storageRef.child(resource + fileName);
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("UPLOAD", e.getMessage(), e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("UPLOAD", "Upload successful");
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("UPLOAD", "link: " + downloadUrl);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("UPLOAD", "Upload is " + progress + "% done");
            }
        });
        return uploadTask;
    }

    public UploadTask putImage(Asset asset) {
        return putAsset("images/", asset);
    }

    public UploadTask putMovie(Asset asset){
        return putAsset("movies/", asset);
    }

}
