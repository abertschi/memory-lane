package io.memorylane.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import io.memorylane.R;
import io.memorylane.model.Asset;

public class MemoryLaneAsyncTask extends AsyncTask<Asset, Void, Boolean> {

    CloudController cloud;
    Activity activity;

    public MemoryLaneAsyncTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
        cloud = new CloudController();
    }

    @Override
    protected Boolean doInBackground(Asset... assets) {

        List<UploadTask> uploadTasks = new ArrayList<>();

        for (Asset asset : assets) {

            if(asset.isPicutre()){
                uploadTasks.add(cloud.putImage(asset));
            } else {
                uploadTasks.add(cloud.putMovie(asset));
            }
        }

        Task<Void> tasks = Tasks.whenAll(uploadTasks);

        // wait for competition
        try {
            Tasks.await(tasks);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("ERROR", e.getMessage(), e);
        }

        ConcurrentHashMap<Asset, String> uploadedAssets = cloud.getUploadedAssets();

        Log.i("TAG", uploadedAssets.toString());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
    }
}
