package io.memorylane.cloud;

import com.google.firebase.storage.*;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;

/**
 * Created by Shodan on 17.09.16.
 */
public class CloudController {

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public CloudController(String s /*hackzurich-cea1b.appspot.com/"*/){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://"+s);
    }

    public void putImage(String path){
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public void putMovie(String path){
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = storageRef.child("movies/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public void delMedia(){

    }

}
